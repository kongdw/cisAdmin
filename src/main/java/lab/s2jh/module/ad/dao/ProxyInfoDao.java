package lab.s2jh.module.ad.dao;

import lab.s2jh.core.dao.jpa.BaseDao;
import lab.s2jh.module.ad.entity.ProxyInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface ProxyInfoDao extends BaseDao<ProxyInfo, Long> {
    @Query("from ProxyInfo")
    @QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    public List<ProxyInfo> findAllCached();

    public ProxyInfo findByIpAndPort(String ip,Integer port);
//    @Query("from ProxyInfo where enable=? and checkedNum < checkNum and (fromDate < ? and toDate > ?) ")
//    @QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
//    public List<ProxyInfo> findByEnabledAndDateBetweenFromDateToDate(Boolean enable, Date fromDate, Date toDate);
}
