package lab.s2jh.module.ad.dao;

import lab.s2jh.core.dao.jpa.BaseDao;
import lab.s2jh.module.ad.entity.Advertising;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

@Repository
public interface AdvertisingDao extends BaseDao<Advertising, Long> {
    @Query("from Advertising")
    @QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    public List<Advertising> findAllCached();
    @Query("from Advertising where enable=? and checkedNum < checkNum and (fromDate < ? and toDate > ?) ")
    @QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    public List<Advertising> findByEnabledAndDateBetweenFromDateToDate(Boolean enable,Date fromDate,Date toDate);
}
