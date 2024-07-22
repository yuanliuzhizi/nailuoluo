package com.ruoyi.im.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 本地文件网络文件转成byte[]实现类
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class FileToByte {


    public static byte[] localFileToByte(String localURL) {
        try {
            FileInputStream input = new FileInputStream(localURL);
            // read() 从此输入流中读取一个数据字节。
            // read(byte[] b) 从此输入流中将最多 b.length 个字节的数据读入一个 byte 数组中。
            byte[] data = new byte[input.available()];
            input.read(data);
            input.close();//关闭流
            // 打印
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据地址获得数据的字节流
     * @param networkURL 网络连接地址
     * @return
     */
    public static byte[] networkFileToByte(String networkURL){
        try {
            URL url = new URL(networkURL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);//建立链接时间
            conn.setReadTimeout(5 * 1000);//如果到了指定的时间，没有可能的数据被客户端读取，则报异常
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            byte[] data = readInputStream(inStream);//得到图片的二进制数据
            return data;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获取网络视频
     * @param networkURL 网络连接地址
     * @return
     */
    public static byte[] voiceFileToByte(String networkURL){
        try {
            URL url = new URL(networkURL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Host","www.peiyinwa.com");
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            conn.setRequestProperty("Accept-Encoding","gzip, deflate");
            conn.setRequestProperty("User-Agent","Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)");
            conn.setConnectTimeout(5 * 1000);//建立链接时间
            conn.setReadTimeout(5 * 1000);//如果到了指定的时间，没有可能的数据被客户端读取，则报异常
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            byte[] data = readInputStream(inStream);//得到图片的二进制数据
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static byte[] fileToByte(String readURl){
        return FileToByte.localFileToByte(readURl);
    }



//    public static byte[] fileToByte(String readURl){
//        File file = new File(readURl);
//        if (file.exists()) {//判断是否是本地文件
//            return FileToByte.localFileToByte(readURl);
//        }else {
//            try {
//                byte[] data=networkFileToByte(readURl);
//                if(data==null){
//                    throw new Exception("");
//                }
//                return data;
//            }catch (Exception e){
//                byte[] data=voiceFileToByte(readURl);
//                return data;
//            }
//        }
//    }



    //测试
    public static byte[] localFileToByte1(String localURL,int size) {
        try {
            FileInputStream input = new FileInputStream(localURL);
            // read() 从此输入流中读取一个数据字节。
            // read(byte[] b) 从此输入流中将最多 b.length 个字节的数据读入一个 byte 数组中。
            byte[] data = new byte[size];
            input.read(data);
            // 打印
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}



