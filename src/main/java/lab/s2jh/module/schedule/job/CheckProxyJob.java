package lab.s2jh.module.schedule.job;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.ProxyInfoService;
import lab.s2jh.module.schedule.BaseQuartzJobBean;
import lab.s2jh.module.schedule.entity.JobBeanCfg;
import lab.s2jh.module.schedule.service.CheckProxyService;
import lab.s2jh.module.schedule.service.JobBeanCfgService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 校验代理作业
 */
@MetaData("校验代理作业")
public class CheckProxyJob extends BaseQuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(CheckProxyJob.class);

    @Override
    protected String executeInternalBiz(JobExecutionContext context) {
        JobBeanCfgService jobBeanCfgService = getSpringBean(JobBeanCfgService.class);
        JobBeanCfg jobBeanCfg = jobBeanCfgService.findByJobClass(context.getJobDetail().getJobClass().getName());
        logger.debug("execute job :{}", jobBeanCfg.getJobClass());

        ProxyInfoService proxyInfoService = getSpringBean(ProxyInfoService.class);
        CheckProxyService checkService = getSpringBean(CheckProxyService.class);
        List<ProxyInfo> proxyInfoList = proxyInfoService.findAllCached();
        logger.debug("find proxy size {}",proxyInfoList.size());
        checkService.injectProxyInfo(proxyInfoList.toArray(new ProxyInfo[proxyInfoList.size()]));
        return "异步校验代理作业提交完毕！";
    }
}
