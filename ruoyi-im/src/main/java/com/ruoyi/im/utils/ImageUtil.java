package com.ruoyi.im.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {

    public static String getImageAsBase64(String imageUrl) throws IOException {
        // 读取图片数据
        URL url = new URL(imageUrl);
        InputStream inputStream = url.openStream();
        byte[] imageData = inputStream.readAllBytes();

        // 将图片数据编码成Base64格式
        String base64Image = Base64.getEncoder().encodeToString(imageData);

        return base64Image;
    }

    public static String getUrlAsBase64(String url) {
        byte[] urlBytes = url.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(urlBytes);
    }

    private static final int QRCODE_SIZE = 300; // 二维码图片的尺寸

    public static byte[] generateQRCodeImage(String text) throws WriterException, IOException {
        // 创建二维码参数
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 1);

        // 使用ZXing库生成二维码矩阵
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);

        // 创建BufferedImage对象，并设置白色背景
        BufferedImage qrCodeImage = new BufferedImage(QRCODE_SIZE, QRCODE_SIZE, BufferedImage.TYPE_INT_RGB);
        qrCodeImage.createGraphics().setColor(Color.WHITE);
        qrCodeImage.createGraphics().fillRect(0, 0, QRCODE_SIZE, QRCODE_SIZE);

        // 绘制二维码矩阵到BufferedImage对象中
        for (int i = 0; i < QRCODE_SIZE; i++) {
            for (int j = 0; j < QRCODE_SIZE; j++) {
                qrCodeImage.setRGB(i, j, bitMatrix.get(i, j) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // 将BufferedImage对象转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", baos);
        return baos.toByteArray();
    }

}