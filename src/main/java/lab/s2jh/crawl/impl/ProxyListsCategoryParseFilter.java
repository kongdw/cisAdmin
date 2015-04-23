package lab.s2jh.crawl.impl;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lab.s2jh.core.cons.GlobalConstant;
import lab.s2jh.crawl.filter.AbstractParseFilter;
import lab.s2jh.crawl.filter.ParseFilterChain;
import lab.s2jh.module.ad.entity.ProxyInfo;
import lab.s2jh.module.ad.service.ProxyInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * www.proxylists.net 分页抓取
 */
public class ProxyListsCategoryParseFilter extends AbstractParseFilter {

    private final Logger logger = LoggerFactory.getLogger(ProxyListsCategoryParseFilter.class);

    @Autowired
    private ProxyInfoService proxyInfoService;

    @Override
    public void doFilterInternal(String url, ParseFilterChain filterChain) throws Exception {
        // 将分页连接加入到list中
        HtmlPage htmlPage = fetchHtmlPage(url);
        HtmlElement ipTable = htmlPage.getFirstByXPath("//TABLE[@style='width:700px;']");
        String ips = ipTable.asText().trim();
        Pattern pattern = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))\\s+\\d{1,4}\\s+[0-9a-zA-Z]+\\s+[0-9a-zA-Z]+");
        Matcher matcher = pattern.matcher(ips);
        while (matcher.find()){
            String proxyStr = matcher.group();
            String[] proxyArray = proxyStr.split("\\s+");
            ProxyInfo proxyInfo = new ProxyInfo();
            proxyInfo.setIp(proxyArray[0]);
            proxyInfo.setPort(Integer.valueOf(proxyArray[1]));
            GlobalConstant.ProxyType proxyType = GlobalConstant.ProxyType.valueOf(proxyArray[2]);
            if (proxyType != null)
                proxyInfo.setProxyType(proxyType);
            else
                proxyInfo.setProxyType(GlobalConstant.ProxyType.Other);
            proxyInfo.setCrawlTime(new Date());
            ProxyInfo temp = proxyInfoService.findByIpAndPort(proxyInfo.getIp(),proxyInfo.getPort());
            if(temp == null)
                proxyInfoService.save(proxyInfo);
            logger.debug("Ip Info: {}",proxyStr);
        }
        pattern = Pattern.compile("cn_([1-9]{1,3})_ext.html");
        matcher = pattern.matcher(ipTable.asXml());
        while (matcher.find()){
            crawlService.injectUrls("http://www.proxylists.net/"+matcher.group());
        }
    }

}
