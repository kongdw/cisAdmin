package lab.s2jh.module.schedule.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.ProxyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代理异步校验
 */

@Service
public class AsyncProxyCheckService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncProxyCheckService.class);

    @Autowired
    private ProxyInfoService proxyInfoService;

    /**
     * 异步执行URL集合数据抓取
     */
    @Async("checkTaskExecutor")
    public Future<String> startAsyncCheck(ProxyInfo proxyInfo) {
        Pattern pattern = Pattern.compile("0110516");
        boolean checkFlag = check(proxyInfo, pattern);
        String result;
        if (checkFlag) {
            proxyInfo.setCheckFlag(1);
            result = proxyInfo.getIp() + ":" + proxyInfo.getPort() + "is success !";
        } else {
            proxyInfo.setCheckFlag(0);
            result = proxyInfo.getIp() + ":" + proxyInfo.getPort() + "is failed !";
        }
        proxyInfo.setCheckTime(new Date());
        proxyInfoService.save(proxyInfo);
        return new AsyncResult<String>(result);
    }

    private static boolean check(ProxyInfo proxyInfo, Pattern pattern) {
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

        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setProxyConfig(proxyConfig);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setRedirectEnabled(true);
        try {
            HtmlPage page = webClient.getPage("http://tieba.baidu.com");
            if (page != null) {
                String pageXml = page.asXml();
                Matcher matcher = pattern.matcher(pageXml);
                return matcher.find();
            }

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return false;
    }

}
