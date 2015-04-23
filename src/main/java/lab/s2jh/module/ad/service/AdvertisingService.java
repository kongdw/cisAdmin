package lab.s2jh.module.ad.service;

import lab.s2jh.core.dao.jpa.BaseDao;
import lab.s2jh.core.service.BaseService;
import lab.s2jh.module.ad.dao.AdvertisingDao;
import lab.s2jh.module.ad.entity.Advertising;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AdvertisingService extends BaseService<Advertising, Long> {

    @Autowired
    private AdvertisingDao advertisingDao;

    @Override
    protected BaseDao<Advertising, Long> getEntityDao() {
        return advertisingDao;
    }

    @Transactional(readOnly = true)
    public List<Advertising> findAllCached() {
        return advertisingDao.findAllCached();
    }

    @Transactional(readOnly = true)
    public List<Advertising> findByEnabledAndDateBetweenFromDateToDate(Boolean enable, Date currentDate) {
        return advertisingDao.findByEnabledAndDateBetweenFromDateToDate(enable, currentDate, currentDate);
    }

}
