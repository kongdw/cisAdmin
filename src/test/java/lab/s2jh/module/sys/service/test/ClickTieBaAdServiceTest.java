package lab.s2jh.module.sys.service.test;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lab.s2jh.core.test.SpringTransactionalTestCase;
import lab.s2jh.core.util.DateUtils;
import lab.s2jh.crawl.service.CrawlService;
import lab.s2jh.crawl.service.HtmlunitService;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.AdvertisingService;
import lab.s2jh.module.ad.service.ProxyInfoService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ClickTieBaAdServiceTest extends SpringTransactionalTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ClickTieBaAdServiceTest.class);

    @Autowired
    private ProxyInfoService proxyInfoService;

    @Autowired
    private AdvertisingService advertisingService;

    @Autowired
    private CrawlService crawlService;

    @Test
    public void clickAdvertising() {
        List<ProxyInfo> proxyInfoList = proxyInfoService.findByCheckFlagAndCheckTime(1, DateUtils.parseDate("2015-05-01", DateUtils.DEFAULT_DATE_FORMATER));
        for (ProxyInfo proxyInfo : proxyInfoList) {
            HtmlPage page = HtmlunitService.fetchHtmlPage("http://tieba.baidu.com/f?ie=utf-8&kw=%E7%AC%94%E8%AE%B0%E6%9C%AC",null,null,getProxyConfig(proxyInfo),false);
            page.getWebResponse();
        }
        logger.info("find {} to result.", proxyInfoList.size());
    }

    private ProxyConfig getProxyConfig(ProxyInfo proxyInfo) {
        ProxyConfig proxyConfig = null;
        if (proxyInfo != null) {
            proxyConfig = new ProxyConfig();
            proxyConfig.setProxyHost(proxyInfo.getIp());
            proxyConfig.setProxyPort(proxyInfo.getPort());
            switch (proxyInfo.getProxyType()) {
                case Anonymous:
                case Transparent:
                case Distorting:
                case Other:
                    proxyConfig.setSocksProxy(false);
                    break;
                case Socks5:
                case Socks4:
                    proxyConfig.setSocksProxy(true);
                    break;
            }
        }
        return proxyConfig;
    }
}
