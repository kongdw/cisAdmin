package lab.s2jh.module.schedule.job;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.module.ad.entity.Advertising;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.AdvertisingService;
import lab.s2jh.module.ad.service.ProxyInfoService;
import lab.s2jh.module.schedule.BaseQuartzJobBean;
import lab.s2jh.module.schedule.entity.JobBeanCfg;
import lab.s2jh.module.schedule.service.ClickAdService;
import lab.s2jh.module.schedule.service.JobBeanCfgService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 贴吧广告点击作业
 */
@MetaData("贴吧广告点击作业")
@DisallowConcurrentExecution
public class ClickTiebaAdJob extends BaseQuartzJobBean {

    private final static Logger logger = LoggerFactory.getLogger(ClickTiebaAdJob.class);

    @Override
    protected String executeInternalBiz(JobExecutionContext context) {
        JobBeanCfgService jobBeanCfgService = getSpringBean(JobBeanCfgService.class);
        JobBeanCfg jobBeanCfg = jobBeanCfgService.findByJobClass(context.getJobDetail().getJobClass().getName());
        logger.debug("execute job :{}", jobBeanCfg.getJobClass());

        AdvertisingService advertisingService = getSpringBean(AdvertisingService.class);
        ClickAdService clickAdService = getSpringBean(ClickAdService.class);
        ProxyInfoService proxyInfoService = getSpringBean(ProxyInfoService.class);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -1);

        LinkedBlockingQueue<ProxyInfo> proxyQueue = (LinkedBlockingQueue<ProxyInfo>) getSpringBean("proxyQueue");
        if (proxyQueue.size() <= 0) {
            List<ProxyInfo> proxyInfoList = proxyInfoService.findByCheckFlagAndCheckTime(1, calendar.getTime());
            for (ProxyInfo proxyInfo : proxyInfoList) {
                proxyQueue.add(proxyInfo);
            }
        }

        List<Advertising> advertisingList = advertisingService.findByEnabledAndDateBetweenFromDateToDate(true, new Date());
        clickAdService.injectProxyInfo(proxyQueue.poll(), advertisingList.toArray(new Advertising[advertisingList.size()])).startClickAdSync();
        return "异步广告点击作业已提交！";
    }
}
