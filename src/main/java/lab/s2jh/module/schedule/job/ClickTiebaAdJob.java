package lab.s2jh.module.schedule.job;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.module.ad.entity.Advertising;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.AdvertisingService;
import lab.s2jh.module.schedule.BaseQuartzJobBean;
import lab.s2jh.module.schedule.entity.JobBeanCfg;
import lab.s2jh.module.schedule.service.ClickAdService;
import lab.s2jh.module.schedule.service.JobBeanCfgService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        AdvertisingService advertisingService = getSpringBean(AdvertisingService.class);
        ClickAdService clickAdService = getSpringBean(ClickAdService.class);
        Object obj = getSpringBean("proxyQueue");
        LinkedBlockingQueue<ProxyInfo> proxyInfos = null;
        if (obj instanceof LinkedBlockingQueue) {
            proxyInfos = (LinkedBlockingQueue<ProxyInfo>) obj;
        }else {
            logger.error("无法获取到代理队列[proxyQueue]，请检查spring配置文件!");
            return "无法获取到代理队列[proxyQueue]，请检查spring配置文件!";
        }
        JobBeanCfg jobBeanCfg = jobBeanCfgService.findByJobClass(context.getJobDetail().getJobClass().getName());
        logger.debug("execute job :{}", jobBeanCfg.getJobClass());
        List<Advertising> advertisings = advertisingService.findByEnabledAndDateBetweenFromDateToDate(true, new Date());
        for (Advertising ad : advertisings) {
            clickAdService.injectProxyInfo(proxyInfos.poll(), ad);
            return "成功完成一次点击!";
        }
        return "未找到需要点击的广告!";
    }
}
