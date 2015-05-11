package lab.s2jh.module.schedule.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lab.s2jh.crawl.service.HtmlunitService;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.ProxyInfoService;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.execchain.MinimalClientExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 异步校验代理
 */

@Service
public class AsyncCheckProxyService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCheckProxyService.class);

    public String checkUrl = "http://tieba.baidu.com";
    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }
    @Autowired
    private ProxyInfoService proxyInfoService;

    @Autowired
    @Qualifier("proxyQueue")
    private LinkedBlockingQueue<ProxyInfo> proxyQueue;

    @Async("checkTaskExecutor")
    public Future<String> startAsyncCheck(ProxyInfo proxyInfo) {
        String result = "";
        try {
            HtmlPage htmlPage =  HtmlunitService.fetchHtmlPage(checkUrl, null, null, getProxyConfig(proxyInfo), false);
            if (htmlPage.getWebResponse().getStatusCode() == 200){
                proxyInfo.setCheckFlag(1);
                proxyQueue.add(proxyInfo);
                result = "is success !";
            }else {
                proxyInfo.setCheckFlag(0);
                result = "is invalid !";
            }

        } catch (Exception e) {
            proxyInfo.setCheckFlag(0);
            result = e.getCause().getMessage();
        }
        proxyInfo.setCheckTime(new Date());
        proxyInfoService.save(proxyInfo);
        logger.info("{}:{} {}",proxyInfo.getIp(),proxyInfo.getPort(), result);
        return new AsyncResult<String>(result);
    }

    private static ProxyConfig getProxyConfig(ProxyInfo proxyInfo) {
        ProxyConfig proxyConfig = new ProxyConfig();
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
        return proxyConfig;
    }
}
