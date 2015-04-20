package lab.s2jh.module.auth.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.NotAudited;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_MgmtUser")
@MetaData(value = "管理端用户信息")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MgmtUser extends BaseNativeEntity {

    private static final long serialVersionUID = 512335968914828057L;

    @MetaData(value = "登录账号对象")
    private User user;

    @MetaData(value = "所属部门")
    private Department department;

    @Override
    @Transient
    public String getDisplay() {
        return user.getDisplay();
    }

    @NotAudited
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "department_id")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @NotAudited
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
