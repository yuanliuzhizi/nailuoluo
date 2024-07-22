package com.ruoyi.im.utils;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class UrlSpiltUtils {

    public static Map<String, String> getUrlMap(String param) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            String str=param;
            String[] splitURL=str.split("&");
            Map<String,Object> mapParam=new HashMap<String,Object>();
            for(String s:splitURL){
                if(s.indexOf("=")>0){
                    mapParam.put(s.substring(0, s.indexOf("=")),s.substring(s.indexOf("=")+1, s.length()));
                }

            }
            for(Map.Entry<String, Object> entry:mapParam.entrySet()){
                map.put(entry.getKey(), URLDecoder.decode(entry.getValue().toString(),"UTF-8"));
            }
        } catch (Exception e) {
            return null;
        }
        return map;
    }

    /**
     * hashMap 转化成表单字符串
     * @param map
     * @return
     */
    public static String map2Form(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map == null || map.size() == 0) {
            return stringBuilder.toString();
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
    }


    /**
     * 表单字符串转化成 hashMap
     *
     * @param orderinfo
     * @return
     */
    public static HashMap<String, String> form2Map( String orderinfo) {
        String listinfo[];
        HashMap<String, String> map = new HashMap<String, String>();
        listinfo = orderinfo.split("&");
        for(String s : listinfo)
        {
            String list[]  = s.split("=");
            if(list.length>1)
            {
                map.put(list[0], list[1]);
            }
        }
        return map;
    }


}