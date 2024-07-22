package com.ruoyi.im.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频操作仅适用win
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class VideoUtils {

//    public static void main(String[] args) {
//        DefaultFFMPEGLocator defaultFFMPEGLocator=new DefaultFFMPEGLocator();
//        String ffmpegEXE=defaultFFMPEGLocator.getFFMPEGExecutablePath();//ffmpeg程序路径
//
//        String fileName="C:\\Users\\com.uohe\\Desktop\\qq图片\\1.mp4";//文件输入源
//        String time="00:00:00";//截取的时间点格式为 00:00:01
//        String outName="C:\\Users\\com.uohe\\Desktop\\qq图片\\123\\11.jpg";//文件输出路径，格式为 11.jpg
//        String size="1920x1080";//截取的图片大小，格式为：1920x1080
//
//        boolean ok=covPic(ffmpegEXE,fileName,time,outName,size);
//        System.out.println(ok);
//    }




    /**
     * 开启线程处理Ffmpeg处理流
     *
     * @param process
     */
    private void dealStream(Process process) {
        if (process == null) {
            return;
        }
        // 处理InputStream的线程
        new Thread() {
            @Override
            public void run() {
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                try {
                    while ((line = in.readLine()) != null) {
                        //log.warn("output: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        // 处理ErrorStream的线程
        new Thread() {
            @Override
            public void run() {
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                try {
                    while ((line = err.readLine()) != null) {
                        //log.warn("output: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        err.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 截取一帧作为图片
     *
     * @param fileName
     *            ffmpeg -i /Users/jinx/Downloads/test.mp4 -y -f image2 -ss
     *            00:00:01 -vframes 1 /Users/jinx/Downloads/test.jpeg
     * @return
     */
    public boolean covPic(String ffmpegEXE,String fileName, String time, String outName, String size) {
        List<String> command = new ArrayList<String>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(fileName);//文件输入源
        command.add("-y");
        command.add("-f");
        command.add("image2");
        command.add("-ss");
        command.add(time);//截取的时间点格式为 00:00:01
        command.add("-vframes");
        command.add("1");
        command.add("-s");
        command.add(size);//截取的图片大小，格式为：1920x1080
        command.add(outName);//文件输出路径，格式为 11.jpg

        try {
            Process videoProcess = new ProcessBuilder(command).start();
            dealStream(videoProcess);
            videoProcess.waitFor();
            return true;
        } catch (Exception e) {
        }
        return false;
    }


}
