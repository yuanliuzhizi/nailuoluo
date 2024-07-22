package com.ruoyi.im.utils;


import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * 将个别转换方法方在这
 * 2021-3-17
 * by GuangHeLiZi
 * 更多请鉴
 * */
public class AllToAll {


    /**
     * 回车分段可以连续写入数据
     * @param fileName
     * @return
     */
    public static ArrayList readFileByLines(String fileName) {
        ArrayList l = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {// 显示行号
                l.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    return null;
                }
            }
        }
        return l;
    }



    /**
     * 将Byte数组转换成文件
     * @param bytes byte数组
     * @param filePath 文件路径  如 D://test/ 最后“/”结尾
     * @param fileName  文件名
     */
    public static void fileToBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {

            file = new File(filePath + fileName);
            if (!file.getParentFile().exists()){
                //文件夹不存在 生成
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String reader(String filePath) {
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                while (lineTxt != null) {
                    return lineTxt;
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
//            System.out.println("Cannot find the file specified!");
            e.printStackTrace();
        } catch (IOException e) {
//            System.out.println("Error reading file content!");
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] mapToBytes(Map map) {
        try {
            byte[] bytes = null;
            //map转byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(map);
            bytes = outputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            return null;
        }

    }




}
