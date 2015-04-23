package lab.s2jh.module.sys.service.test;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Maps;
import lab.s2jh.core.test.Unicode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaCheck {
    public static void main(String[] args) throws IOException {
        String url = "http://tieba.baidu.com/f?ie=utf-8&kw=%E5%9B%BE%E6%8B%89%E4%B8%81";
        String adTitle = " 狂促！奔腾双核+GT730独显+19.5LED液晶显示器惊爆2799元！ ";
        WebClient webClient = buildWebClient();
        printCookies(webClient);
        WebRequest request = new WebRequest(new URL(url));
        request.setHttpMethod(HttpMethod.GET);
        request.setAdditionalHeaders(getAdditionalHeaders());
        printAdditionalHeaders(request);
        HtmlPage page = webClient.getPage(request);
        printAdditionalHeaders(request);
        printResponseHeaders(page.getWebResponse());
        printCookies(webClient);
        List<String> adList = getAdJsonList(page, adTitle);

    }

    public static WebClient buildWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.waitForBackgroundJavaScript(60 * 1000);
        return webClient;
    }

    public static Map<String, String> getAdditionalHeaders() {
        Map<String, String> additionalHeaders = Maps.newHashMap();
        additionalHeaders.put("Connection", "keep-alive");
        additionalHeaders.put("Host", "tieba.baidu.com\n");
        additionalHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        additionalHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        additionalHeaders.put("Accept-Encoding", "gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        return additionalHeaders;
    }

    public static void printAdditionalHeaders(WebRequest request) {
        System.out.println("----------------------------------------------------");
        Map<String, String> additionalHeaders = request.getAdditionalHeaders();
        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            System.out.println(String.format("--------Request Header key: %s value: %s ", entry.getKey(), entry.getValue()));
        }
    }

    public static void printResponseHeaders(WebResponse response) {
        System.out.println("----------------------------------------------------");
        List<NameValuePair> nameValuePairList = response.getResponseHeaders();
        for (NameValuePair n : nameValuePairList) {
            System.out.println(String.format("--------Response Header name: %s value: %s ", n.getName(), n.getValue()));
        }
    }

    public static List<String> getAdJsonList(HtmlPage htmlPage, String adTitle) {

        List<String> adList = new ArrayList<String>();
        String html = htmlPage.asXml();
        Pattern pattern = Pattern.compile("\\{.*adData.*\\}");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String adStr = Unicode.unicodeToString(matcher.group());
            if (adStr.contains(adTitle.trim())) {
                adList.add(matcher.group());
                System.out.println(String.format("----------广告连接为:%s", matcher.group()));
            }
        }
        System.out.println(String.format("----------解析网页中的广告条数为%d", adList.size()));
        return adList;
    }


    public static void printCookies(WebClient webClient) {
        System.out.println("----------------------------------------------------");
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(String.format("--------Cookie domain: %s name: %s value:%s ", cookie.getDomain(), cookie.getName(), cookie.getValue()));
        }
    }
}
