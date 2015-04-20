package lab.s2jh.core.security;

import java.util.List;

import javax.annotation.PostConstruct;

import lab.s2jh.module.auth.entity.Privilege;
import lab.s2jh.module.auth.entity.Role;
import lab.s2jh.module.auth.entity.User;
import lab.s2jh.module.auth.entity.User.AuthTypeEnum;
import lab.s2jh.module.auth.service.UserService;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class ShiroJdbcRealm extends AuthorizingRealm {

    private PasswordService passwordService;

    private UserService userService;

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        SourceUsernamePasswordToken token = (SourceUsernamePasswordToken) authcToken;

        String username = token.getUsername();
        User authAccount = userService.findByAuthTypeAndAuthUid(User.AuthTypeEnum.SYS, username);
        if (authAccount == null) {
            return null;
        }

        //把盐值注入到用户输入密码，用于后续加密算法使用
        token.setPassword(passwordService.injectPasswordSalt(String.valueOf(token.getPassword()),
                authAccount.getAuthGuid()).toCharArray());

        //构造权限框架认证用户信息对象
        AuthUserDetails authUserDetails = new AuthUserDetails();
        authUserDetails.setAuthGuid(authAccount.getAuthGuid());
        authUserDetails.setAuthType(AuthTypeEnum.SYS);
        authUserDetails.setAuthUid(authAccount.getAuthUid());
        authUserDetails.setNickName(authAccount.getNickName());
        authUserDetails.setSource(token.getSource());

        return new SimpleAuthenticationInfo(authUserDetails, authAccount.getPassword(), "Shiro JDBC Realm");
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        AuthUserDetails authUserDetails = (AuthUserDetails) principals.getPrimaryPrincipal();
        User user = userService.findByAuthTypeAndAuthUid(authUserDetails.getAuthType(), authUserDetails.getAuthUid());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        if (Boolean.TRUE.equals(user.getMgmtGranted())) {
            //管理端登录来源
            info.addRole(AuthUserDetails.ROLE_MGMT_USER);
        } else {
            // 普通前端登录来源
            info.addRole(AuthUserDetails.ROLE_SITE_USER);
        }

        //查询用户角色列表
        List<Role> userRoles = userService.findRoles(user);
        for (Role role : userRoles) {
            info.addRole(role.getCode());
        }

        //处理绑定用户
        User bindToUser = user.getBindTo();
        if (bindToUser != null) {
            List<Role> bindToUserRoles = userService.findRoles(bindToUser);
            for (Role role : bindToUserRoles) {
                info.addRole(role.getCode());
            }
        }

        //超级管理员特殊处理
        for (String role : info.getRoles()) {
            if (AuthUserDetails.ROLE_SUPER_USER.equals(role)) {
                //追加超级权限配置
                info.addStringPermission("*");
                break;
            }
        }

        //基于当前用户所有角色集合获取有效的权限集合
        List<Privilege> privileges = userService.findPrivileges(info.getRoles());
        for (Privilege privilege : privileges) {
            info.addStringPermission(privilege.getCode());
        }

        return info;
    }

    public AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
        return super.getAuthorizationInfo(principals);
    }

    /**
     * 设定Password校验的Hash算法与迭代次数.
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(PasswordService.HASH_ALGORITHM);
        setCredentialsMatcher(matcher);
    }

    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
