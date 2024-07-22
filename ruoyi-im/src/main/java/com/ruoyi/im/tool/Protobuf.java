package com.ruoyi.im.tool;


import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.NumericConvertUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * protobuf
 * 2021-3-17
 * by GuangHeLiZi
 *
 * 注：简单解释一下C语言中的算术位移和逻辑位移。他们的左移操作都是相同的，即低位补0，高位直接移除。不同的是右移操作，逻辑位移比较简单，高位全部补0。
 * 而算术位移则需要视当前值的符号位而定，补进的位和符号位相同，即正数全补0，负数全补1。换句话说，算术位移右移时要保证符号位的一致性。
 * 在C语言中，如果使用 int变量位移时就是算术位移，uint变量位移时是逻辑位移。
 *
 *
 * 更多请鉴
 * */
public class Protobuf {


    public static void main(String[] args) {
        System.out.println(Protobuf.bit64(AllToByte.hexToByte(NumericConvertUtil.toOtherBaseString(Long.parseLong("678680621"), 16)+"20 00 01 98 EB 9F 77 00 00 0E 10 "),4));
    }


    PackList packList = new PackList();

    /**
     * int64包含int32 pro
     * pb正负判断
     * @param i 头索引
     * @param o 根据tag
     * @param tag 0 是byte[]类型 1 是整数 2 是有子节点
     * @return 返回十六进制pb
     */
    public void pbPro(int tag,String i,Object o){
        String[] strings=i.split("\\.");
        int len = strings.length;
        ArrayList arr = new ArrayList();
        arr.add(o);
        arr.add(strings);
        arr.add(tag);
        packList.add(arr);
    }

    public String recursion(){
        ArrayList arrayList;

        do {
            arrayList=pbProPack();
            packList.set(arrayList);
        }while (arrayList.size()>1);
        ArrayList arrayList1= (ArrayList) arrayList.get(0);
        String[] strings1 = (String[]) arrayList1.get(1);//tag
        int strings2 = (int) arrayList1.get(2);//类型
        if (strings2==0){//0 是byte[]类型 1 是整数 2 是有子节点
        }else if (strings2==1){
        }else {//遇到父节点处理
            StringBuilder s = new StringBuilder();
            for (int i1 = 3; i1 < arrayList1.size(); i1++) {//取出之前处理过的并接起来
                String data=arrayList1.get(i1).toString();
                s.append(data);
            }
            String pb=Protobuf.bit64(AllToByte.hexToByte(s.toString()), Integer.parseInt(strings1[strings1.length - 1]));
            packList.empty();
            return pb;
        }
        return null;
    }
    public ArrayList pbProPack(){
        ArrayList arrayList=packList.getAll();
        int[] intList=new int[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            ArrayList arr= (ArrayList) arrayList.get(i);
            String[] strings1 = (String[]) arr.get(1);//tag
            intList[i] = strings1.length;
        }
        int iMax=toMax(intList);
        ArrayList aix = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            ArrayList arr= (ArrayList) arrayList.get(i);
            String[] strings1 = (String[]) arr.get(1);//tag
            int ii=strings1.length;
            if (iMax==ii){//处理最里层pb
                int strings2 = (int) arr.get(2);//类型
                if (strings2==0){//0 是byte[]类型 1 是整数 2 是有子节点
                    ArrayList arr1= (ArrayList) arrayList.get(i-1);
                    byte[] bytes = (byte[]) arr.get(0);//内容
                    String pb=Protobuf.bit64(bytes, Integer.parseInt(strings1[ii - 1]));
                    arr1.add(pb);
                    aix.add(i);
                    break;
                }else if (strings2==1){
                    ArrayList arr1= (ArrayList) arrayList.get(i-1);
                    long l = Long.parseLong(arr.get(0).toString());//内容
                    String pb=Protobuf.int64(l, Integer.parseInt(strings1[ii - 1]));
                    arr1.add(pb);
                    aix.add(i);
                    break;
                }else {//遇到父节点处理
                    StringBuilder s = new StringBuilder();
                    for (int i1 = 3; i1 < arr.size(); i1++) {//取出之前处理过的并接起来
                        String data=arr.get(i1).toString();
                        s.append(data);
                    }
                    arr.set(0, AllToByte.hexToByte(s.toString()));
                    arr.set(2, 0);
                }

            }


        }


