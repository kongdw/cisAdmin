package lab.s2jh.module.sys.service.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Maps;
import com.nebhale.jsonpath.JsonPath;
import lab.s2jh.core.test.Unicode;
import lab.s2jh.core.util.JsonUtils;
import org.apache.commons.codec.net.URLCodec;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaCheck {

    private static WebClient webClient = buildWebClient();
    private static String adUrl = "http://tieba.baidu.com/f?ie=utf-8&kw=%E7%AC%94%E8%AE%B0%E6%9C%AC";

    public static void main(String[] args) {
        String adTitle = "为游戏而生！GTX970M战神Z7彩色版7499元“好色”首发！";
        try {
            WebRequest request = buildWebRequest(adUrl);
            request.setHttpMethod(HttpMethod.GET);
            request.setAdditionalHeaders(getAdditionalHeaders(adUrl));
            printAdditionalHeaders(request);
            HtmlPage page = webClient.getPage(request);
            printAdditionalHeaders(request);
            printResponseHeaders(page.getWebResponse());
            printCookies(webClient);
            List<String> adList = getAdJsonList(page, adTitle);
            for (String adstr : adList) {
                verifyAd(getVerifyUrl(adstr, true),true);
                verifyAd(getVerifyUrl(adstr, false),false);
                System.out.println("广告点击: "+checkAd(adstr,"神舟"));
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
         webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.waitForBackgroundJavaScript(60 * 1000);
        return webClient;
    }

    public static WebRequest buildWebRequest(String url) throws MalformedURLException {
        return buildWebRequest(url, null);
    }

    public static WebRequest buildWebRequest(String url, Map<String, String> headers) throws MalformedURLException {
        return buildWebRequest(url, headers, null);
    }

    public static WebRequest buildWebRequest(String url, Map<String, String> headers, List<NameValuePair> parameters) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(url));
        if (parameters != null && parameters.size() > 0) {
            webRequest.setRequestParameters(parameters);
        }
        if (headers != null && headers.size() > 0) {
            webRequest.setAdditionalHeaders(headers);
        }
        return webRequest;
    }

    public static Map<String, String> getAdditionalHeaders(String refUrl) {
        Map<String, String> additionalHeaders = Maps.newHashMap();
        additionalHeaders.put("Connection", "keep-alive");
        additionalHeaders.put("Host", "tieba.baidu.com");
        additionalHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        additionalHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        additionalHeaders.put("Accept-Encoding", "gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        if (refUrl != null && refUrl.length() > 0)
            additionalHeaders.put("Referer", refUrl);
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
                adList.add(matcher.group().replaceAll("(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/", ""));
                System.out.println(String.format("----------广告连接为:%s", matcher.group().replaceAll("(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/", "")));
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

    public static boolean verifyAd(String verifyUrl, boolean isTrack) throws ParseException, IOException {
        Map<String, String> additionalHeaders = Maps.newHashMap();
        additionalHeaders.put("Accept", "image/webp,*/*;q=0.8");
        additionalHeaders.put("Accept-Encoding", "gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        additionalHeaders.put("Cache-Control", "max-age=0");
        additionalHeaders.put("Connection", "keep-alive");
        if (isTrack) {
            additionalHeaders.put("Host", "static.tieba.baidu.com");
        } else {
            additionalHeaders.put("Host", "tieba.baidu.com");
        }
        additionalHeaders.put("Referer", adUrl);
        additionalHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        WebRequest webRequest = buildWebRequest(verifyUrl,additionalHeaders);
        printAdditionalHeaders(webRequest);
        Page page = webClient.getPage(webRequest);
        printResponseHeaders(page.getWebResponse());
        return true;
    }

    public static boolean checkAd(String adData,String checkRegex) throws ParseException, IOException {
        ObjectMapper mapper = JsonUtils.getMapperInstance();
        JsonNode jsonNode = mapper.readTree(adData);
        String url = JsonPath.read("$.adData.url", jsonNode, String.class);
        Map<String, String> additionalHeaders = Maps.newHashMap();
        additionalHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        additionalHeaders.put("Accept-Encoding", "gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        additionalHeaders.put("Connection", "keep-alive");
        additionalHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        additionalHeaders.put("Referer",adUrl);
        WebRequest webRequest = buildWebRequest(url,additionalHeaders);
        HtmlPage page  = webClient.getPage(webRequest);

        return page.asXml().contains(checkRegex);
    }

    public static String getVerifyUrl(String adData, boolean isTrack) throws Exception {
        StringBuilder url = new StringBuilder();
        if(isTrack){
            url.append("http://static.tieba.baidu.com/tb/img/track.gif?");
        }else {
            url.append("http://tieba.baidu.com/billboard/pushlog/?");
        }
        ObjectMapper mapper = JsonUtils.getMapperInstance();
        JsonNode jsonNode = mapper.readTree(adData);
        String paramJson = "t=" + String.valueOf((new Date()).getTime()) +
                "&client_type=pc_web" +
                "&task=" + JsonPath.read("$.adData.pb_log.task", jsonNode, String.class) +
                "&page=frs" +
                "&fid=" + JsonPath.read("$.adData.pb_log.forum_id", jsonNode, Integer.class) +
                "&tid=" +
                "&uid=" +
                "&da_task=" + JsonPath.read("$.adData.pb_log.task", jsonNode, String.class) +
                "&da_fid=" + JsonPath.read("$.adData.pb_log.forum_id", jsonNode, Integer.class) +
                "&da_tid=" +
                "&da_uid=" +
                "&da_page=frs" +
                "&da_type_id=" + JsonPath.read("$.adData.typeid", jsonNode, String.class) +
                "&da_obj_id=" + JsonPath.read("$.adData.id", jsonNode, String.class) +
                "&da_good_id=" + JsonPath.read("$.adData.goods_info[0].id", jsonNode, String.class) +
                "&da_obj_name=" + JsonPath.read("$.adData.name", jsonNode, String.class) +
                "&da_first_name=" + JsonPath.read("$.adData.first_name", jsonNode, String.class) +
                "&da_second_name=" + JsonPath.read("$.adData.second_name", jsonNode, String.class) +
                "&da_cpid=" + JsonPath.read("$.adData.cpid", jsonNode, String.class) +
                "&da_abtest=" + JsonPath.read("$.adData.abtest", jsonNode, String.class) +
                "&da_price=" + JsonPath.read("$.adData.price", jsonNode, Integer.class) +
                "&da_verify=" + JsonPath.read("$.adData.verify", jsonNode, String.class) +
                "&da_plan_id=" + JsonPath.read("$.adData.plan_id", jsonNode, Integer.class) +
                "&da_ext_info=" + JsonPath.read("$.adData.ext_info", jsonNode, String.class) +
                "&da_client_type=" + JsonPath.read("$.adData.client_type", jsonNode, String.class) +
                (isTrack ? "&locate=" + JsonPath.read("$.adData.pos_name", jsonNode, String.class) : "") +
                "&da_locate=" + JsonPath.read("$.adData.pos_name", jsonNode, String.class) +
                (isTrack ? "&type=show" : "") +
                "&da_type=show";
        return encodeUrl(url.append(paramJson).toString(), "UTF-8");
    }

    public static String encodeUrl(String url, String encodingCharset) throws UnsupportedEncodingException {
        return new URLCodec().encode(url, encodingCharset).replace("%3A", ":").replace("%2F", "/").replace("%3F", "?").replace("%3D", "=").replace("%26", "&");
    }
}
