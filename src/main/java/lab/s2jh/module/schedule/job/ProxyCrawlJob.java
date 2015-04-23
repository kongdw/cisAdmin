package lab.s2jh.module.schedule.job;

import lab.s2jh.core.annotation.MetaData;
import lab.s2jh.crawl.service.CrawlService;
import lab.s2jh.module.schedule.BaseQuartzJobBean;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/**
 * 代理服务器抓取作业
 */
@MetaData("代理服务器抓取作业")
@DisallowConcurrentExecution
public class ProxyCrawlJob extends BaseQuartzJobBean {

    @Override
    protected String executeInternalBiz(JobExecutionContext context) {
        CrawlService crawlService = getSpringBean(CrawlService.class);
        crawlService.injectUrls("http://www.proxylists.net/cn_0_ext.html").startCrawlSync();
        return "代理服务器抓取完成!";
    }


}
