package lab.s2jh.core.test;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Unicode {
    public static String getUnicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);

            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static void main(String[] args) {
        String s =
                unicodeToString("{\n" +
                        "    'adData': {\n" +
                        "        \"id\": \"13250\",\n" +
                        "        \"name\": \"\\u795e\\u821f\\u7535\\u8111\",\n" +
                        "        \"typeid\": \"0002\",\n" +
                        "        \"pos_name\": 3,\n" +
                        "        \"loc_code\": \"p0001\",\n" +
                        "        \"client_type\": \"PC\",\n" +
                        "        \"page_name\": \"FRS\",\n" +
                        "        \"app_type\": \"5\",\n" +
                        "        \"tpl_name\": \"4\",\n" +
                        "        \"locator\": \"#thread_list .j_thread_list:not(.thread_top):eq(2)\",\n" +
                        "        \"load_type\": \"before\",\n" +
                        "        \"first_screen\": \"1\",\n" +
                        "        \"url_type\": \"1\",\n" +
                        "        \"throw_type\": \"0\",\n" +
                        "        \"url\": \"http:\\/\\/tieba.baidu.com\\/link?tbjump=lid%3D1825548220_13250_5_74075_%26url%3Db65a5G4CY1VIUJUUYnkX3w1YlSWoWo5azXHkoVJqQa4Wn6AS6MyiGzoA9F-_kEhqzVBxB9HkZewz34iyt3G4ATUDhjfKcXZtKfiEO1mZYSbEYZ0Mit0qD4I03n8K3b4-cOpSKvsrBhUnEOLAQELJ24PX1gIHk3LAcALn9-VvOn7hMqsy08AsVcEu_urOUVZWeo6oYkZp_D_kYfQ3NCQDqi3XfrSfxkcjt2YuuvgcPbkl5nWCjU1iXnVo3gcCdqQr2wFvcMzJZNRByA_DWAjvqKqZlfu0MAMbCE8muw0OPK1L0Zw9LuWTvH8pEkd_nKcZv_a3LV_hL198pTtcaP_6qxMBN3Oh2xuAnBNX3WSkOoQhgpnPDAyEksz9DcFFYzzsZDuTFqPw54C6EeENW-nLHmhZC1T00raVHhIKZS84_4137mKszAI7ZYRi2VTq-xhrmWJqQcSuYnWE_kAVKRMXEx7t9Vqy9s8osf2cWK2NZOhUTaQ8oa0ipqjFxATPRTsnR_NV8j3wWR7qDEC0DdJBjZ-MPzr0zAbzTOICmjy9S_cAwjZpRuzbmyJZ8zyQASIygm2oDE3qFypjJvt55DfQLUUIGcIP8aEZ7-bWnpBhcUwrgrPVmMja_Hnec98f5rbNi_tBiENLfg_JSf2B104MyFGkxuYH0jPw7uHlsHataRx0ET_oTMcKROeAyG9TNMklEU7Q3LXJCSy9A_oKn963q-faKlcXSRwGXquX7yTk71x3_QvBUinoTGChSN2bEFM4QqEkLwx4dX80hGotnO0rT4AfEx1PEUBLQcNDkH_EMsBrCI3LLCdjSS49BH_UcbSi4tJ1ZLDV\",\n" +
                        "        \"ios_url\": \"\",\n" +
                        "        \"apk_url\": \"\",\n" +
                        "        \"apk_name\": \"\",\n" +
                        "        \"first_name\": \"\\u975e\\u6807\",\n" +
                        "        \"second_name\": \"\\u7535\\u5546\",\n" +
                        "        \"goods_info\": [{\n" +
                        "            \"user_name\": \"\\u795e\\u821f\\u7535\\u8111\",\n" +
                        "            \"thread_title\": \"\\u72c2\\u4fc3\\uff01\\u5954\\u817e\\u53cc\\u6838+GT730\\u72ec\\u663e+19.5LED\\u6db2\\u6676\\u663e\\u793a\\u5668\\u60ca\\u72062799\\u5143\\uff01\",\n" +
                        "            \"thread_content\": \"\\u6700\\u5f3a\\u6027\\u4ef7\\u6bd4\\u72ec\\u663e\\u673a\\uff01\\u795e\\u821fK60\\uff0cGT730 2G\\u72ec\\u663e\\u4e3b\\u673a+19.5LED\\u6db2\\u6676\\u4ec5\\u552e2799\\u5143\\uff01\",\n" +
                        "            \"thread_pic\": \"http:\\/\\/tb1.bdstatic.com\\/tb\\/cms\\/ngmis\\/adsense\\/file_1429755779345.jpg\",\n" +
                        "            \"id\": \"22522\"\n" +
                        "        }],\n" +
                        "        \"cpid\": \"5\",\n" +
                        "        \"abtest\": \"\",\n" +
                        "        \"price\": 100,\n" +
                        "        \"plan_id\": 1,\n" +
                        "        \"user_id\": \"37676788\",\n" +
                        "        \"ext_info\": \"1_0_0_0_5_0_0_0\",\n" +
                        "        \"search_id\": null,\n" +
                        "        \"imTimeSign\": null,\n" +
                        "        \"ad_tags\": [],\n" +
                        "        \"verify\": \"b2de0902a1f16b8b65d40f364b1f51a3\",\n" +
                        "        \"raw_adurl\": \"http:\\/\\/www.hasee.com\\/cn\\/ad\\/2015\\/desktop\\/01\\/\",\n" +
                        "        \"pb_log\": {\n" +
                        "            \"forum_id\": 74075,\n" +
                        "            \"forum_name\": \"\\u56fe\\u62c9\\u4e01\",\n" +
                        "            \"forum_dir\": \"\\u7535\\u8111\\u6570\\u7801\",\n" +
                        "            \"forum_second_dir\": \"\\u7535\\u8111\\u53ca\\u786c\\u4ef6\",\n" +
                        "            \"click_url\": \"http:\\/\\/www.hasee.com\\/cn\\/ad\\/2015\\/desktop\\/01\\/\",\n" +
                        "            \"action_type\": 2,\n" +
                        "            \"client_type\": 1,\n" +
                        "            \"task\": \"tbda\",\n" +
                        "            \"loc_param\": 1,\n" +
                        "            \"page\": 2,\n" +
                        "            \"location\": \"p0001\"\n" +
                        "        },\n" +
                        "        \"click_url_params\": \"lid%3D1825548220_13250_5_74075_%26url%3Db65a5G4CY1VIUJUUYnkX3w1YlSWoWo5azXHkoVJqQa4Wn6AS6MyiGzoA9F-_kEhqzVBxB9HkZewz34iyt3G4ATUDhjfKcXZtKfiEO1mZYSbEYZ0Mit0qD4I03n8K3b4-cOpSKvsrBhUnEOLAQELJ24PX1gIHk3LAcALn9-VvOn7hMqsy08AsVcEu_urOUVZWeo6oYkZp_D_kYfQ3NCQDqi3XfrSfxkcjt2YuuvgcPbkl5nWCjU1iXnVo3gcCdqQr2wFvcMzJZNRByA_DWAjvqKqZlfu0MAMbCE8muw0OPK1L0Zw9LuWTvH8pEkd_nKcZv_a3LV_hL198pTtcaP_6qxMBN3Oh2xuAnBNX3WSkOoQhgpnPDAyEksz9DcFFYzzsZDuTFqPw54C6EeENW-nLHmhZC1T00raVHhIKZS84_4137mKszAI7ZYRi2VTq-xhrmWJqQcSuYnWE_kAVKRMXEx7t9Vqy9s8osf2cWK2NZOhUTaQ8oa0ipqjFxATPRTsnR_NV8j3wWR7qDEC0DdJBjZ-MPzr0zAbzTOICmjy9S_cAwjZpRuzbmyJZ8zyQASIygm2oDE3qFypjJvt55DfQLUUIGcIP8aEZ7-bWnpBhcUwrgrPVmMja_Hnec98f5rbNi_tBiENLfg_JSf2B104MyFGkxuYH0jPw7uHlsHataRx0ET_oTMcKROeAyG9TNMklEU7Q3LXJCSy9A_oKn963q-faKlcXSRwGXquX7yTk71x3_QvBUinoTGChSN2bEFM4QqEkLwx4dX80hGotnO0rT4AfEx1PEUBLQcNDkH_EMsBrCI3LLCdjSS49BH_UcbSi4tJ1ZLDV\"\n" +
                        "    }, /* 广告数据 */\n" +
                        "    'className': 'ad9dd7a872', /* 广告定位器 */\n" +
                        "    'asyncHTML': '', /* 异步加载的广告字符串 */\n" +
                        "    'isAsync': '', /* 是否为异步加载 */\n" +
                        "    'needStats': true                       /* 是否需要点击统计 */\n" +
                        "}");
        System.out.println(s);

        long t = 1429774290681l;
        Date d = new Date(t);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(d));

    }
}
