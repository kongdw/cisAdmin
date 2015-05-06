package lab.s2jh.module.schedule.job;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.crawl.service.CrawlService;
import lab.s2jh.module.schedule.BaseQuartzJobBean;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/**
 * 抓取代理作业
 */
@MetaData("抓取代理作业")
@DisallowConcurrentExecution
public class CrawlProxyJob extends BaseQuartzJobBean {
    @Override
    protected String executeInternalBiz(JobExecutionContext context) {
        CrawlService crawlService = getSpringBean(CrawlService.class);
        crawlService.injectUrls("http://www.proxylists.net/cn_0_ext.html");
        return "异步抓取代理作业提交完毕！";
    }
}
