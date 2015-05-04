package lab.s2jh.module.schedule.job;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.crawl.service.CrawlService;
import lab.s2jh.module.ad.entity.Advertising;
import lab.s2jh.module.ad.service.AdvertisingService;
import lab.s2jh.module.schedule.BaseQuartzJobBean;
import lab.s2jh.module.schedule.entity.JobBeanCfg;
import lab.s2jh.module.schedule.service.JobBeanCfgService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * 贴吧广告点击作业
 */
@MetaData("贴吧广告点击作业")
@DisallowConcurrentExecution
public class TiebaAdCheckJob extends BaseQuartzJobBean {

    private final static Logger logger = LoggerFactory.getLogger(TiebaAdCheckJob.class);

    @Override
    protected String executeInternalBiz(JobExecutionContext context) {
        JobBeanCfgService jobBeanCfgService = getSpringBean(JobBeanCfgService.class);
        AdvertisingService advertisingService = getSpringBean(AdvertisingService.class);
        JobBeanCfg jobBeanCfg = jobBeanCfgService.findByJobClass(context.getJobDetail().getJobClass().getName());
        logger.debug("execute job :{}", jobBeanCfg.getJobClass());
        List<Advertising> advertisings = advertisingService.findByEnabledAndDateBetweenFromDateToDate(true, new Date());
        for (Advertising ad : advertisings) {

            int checkedNum = ad.getCheckedNum();
            ad.setCheckedNum(checkedNum + 1);
            advertisingService.save(ad);
        }
        return "成功完成一次点击!";
    }
}
