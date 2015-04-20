package lab.s2jh.module.auth.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
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

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_RoleR2Privilege")
@MetaData(value = "角色与权限关联")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleR2Privilege extends BaseNativeEntity {

    private static final long serialVersionUID = -4312077296555510354L;

    /** 关联权限对象 */
    @ManyToOne
    @JoinColumn(name = "privilege_id", nullable = false)
    private Privilege privilege;

    /** 关联角色对象 */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Transient
    @Override
    public String getDisplay() {
        return privilege.getDisplay() + "_" + role.getDisplay();
    }
}
