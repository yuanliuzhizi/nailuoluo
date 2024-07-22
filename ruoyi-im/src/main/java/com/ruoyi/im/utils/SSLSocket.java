package com.ruoyi.im.utils;


import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 用来实现https的请求
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class SSLSocket {


    private Socket socket;//构造客户端请求

    /**
     * @param ip 目标ip地址。
     * @param port 目标端口地址。
     * @return boolean
     */
    public boolean link(String ip,int port){
        this.socketEnd();
        try{
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            this.socket =socketFactory.createSocket(ip, port);
            this.socket.setSoTimeout(0);//超时时间关闭连接
            return true;
        }catch (Exception e){
            System.err.println("error195:link()连接异常"+e);
            return false;
        }
    }

    /**
     * @param ip 目标ip地址。
     * @param port 目标端口地址。
     * @return boolean
     */
    public boolean link(String ip,int port,boolean ok){
        this.socketEnd();
        try{
            // 自定义的管理器
            X509TrustManager xtm = new Java2000TrustManager();
            TrustManager mytm[] = { xtm };
            // 得到上下文
            SSLContext ctx = SSLContext.getInstance("SSL");
            // 初始化
            ctx.init(null, mytm, null);
            // 获得工厂
            SSLSocketFactory factory = ctx.getSocketFactory();

            // 从工厂获得Socket连接
             this.socket = factory.createSocket(ip, port);
            return true;
        }catch (Exception e){
            System.err.println("error195:link()连接异常"+e);
            return false;
        }
    }

    /**
     * @param sendByte 发送数据。
     * @return boolean
     */
    public synchronized boolean sendData(byte[] sendByte){
        try {
            OutputStream outputStream = this.socket.getOutputStream();
            outputStream.write(sendByte);
            outputStream.flush();
            return true;
        }catch (Exception e){
            System.err.println("error196:sendData()发送异常"+e);
            return false;
        }
    }


    /**
     * @param sendByte 发送数据。
     * @return boolean
     */
    public synchronized boolean sendData1(String sendByte){
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(sendByte);
            out.flush();
            return true;
        }catch (Exception e){
            System.err.println("error196:sendData()发送异常"+e);
            return false;
        }
    }

    public synchronized byte[] getData(){//接收返回数据
        try {

            ByteToAll byteToAll=new ByteToAll();
            AllToByte allToByte=new AllToByte();
            ByteUtils byteUtils=new ByteUtils();
            //得到一个输入流，用于接收服务器响应的数据
            InputStream inputStream = socket.getInputStream();
//            byte[] bytes = new byte[1]; // 一次读取一个byte
            byte[] bytes = new byte[1024];
            byte[] data = new byte[0];
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                data=byteUtils.byteMerger(data,bytes);
            }

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public synchronized byte[] getData1(){//接收返回数据
        try {
            // 剩下的就和普通的Socket操作一样了
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            out.close();
            in.close();
            return AllToByte.textToByte(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public synchronized void socketEnd(){//停止所有链接
        if (this.socket!=null){
            try{
                this.socket.shutdownInput();//需要约定好双方读写完成的条件，然后关闭输入输出流
                this.socket.shutdownOutput();//需要约定好双方读写完成的条件，然后关闭输入输出流
                this.socket.getInputStream().close();//关闭此输入流并释放任何相关的系统资源流。
                this.socket.getOutputStream().close();//关闭此输入流并释放任何相关的系统资源流。
                this.socket.close();//关闭此输入流并释放任何相关的系统资源流。
            }catch (Exception e){
                System.err.println("error198:socketEnd()停止异常");
            }
        }
    }



}

/**
 * 自定义的认证管理类。
 *
 * @author JAVA世纪网(java2000.net)
 *
 */
class Java2000TrustManager implements X509TrustManager {
    Java2000TrustManager() {
        // 这里可以进行证书的初始化操作
    }

    // 检查客户端的可信任状态
    public void checkClientTrusted(X509Certificate chain[], String authType) throws CertificateException {
//        System.out.println("检查客户端的可信任状态...");
    }

    // 检查服务器的可信任状态
    public void checkServerTrusted(X509Certificate chain[], String authType) throws CertificateException {
//        System.out.println("检查服务器的可信任状态");
    }

    // 返回接受的发行商数组
    public X509Certificate[] getAcceptedIssuers() {
//        System.out.println("获取接受的发行商数组...");
        return null;
    }
}