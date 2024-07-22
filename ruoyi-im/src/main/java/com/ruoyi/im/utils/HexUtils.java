package com.ruoyi.im.utils;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 十六进制工具类
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class HexUtils {
    /**
     * 时间转进制
     * @param textTime 文本型时间
     * @return 十六进制
     */
    public static String timeToHex(String textTime){
        if(textTime == null || textTime.trim().equals("")) {
            return "";
        }else {
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            Date date;
            String textHex="";
            try{
                date=ft.parse(textTime);
                long timer = date.getTime() / 1000;
                textHex= Long.toHexString(timer).toUpperCase();
            }catch (Exception e){
                System.err.println("error154:时间转换错误");
            }
            return textHex;
        }
    }

    /**
     * 进制转时间
     * @param textHex 文本型时间十六进制
     * @return 时间
     */
    public static String hexToTime(String textHex) {
        textHex=textHex.replace(" ", "");
        textHex=textHex.replaceAll("[\\{\\}\\[\\]]", "");
        textHex=textHex .replaceAll("\\s*", "");
        if (textHex != null && !textHex.equals("")) {
            BigInteger bigInteger = new BigInteger(textHex, 16);
            BigInteger bigInteger1 = new BigInteger("-9223372036854775808");
            BigInteger bigInteger2 = new BigInteger("9223372036854775807");
            int aa = bigInteger.compareTo(bigInteger1);
            int bb = bigInteger.compareTo(bigInteger2);
            if (aa == 1 && bb == -1) {
                long lo = Long.valueOf(bigInteger.toString());
                Long.valueOf(textHex, 16);
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return ft.format(new Date(lo * 1000));
            }
        }
        return "";
    }



    /**
     * 进制转补位
     * @param num
     * @return
     */
    public static String ten2Hex1(int num) {
        String strHex1 = String.format("%02x", num).toUpperCase();//不足两位高位补0
        return strHex1;
    }

    /**
     * 进制转补位
     * @param num
     * @return
     */
    public static String ten2Hex2(long num) {
//        String strHex1 = Integer.toHexString(valueTen).toUpperCase();//高位不用补0
        String strHex2 = String.format("%08x", num).toUpperCase();//高位补0
        return strHex2;
    }


    /**
     * str 原字符串
     * strLength 字符串总长
     * */

    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();

        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();

                sb.append("0").append(str);// 左补0

    // sb.append(str).append("0");//右补0

                str = sb.toString();

                strLen = str.length();

            }

        }

        return str;

    }


    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    public static String intToIp(long ipInt){
        StringBuilder sb=new StringBuilder();
        sb.append(ipInt&0xFF).append(".");
        sb.append((ipInt>>8)&0xFF).append(".");
        sb.append((ipInt>>16)&0xFF).append(".");
        sb.append((ipInt>>24)&0xFF);
        return sb.toString();
    }





    /**

     * IPv4地址和int数字的互换

     * @author sun

     */



        /**

         * IPv4地址转换为int类型数字

         * @param ipv4Addr

         * @return

         */

        public static int ipToInt(String ipv4Addr) {

// 判断是否是ip格式的

            if (!isIPv4Address(ipv4Addr))

                throw new RuntimeException("Invalid ip address");

// 匹配数字

            Pattern pattern = Pattern.compile("\\d+");

            Matcher matcher = pattern.matcher(ipv4Addr);

            int result = 0;

            int counter = 0;

            while (matcher.find()) {

                int value = Integer.parseInt(matcher.group());

                result = (value << 8 * (3 - counter++)) | result;

            }

            return result;

        }

        /**

         * 判断是否为ipv4地址

         * @param ipv4Addr

         * @return

         */

        private static boolean isIPv4Address(String ipv4Addr) {

            String lower = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])"; // 0-255的数字

            String regex = lower + "(\\." + lower + "){3}";

            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(ipv4Addr);

            return matcher.matches();

        }

        /**

         * 将int数字转换成ipv4地址

         * @param ip

         * @return

         */

        public static String intToIp(int ip) {

            StringBuilder sb = new StringBuilder();

            int num = 0;

            boolean needPoint = false; // 是否需要加入'.'

            for (int i = 0; i < 4; i++) {

                if (needPoint) {

                    sb.append('.');

                }

                needPoint = true;

                int offset = 8 * (3 - i);

                num = (ip >> offset) & 0xff;

                sb.append(num);

            }

            return sb.toString();

        }










}