        for (int i = aix.size() - 1; i >= 0; i--) {
            arrayList.remove(Integer.parseInt(aix.get(i).toString()));
        }

        return arrayList;


    }

    public static int toMax(int[] intList) {
        // 查找最大元素
        int max = intList[0];
        for (int i = 1; i < intList.length; i++) {
            if (intList[i] > max) max = intList[i];
        }
        return max;

    }


    /**
     * pb头生成
     *
     * int 8 000
     * double 9 001
     * float 13  101
     * byte 10  010
     *
     * @param num1x10 头索引
     * @param num1x16 数据类型
     *
     * @return 生成头
     */
    public static String headPro(int num1x10, int num1x16) {

        String numx2= NumericConvertUtil.toOtherBaseString(num1x10,2);

        switch (num1x16){
            case 1:
                numx2=numx2+"000";
                break;
            case 2:
                numx2=numx2+"001";
                break;
            case 3:
                numx2=numx2+"101";
                break;
            case 4:
                numx2=numx2+"010";
                break;
        }

         long end= NumericConvertUtil.toDecimalism(numx2,2);
         long end1= NumericConvertUtil.toDecimalism("128",10);

         if(end1>end){//小于128直接转换
             return Protobuf.ten2Hex1(NumericConvertUtil.toTwoBaseString(numx2));
         }else {
             int len=numx2.length();
             int x=len /7;
             int min=len-x*7;
             StringBuffer stringBuffer=new StringBuffer();
              while (x!=0){
                 String str=numx2.substring(min+((x-1)*7),min+(x*7));
                 String add= Protobuf.ten2Hex1(NumericConvertUtil.toTwoBaseString(padLeft(str)));
                 stringBuffer.append(add);
                 x--;
             }
             String add= Protobuf.ten2Hex1(NumericConvertUtil.toTwoBaseString(numx2.substring(0,min)));
             stringBuffer.append(add);
             return stringBuffer.toString();

         }


    }


    /**
     * int64包含int32
     * pb正负判断
     * @param i 头索引
     * @param ln 长整数  超过无效 // -9223372036854775808 到 9223372036854775807 负数比较繁琐做到long范围实现负数 （-2^64“ 到”2^64 -1）之外头大
     * @return 返回十六进制pb
     */
    public static String int64(Long ln, int i){

        if(-9223372036854775808l>ln||9223372036854775807l<ln){
            return "0";
        }
        if(ln>=0){
            return headPro(i,1)+" "+valNumP(ln);
        }
        if (ln<0){
            return headPro(i,1)+" "+valNumM(Math.abs(ln));
        }
            return "0";

    }

    /**
     * bit64 字节集型
     * 适用于文件字节特殊编码等字节类型的数据
     * @param i 头索引
     * @param bin 字节集型  超过无效 // 负数比较繁琐做到long范围实现负数 （-2^32“ 到”2^32 -1）之外头大
     * @return 返回十六进制pb
     */
    public static String bit64(byte[] bin, int i) {
        Pack pack=new Pack();
        pack.empty();
        pack.setHex(headPro(i,4));
        pack.setHex(valNumP((long)bin.length));
        pack.setBin(bin);
        return ByteToAll.byteToHxe(pack.getAll());
    }






    /**
     * pb负整数生成
     *
     *
     * @param ln 长整数  超过无效 //-9223372036854775808到9223372036854775807 负数比较繁琐做到long范围实现负数 （-2^64“ 到”2^64 -1）之外头大
     *
     * @return 返回十六进制pb
     */
    public static String valNumM(Long ln){


        if(-128==ln){//这是java处理特殊情况，128进一
            return "80 FF FF FF FF FF FF FF FF 01";
        }if(-16384==ln){//这是java处理特殊情况，128进一
            return "80 80 FF FF FF FF FF FF FF 01";
        }if(-2097152==ln){//这是java处理特殊情况，128进一
            return "80 80 80 FF FF FF FF FF FF 01";
        }if(-268435456==ln){//这是java处理特殊情况，128进一
            return "80 80 80 80 FF FF FF FF FF 01";
        }if(-34359738368l==ln){//这是java处理特殊情况，128进一
            return "80 80 80 80 80 FF FF FF FF 01";
        }if(-4398046511104l==ln){//这是java处理特殊情况，128进一
            return "80 80 80 80 80 80 FF FF FF 01";
        }if(-562949953421312l==ln){//这是java处理特殊情况，128进一
            return "80 80 80 80 80 80 80 FF FF 01";
        }if(-72057594037927936l==ln){//这是java处理特殊情况，128进一
            return "80 80 80 80 80 80 80 80 FF 01";
        }if(-9223372036854775808l==ln){//这是java处理特殊情况，128进一
            return "80 80 80 80 80 80 80 80 80 01";
        }

        BigInteger ber=new BigInteger(String.valueOf(ln),10);
        //1.转换为bigNum的二进制补码形式
        byte[] num1 = ber.toByteArray();
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < num1.length; i++) {
            String tString = Long.toBinaryString((num1[i] & 0xFF) + 0x100).substring(1);//补位
            s.append(tString);
        }
        String s1=s.toString();
        int l=s1.length();
        if(l<64){//多余的0最后一个字节填1
            for (int i1 = 0; i1 < (64-l); i1++) {
                if(i1==(63-l)){
                    s1=padLeft(s1);
                }else {
                    s1=padLeft0(s1);
                }
            }
        }
        s.delete(0,s.length());
        s.append(s1);
        if(s.length()%7!=0){
            for (int i = 0; i < s.length() % 7; i++) {
                s.insert(0,"0");
            }
        }
        List list=new ArrayList();
        for (int i = 0; i < s.length(); i++) {
            if(i%7==0){
                if(i==s.length()-7){//最后这是处理负数进一问题的方案
                    BigInteger b = new BigInteger(s.substring(i, i + 7), 2);
                    BigInteger a = new BigInteger("f1", 17);
                    BigInteger e = new BigInteger(a.subtract(b).toString(2), 2);
//                    BigInteger d = new BigInteger("256",10);
//                    if (e.compareTo(d)==0) {//等于0 小于-1 大于1
//                    }
                    list.add(e.toString(16));
                }else {//靠前
                    if(i==0){
                        //long限制方案第10位只能是1
//                        BigInteger b = new BigInteger(s.substring(i, i + 7), 2);
//                        list.add(ten2Hex2(b.intValue()));//会有影响
                        list.add(ten2Hex2(1));//写死
                    }else {
                        BigInteger b = new BigInteger(s.substring(i, i + 7), 2);
                        BigInteger a = new BigInteger("ff", 16);
                        BigInteger e = new BigInteger(a.subtract(b).toString(2), 2);
                        list.add(e.toString(16));
                    }
                }
            }
        }

        StringBuffer sin=new StringBuffer();
        for (int i = list.size() - 1; i >= 0; i--) {
            sin.append(((String) list.get(i)).toUpperCase() + " ");
        }
        return sin.toString();

    }





    /**
     * pb正整数生成
     *
     *
     * @param ln 长整数  超过无效 //-9223372036854775808到9223372036854775807 负数比较繁琐做到long范围实现负数 （-2^64“ 到”2^64 -1）之外头大
     *
     * @return 返回十六进制pb
     */
    public static String valNumP(Long ln){

        BigInteger ber=new BigInteger(String.valueOf(ln),10);
        //1.转换为bigNum的二进制补码形式
        byte[] num1 = ber.toByteArray();
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < num1.length; i++) {
            String tString = Long.toBinaryString((num1[i] & 0xFF) + 0x100).substring(1);//补位
            s.append(tString);
        }
        BigInteger b=new BigInteger(s.toString(),2);
        s.delete(0,s.length());
        s.append(b.toString(2));
        int le=s.length()%7;
        if(le!=0){
            for (int i = 0; i < 7-le; i++) {
                s.insert(0,"0");
            }
        }

        StringBuffer sin=new StringBuffer();

        for (int i = (s.length() / 7); i > 0; i--) {
            if(i==1){
                BigInteger br=new BigInteger("0"+s.substring(i*7-7,i*7),2);
                sin.append(ten2Hex2(br.intValue()));
            }else {
                BigInteger br=new BigInteger("1"+s.substring(i*7-7,i*7),2);
                sin.append(br.toString(16).toUpperCase()+" ");
            }

        }

        return sin.toString();

    }






    public static String padLeft(String s){
        StringBuffer stringBuffer=new StringBuffer(s);
        return  stringBuffer.insert(0,"1").toString();
    }

    public static String padLeft0(String s){
        StringBuffer stringBuffer=new StringBuffer(s);
        return  stringBuffer.insert(0,"0").toString();
    }

    /**
     * 进制转补位
     * @param num
     * @return
     */
    public static String ten2Hex1(String num) {
        StringBuffer stringBuffer=new StringBuffer(num);
        if(stringBuffer.length()==1){
            return  stringBuffer.insert(0,"0").toString();
        }else {
            return num;
        }

    }


    /**
     * 套娃
     * @param s1 解析全部数据
     * @return map解析tag len data remain
     */
    public static List pbToList(String s1){
        List lm=analysisAllPB(s1);
        return analysisData1(lm);

    }

    /**
     * 套娃
     * @param lm 解析全部数据
     * @return map解析tag len data remain
     */
    public static List analysisData1(List lm){
        for (int i = 0; i < lm.size(); i++) {//循环判断是否分尽
            Map map= (Map) lm.get(i);
            if (map.get("string") != null || "i".equals(map.get("type"))) {//分尽 是整数情况不进行解析或string不为null情况下
            }else {
                String data=(String) map.get("data");
                List l=analysisAllPB(data);
                if(l.size()!=0){
                    l=analysisData1(l);
                    map.put("data",l);
                    lm.set(i,map);
                }
            }
        }
        return lm;

    }



    /**
     * 套娃
     * @param lm 解析全部数据
     * @return map解析tag len data remain
     */
    public static List analysisData(List lm){
        for (int i = 0; i < lm.size(); i++) {
            Map map= (Map) lm.get(i);
            if ("b".equals(map.get("type"))&&(map.get("string")==null)) {//跳过整型
                String data= (String) map.get("data");
                List lm1=analysisAllPB(data);
                if(lm1.size()!=0){//有数据
                    Map mm=(Map)lm.get(i);
                    mm.put("data",lm1);
                    lm.set(i,mm);
                }else {//没有数据
                }
            }else if("i".equals(map.get("type"))){
                if(!"".equals(map.get("remain"))){
                    Map m= (Map) lm.get(i);
                    List l= (List) m.get("tagData");
                    String s = "";
                    for (int i1 = 0; i1 < l.size(); i1++) {
                        s+=l.get(i1);
                    }
                    m.put("string",s+m.get("remain"));
                    lm.set(i,m);
                }
            }
        }
        return lm;
    }



    /**
     * 解析PB 全部解析
     * @param s1
     * @return map解析tag len data remain
     */
    public static List analysisAllPB(String s1){
        List lm=new ArrayList();
        Map m;
        do {
            m=analysisPB(s1);
            if(m!=null){
                s1= (String) m.get("remain");
                lm.add(m);
            }else {
                break;
            }
        }while (!"".equals(m.get("remain")));
        return lm;
    }



    /**
     * 解析PB 单次解析
     * @param s1
     * @return map解析tag len data remain
     */
        public static Map analysisPB(String s1){

            StringBuffer s = new StringBuffer();//字节
            s.append(StringFilter(getNonBlankStr(s1)));
            if(s==null||s.length()==0){
                return null;
            }else if(s.length()<4){
                return null;
            }else if (s.length() % 2!=0) {
                s.insert(s.length()-1,"0");
            }

            List l=new ArrayList();
            for (int i = 0; i < s.length(); i+=2) {
                BigInteger b=new BigInteger(s.substring(i, i + 2),16);//tag
                String tString = Long.toBinaryString((b.intValue() & 0xFF) + 0x100).substring(1);//补位
                l.add(tString);
            }
            String s2=(String) l.get(0);
            int tl=s2.length();
            BigInteger bq=new BigInteger("128",10);
            Map m=new HashMap<String, ArrayList>();
            if ("000".equals(s2.substring(tl - 3,tl))) {
                List tag=new ArrayList();
                for (int i = 0; i < l.size(); i++) {//tag
                    BigInteger b=new BigInteger((String) l.get(i),2);
                    if(bq.compareTo(b)==1){//小于则返回-1，等于则返回0，大于则返回1
                        tag.add(b.toString(16));
                        break;
                    }else {
                        tag.add(b.toString(16));
                    }
                }
                m.put("tagData",tag);
                StringBuffer st=new StringBuffer();
                for (int i = tag.size() - 1; i >= 0; i--) {
                    BigInteger b=new BigInteger((String) tag.get(i),16);
                    String tString = Long.toBinaryString((b.intValue() & 0xFF) + 0x100).substring(1);//补位
                    if(i == tag.size() - 1){
                        st.append(tString);
                    }else {
                        st.append(tString.substring(1));
                    }
                }
                BigInteger bt=new BigInteger(st.substring(0,st.length()-3),2);
                m.put("tag",bt.toString(10));
                List lv=new ArrayList();
                for (int i = tag.size(); i < l.size(); i++) {//tag
                    BigInteger b=new BigInteger((String) l.get(i),2);
                    if(bq.compareTo(b)==1){//小于则返回-1，等于则返回0，大于则返回1
                        lv.add(b.toString(16));
                        break;
                    }else {
                        lv.add(b.toString(16));
                    }
                }
                m.put("lvData",lv);
                if(((ArrayList)m.get("lvData")).size()>9){//负数处理
                    StringBuffer slv = new StringBuffer();
                    for (int i = lv.size() - 1; i >= 0; i--) {
                        BigInteger b = new BigInteger((String) lv.get(i), 16);
                        String tString = Long.toBinaryString((b.intValue() & 0xFF) + 0x100).substring(1);//补位
                        if (i == lv.size() - 1) {
                        } else {
                            BigInteger br=new BigInteger(tString,2);
                            BigInteger br2=new BigInteger("11111111",2);
                            String tString1 = Long.toBinaryString((br2.subtract(br).intValue() & 0xFF) + 0x100).substring(1);//补位
                            slv.append(tString1.substring(1));
                        }
                    }
                    BigInteger br=new BigInteger(String.valueOf(slv),2);
                    slv.delete(0,slv.length());
                    slv.append(br.add(new BigInteger("1",2)).toString(2));
                    BigInteger blv = new BigInteger(slv.substring(0, slv.length()), 2);
                    m.put("lv", "-"+blv.toString(10));
                    m.put("type", "i");//类型
                    List ss1 = (ArrayList) m.get("tagData");
                    List ss2 = (ArrayList) m.get("lvData");
                    int l1 = ss1.size() + ss2.size();
                    m.put("remain", s.substring(l1 * 2, s.length()));//如果剩余
                    return m;

                }else {
                    StringBuffer slv = new StringBuffer();
                    for (int i = lv.size() - 1; i >= 0; i--) {
                        BigInteger b = new BigInteger((String) lv.get(i), 16);
                        String tString = Long.toBinaryString((b.intValue() & 0xFF) + 0x100).substring(1);//补位
                        if (i == lv.size() - 1) {
                            slv.append(tString);
                        } else {
                            slv.append(tString.substring(1));
                        }
                    }
                    try {
                        BigInteger blv = new BigInteger(slv.substring(0, slv.length()), 2);
                        m.put("lv", blv.toString(10));
                    }catch (Exception e){

                    }
                    m.put("type", "i");//类型
                    List ss1 = (ArrayList) m.get("tagData");
                    List ss2 = (ArrayList) m.get("lvData");
                    int l1 = ss1.size() + ss2.size();
                    m.put("remain", s.substring(l1 * 2, s.length()));//如果剩余
                    return m;
                }
            }
            if ("001".equals(s2.substring(tl - 3,tl))) {
//                System.out.println("双精度型");
            }
            if ("101".equals(s2.substring(tl - 3,tl))) {
//                System.out.println("浮点数型");
            }
            if ("010".equals(s2.substring(tl - 3,tl))) {
//                System.out.println("字节型");
                List tag=new ArrayList();
                for (int i = 0; i < l.size(); i++) {//tag
                    BigInteger b=new BigInteger((String) l.get(i),2);
                    if(bq.compareTo(b)==1){//小于则返回-1，等于则返回0，大于则返回1
                        tag.add(b.toString(16));
                        break;
                    }else {
                        tag.add(b.toString(16));
                    }
                }
                m.put("tagData",tag);
                StringBuffer st=new StringBuffer();
                for (int i = tag.size() - 1; i >= 0; i--) {
                    BigInteger b=new BigInteger((String) tag.get(i),16);
                    String tString = Long.toBinaryString((b.intValue() & 0xFF) + 0x100).substring(1);//补位
                    if(i == tag.size() - 1){
                        st.append(tString);
                    }else {
                        st.append(tString.substring(1));
                    }
                }
                BigInteger bt=new BigInteger(st.substring(0,st.length()-3),2);
                m.put("tag",bt.toString(10));
                List len=new ArrayList();
                for (int i = tag.size(); i < l.size(); i++) {//len
                    BigInteger b=new BigInteger((String) l.get(i),2);
                    if(bq.compareTo(b)==1){//小于则返回-1，等于则返回0，大于则返回1
                        len.add(b.toString(16));
                        break;
                    }else {
                        len.add(b.toString(16));
                    }
                }
                m.put("lenData",len);
                StringBuffer sl=new StringBuffer();
                for (int i = len.size() - 1; i >= 0; i--) {
                    BigInteger b=new BigInteger((String) len.get(i),16);
                    String tString = Long.toBinaryString((b.intValue() & 0xFF) + 0x100).substring(1);//补位
                    if(i==len.size()-1){
                        sl.append(tString);
                    }else {
                        sl.append(tString.substring(1));
                    }
                }
                BigInteger lenData=new BigInteger(String.valueOf(sl),2);
                m.put("len",lenData.toString(10));
                List ss1= (ArrayList) m.get("tagData");
                List ss2= (ArrayList) m.get("lenData");
                int l1=ss1.size()+ss2.size();
                if (l1*2+ Integer.valueOf((String)m.get("len"))*2>s.length()){
                    return null;
                }
                String data=s.substring(l1*2,l1*2+ Integer.valueOf((String)m.get("len"))*2);
                m.put("data",data);
                Map mmm=analysisPB(data);
                if(mmm!=null) {
                    if ("i".equals(mmm.get("type"))) {
                        if (!"".equals(mmm.get("remain"))) {
                            m.put("string", data);
                        }
                    }
                }
                m.put("remain",s.substring(l1*2+ Integer.valueOf((String)m.get("len"))*2,s.length()));//如果剩余
                m.put("type","b");//类型
                return m;
            }
            return null;
        }




    /**
     * 去掉字符串中的空格和换行符
     * @param str
     * @return
     */
    public static String getNonBlankStr(String str) {
        if(str!=null && !"".equals(str)) {
            Pattern pattern = Pattern.compile("\\s*|\t|\r|\n"); //去掉空格符合换行符
            Matcher matcher = pattern.matcher(str);
            String result = matcher.replaceAll("");
            return result;
        }else {
            return str;
        }
    }

    /**
     * 去掉指定特殊字符
     * @param str
     * @return
     */
    public static String StringFilter(String str)throws PatternSyntaxException {
        // String   regEx  =  "[^a-zA-Z0-9]"; // 只允许字母和数字
        // 清除掉所有特殊字符(除了~之外)
        String regEx="[`!@#$%^&*()+=|{}':;',//[//].<>/?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？[g-z][G-Z]]";
        Pattern pattern   =   Pattern.compile(regEx);
        Matcher matcher   =   pattern.matcher(str);
        return   matcher.replaceAll("").trim();
    }


    /**
     * 进制转补位
     * @param num
     * @return
     */
    public static String ten2Hex2(int num) {
        String strHex2 = String.format("%02x", num).toUpperCase();//高位补0
        return strHex2;
    }




}
