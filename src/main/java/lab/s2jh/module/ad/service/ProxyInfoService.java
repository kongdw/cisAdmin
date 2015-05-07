package lab.s2jh.module.ad.service;

import lab.s2jh.core.dao.jpa.BaseDao;
import lab.s2jh.core.service.BaseService;
import lab.s2jh.module.ad.dao.AdvertisingDao;
import lab.s2jh.module.ad.dao.ProxyInfoDao;
import lab.s2jh.module.ad.entity.Advertising;
import lab.s2jh.module.ad.entity.ProxyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProxyInfoService extends BaseService<ProxyInfo, Long> {

    @Autowired
    private ProxyInfoDao proxyInfoDao;

    @Override
    protected BaseDao<ProxyInfo, Long> getEntityDao() {
        return proxyInfoDao;
    }

    @Transactional(readOnly = true)
    public List<ProxyInfo> findAllCached() {
        return proxyInfoDao.findAllCached();
    }

    @Transactional(readOnly = true)
    public ProxyInfo findByIpAndPort(String ip, Integer port) {
        return proxyInfoDao.findByIpAndPort(ip, port);

    }

    @Transactional(readOnly = true)
    public List<ProxyInfo> findByCheckFlagAndCheckTime(Integer checkFlag, Date checkTime) {
        return proxyInfoDao.findByCheckFlagAndCheckTime(checkFlag, checkTime);
    }

}
