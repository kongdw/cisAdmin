package lab.s2jh.module.sys.service.test;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Maps;
import lab.s2jh.core.test.Unicode;
import lab.s2jh.crawl.htmlunit.ExtJavaScriptErrorListener;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaCheck {
    public static void main(String[] args) {
        String url = "http://tieba.baidu.com/f?ie=utf-8&kw=%E5%9B%BE%E6%8B%89%E4%B8%81";
        String adTitle = "贴吧会员周年庆，绝版星座王霸气来袭";
        try {
            WebClient webClient = buildWebClient();
            WebRequest request = buildWebRequest(url);
            request.setHttpMethod(HttpMethod.GET);
            request.setAdditionalHeaders(getAdditionalHeaders());
            printAdditionalHeaders(request);
            HtmlPage page = webClient.getPage(request);
            printAdditionalHeaders(request);
            printResponseHeaders(page.getWebResponse());
            printCookies(webClient);
            List<String> adList = getAdJsonList(page, adTitle);
            for(String adstr : adList){
                verifyAd(adstr);
                checkAd(adstr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WebClient buildWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setJavaScriptErrorListener(new ExtJavaScriptErrorListener());
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.waitForBackgroundJavaScript(60 * 1000);
        return webClient;
    }

    public static WebRequest buildWebRequest(String url) throws MalformedURLException {
       return buildWebRequest(url,null);
    }

    public static WebRequest buildWebRequest(String url,Map<String, String> headers) throws MalformedURLException {
        return buildWebRequest(url,headers,null);
    }

    public static WebRequest buildWebRequest(String url,Map<String, String> headers,List<NameValuePair> parameters) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(url));
        if(parameters != null && parameters.size()>0){
            webRequest.setRequestParameters(parameters);
        }
        if(headers!= null && headers.size() > 0){
            webRequest.setAdditionalHeaders(headers);
        }
        return webRequest;
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

    public static boolean verifyAd(String adData) throws ParseException, MalformedURLException {
        String baseUrl ="http://tieba.baidu.com/billboard/pushlog/";
        WebRequest webRequest = buildWebRequest(baseUrl);
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        NameValuePair parameter = new NameValuePair("t",String.valueOf(new Date().getTime()));
        /*
        t:1429865028822
        client_type:pc_web
        task:tbda
        page:frs
        fid:74075
        tid:
        uid:43415994
        da_task:tbda
        da_fid:74075
        da_tid:
        da_uid:43415994
        da_page:frs
        da_type_id:0002
        da_obj_id:13060
        da_good_id:21786
        da_obj_name:WCA报名FRS15
        da_first_name:游戏
        da_second_name:游戏
        da_cpid:4
        da_abtest:
        da_price:100
        da_verify:465a117c81d06d37629d6cde203a083a
        da_plan_id:1
        da_ext_info:1_0_0_0_1_0_0_0
        da_client_type:PC
        da_locate:15
        da_type:show
        */
        JSONParser jsonParser = new JSONParser();
        Object o = jsonParser.parse(adData.replace("'","\""));
        return false;
    }

    public static boolean checkAd(String adData) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        Object o = jsonParser.parse(adData.replace("'","\""));
        return false;
    }
}
