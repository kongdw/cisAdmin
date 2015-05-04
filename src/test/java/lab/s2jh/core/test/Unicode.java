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
                        "        \"id\": \"13329\",\n" +
                        "        \"name\": \"\\u795e\\u821f-\\u661f\\u5c18\",\n" +
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
                        "        \"url\": \"http:\\/\\/tieba.baidu.com\\/link?tbjump=lid%3D757465755_13329_5_52_%26url%3D85c1gto4JFnimOnxv54iG-X_YUBYp3KKw-2LiZGYfwpXnX-jeMhKVUS8UMKFTx7fvdPZkdh_akcUvPlKmK8Eal54qwLNyEFnyfzr-CurVqMy5S7hDfOc23cTAGCvS9iVPUnSwBid3v6bEiphaD5gbd7ogeSnY7Sv90QlpQfKnUzWNCJs9roOYN726JMHFjyhGVdvOXeVzPhJFgt9F_KW96rxiDdnZSqsD6YXeA7yRM6nNlwkwwMqC298bQUVK_z-4voCwHcB4367cg0jQ1GaJeGfADtH_AVTsCst3Z0zClLpZgCSjpDkKE_VcN-T287dTz77R8QNUT2vCqdiMMBBH0F9cUYxjBHT0JKG4ZNtzGah06DeCbvDmOvC2SzHOEPX8TLJMt06zx5OWWTaTmXrAldTN2Fvk2ApOKcEaLdFTMbFnDS20ABK2uvDAR0ti1pdpCvE6Zp8vIgkS9rEyT5JFeFHpbS-vWYoy9Lvnl1nj9bVfynt9L_Tkomfs9lOfCFx-dMoAtsiKWJYfjdqlilL_tY20EY52Xv_iS9VuR59xZBtskv5vsnhfgRb7Mbs1HlScAgF7JP3gS5EsxK3uqtl3QCCyNA0KUGveqrqhfFRybe8T-43n4HawWRHNPkfQWZavfM_UAnEmgk8ByMr9aBTpJ5HPo9iBk2Sh95Cae45h6u-diqza2k4jZBGR2mUzlAGX_t535urqa_pQlzhwGoyJWn8zATzbkW4VGo3NEZNaizHZvJGUmJ3pKmszvcDWoGUnBXSrewXDKUk6y4bc3_tDxb3x58RrJJqiels9UfEn3UZirIyeoga0j19XJfJpb5mumBk-9wGUeai\",\n" +
                        "        \"ios_url\": \"\",\n" +
                        "        \"apk_url\": \"\",\n" +
                        "        \"apk_name\": \"\",\n" +
                        "        \"first_name\": \"\\u975e\\u6807\",\n" +
                        "        \"second_name\": \"\\u7535\\u5546\",\n" +
                        "        \"goods_info\": [{\n" +
                        "            \"user_name\": \"\\u96f7\\u795e\\u7b14\\u8bb0\\u672c\",\n" +
                        "            \"thread_title\": \"\\u96f7\\u795e\\u62fc\\u4e86\\uff01\\u65b0\\u54c1911M-M4\\u4f17\\u7b79\\u60ca\\u5446\\u4ef7\\uffe54998\\uff01\",\n" +
                        "            \"thread_content\": \"\\u96f7\\u795e\\u65b0\\u54c1911M-M4\\u8f7b\\u5962\\u6e38\\u620f\\u672c\\u4f17\\u7b79\\u5f00\\u59cb\\u5566\\uff01\\uffe54998\\u60ca\\u5446\\u4ef7\\uff01\\u6bcf\\u4e2aID\\u9650\\u8d2d\\u4e00\\u53f0\\uff01\",\n" +
                        "            \"thread_pic\": \"http:\\/\\/tb1.bdstatic.com\\/tb\\/cms\\/ngmis\\/adsense\\/file_1429866035098.JPG\",\n" +
                        "            \"id\": \"22678\"\n" +
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
                        "        \"verify\": \"9f1afe2c58927e5cf2489e721d9bdb84\",\n" +
                        "        \"raw_adurl\": \"http:\\/\\/z.jd.com\\/project_details.action?projectId=8375\",\n" +
                        "        \"pb_log\": {\n" +
                        "            \"forum_id\": 52,\n" +
                        "            \"forum_name\": \"\\u7b14\\u8bb0\\u672c\",\n" +
                        "            \"forum_dir\": \"\\u7535\\u8111\\u6570\\u7801\",\n" +
                        "            \"forum_second_dir\": \"\\u7535\\u8111\\u53ca\\u786c\\u4ef6\",\n" +
                        "            \"click_url\": \"http:\\/\\/z.jd.com\\/project_details.action?projectId=8375\",\n" +
                        "            \"action_type\": 2,\n" +
                        "            \"client_type\": 1,\n" +
                        "            \"task\": \"tbda\",\n" +
                        "            \"loc_param\": 1,\n" +
                        "            \"page\": 2,\n" +
                        "            \"location\": \"p0001\"\n" +
                        "        },\n" +
                        "        \"click_url_params\": \"lid%3D757465755_13329_5_52_%26url%3D85c1gto4JFnimOnxv54iG-X_YUBYp3KKw-2LiZGYfwpXnX-jeMhKVUS8UMKFTx7fvdPZkdh_akcUvPlKmK8Eal54qwLNyEFnyfzr-CurVqMy5S7hDfOc23cTAGCvS9iVPUnSwBid3v6bEiphaD5gbd7ogeSnY7Sv90QlpQfKnUzWNCJs9roOYN726JMHFjyhGVdvOXeVzPhJFgt9F_KW96rxiDdnZSqsD6YXeA7yRM6nNlwkwwMqC298bQUVK_z-4voCwHcB4367cg0jQ1GaJeGfADtH_AVTsCst3Z0zClLpZgCSjpDkKE_VcN-T287dTz77R8QNUT2vCqdiMMBBH0F9cUYxjBHT0JKG4ZNtzGah06DeCbvDmOvC2SzHOEPX8TLJMt06zx5OWWTaTmXrAldTN2Fvk2ApOKcEaLdFTMbFnDS20ABK2uvDAR0ti1pdpCvE6Zp8vIgkS9rEyT5JFeFHpbS-vWYoy9Lvnl1nj9bVfynt9L_Tkomfs9lOfCFx-dMoAtsiKWJYfjdqlilL_tY20EY52Xv_iS9VuR59xZBtskv5vsnhfgRb7Mbs1HlScAgF7JP3gS5EsxK3uqtl3QCCyNA0KUGveqrqhfFRybe8T-43n4HawWRHNPkfQWZavfM_UAnEmgk8ByMr9aBTpJ5HPo9iBk2Sh95Cae45h6u-diqza2k4jZBGR2mUzlAGX_t535urqa_pQlzhwGoyJWn8zATzbkW4VGo3NEZNaizHZvJGUmJ3pKmszvcDWoGUnBXSrewXDKUk6y4bc3_tDxb3x58RrJJqiels9UfEn3UZirIyeoga0j19XJfJpb5mumBk-9wGUeai\"\n" +
                        "    }, /* 广告数据 */\n" +
                        "    'className': 'p8ba97b449', /* 广告定位器 */\n" +
                        "    'asyncHTML': '', /* 异步加载的广告字符串 */\n" +
                        "    'isAsync': '', /* 是否为异步加载 */\n" +
                        "    'needStats': true                       /* 是否需要点击统计 */\n" +
                        "}");
        System.out.println(s);

    }
}
