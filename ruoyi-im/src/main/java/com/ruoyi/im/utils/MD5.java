package com.ruoyi.im.utils;


import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class MD5 {
    static final byte[] PADDING = new byte[]{Byte.MIN_VALUE, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
    static final int S11 = 7;
    static final int S12 = 12;
    static final int S13 = 17;
    static final int S14 = 22;
    static final int S21 = 5;
    static final int S22 = 9;
    static final int S23 = 14;
    static final int S24 = 20;
    static final int S31 = 4;
    static final int S32 = 11;
    static final int S33 = 16;
    static final int S34 = 23;
    static final int S41 = 6;
    static final int S42 = 10;
    static final int S43 = 15;
    static final int S44 = 21;
    private byte[] buffer = new byte[64];
    private long[] count = new long[2];
    private byte[] digest = new byte[16];
    public String digestHexStr;
    private long[] state = new long[4];

    public byte[] getMD5(byte[] bArr) {
        md5Init();
        md5Update(new ByteArrayInputStream(bArr), (long) bArr.length);
        md5Final();
        return this.digest;
    }

    public byte[] getMD5(InputStream inputStream, long j) {
        md5Init();
        if (!md5Update(inputStream, j)) {
            return new byte[16];
        }
        md5Final();
        return this.digest;
    }

    public MD5() {
        md5Init();
    }

    private void md5Init() {
        this.count[0] = 0;
        this.count[1] = 0;
        this.state[0] = 1732584193;
        this.state[1] = 4023233417L;
        this.state[2] = 2562383102L;
        this.state[3] = 271733878;
    }

    /* renamed from: F */
    private long m12820F(long j, long j2, long j3) {
        return (j & j2) | ((-1 ^ j) & j3);
    }

    /* renamed from: G */
    private long m12821G(long j, long j2, long j3) {
        return (j & j3) | ((-1 ^ j3) & j2);
    }

    /* renamed from: H */
    private long m12822H(long j, long j2, long j3) {
        return (j ^ j2) ^ j3;
    }

    /* renamed from: I */
    private long m12823I(long j, long j2, long j3) {
        return ((-1 ^ j3) | j) ^ j2;
    }

    private long FF(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        long F = ((m12820F(j2, j3, j4) + j5) + j7) + j;
        return ((long) ((((int) F) >>> ((int) (32 - j6))) | (((int) F) << ((int) j6)))) + j2;
    }

    private long GG(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        long G = ((m12821G(j2, j3, j4) + j5) + j7) + j;
        return ((long) ((((int) G) >>> ((int) (32 - j6))) | (((int) G) << ((int) j6)))) + j2;
    }

    private long HH(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        long H = ((m12822H(j2, j3, j4) + j5) + j7) + j;
        return ((long) ((((int) H) >>> ((int) (32 - j6))) | (((int) H) << ((int) j6)))) + j2;
    }

    private long II(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        long I = ((m12823I(j2, j3, j4) + j5) + j7) + j;
        return ((long) ((((int) I) >>> ((int) (32 - j6))) | (((int) I) << ((int) j6)))) + j2;
    }

    private boolean md5Update(InputStream inputStream, long j) {
        byte[] bArr;
        int i;
        byte[] bArr2 = new byte[64];
        int i2 = ((int) (this.count[0] >>> 3)) & 63;
        long[] jArr = this.count;
        long j2 = jArr[0] + (j << 3);
        jArr[0] = j2;
        if (j2 < (j << 3)) {
            jArr = this.count;
            jArr[1] = jArr[1] + 1;
        }
        jArr = this.count;
        jArr[1] = jArr[1] + (j >>> 29);
        int i3 = 64 - i2;
        if (j >= ((long) i3)) {
            bArr = new byte[i3];
            try {
                inputStream.read(bArr, 0, i3);
                md5Memcpy(this.buffer, bArr, i2, 0, i3);
                md5Transform(this.buffer);
                i = i3;
                while (((long) (i + 63)) < j) {
                    try {
                        inputStream.read(bArr2);
                        md5Transform(bArr2);
                        i += 64;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                i2 = 0;
            } catch (Exception e2) {
                e2.printStackTrace();
                return false;
            }
        }
        i = 0;
        bArr = new byte[((int) (j - ((long) i)))];
        try {
            inputStream.read(bArr);
            md5Memcpy(this.buffer, bArr, i2, 0, bArr.length);
            return true;
        } catch (Exception e22) {
            e22.printStackTrace();
            return false;
        }
    }

    private void md5Update(byte[] bArr, int i) {
        int i2 = 0;
        byte[] bArr2 = new byte[64];
        int i3 = ((int) (this.count[0] >>> 3)) & 63;
        long[] jArr = this.count;
        long j = jArr[0] + ((long) (i << 3));
        jArr[0] = j;
        if (j < ((long) (i << 3))) {
            jArr = this.count;
            jArr[1] = jArr[1] + 1;
        }
        jArr = this.count;
        jArr[1] = jArr[1] + ((long) (i >>> 29));
        int i4 = 64 - i3;
        if (i >= i4) {
            md5Memcpy(this.buffer, bArr, i3, 0, i4);
            md5Transform(this.buffer);
            while (i4 + 63 < i) {
                md5Memcpy(bArr2, bArr, 0, i4, 64);
                md5Transform(bArr2);
                i4 += 64;
            }
            i3 = 0;
            i2 = i4;
        }
        md5Memcpy(this.buffer, bArr, i3, i2, i - i2);
    }

    private void md5Final() {
        byte[] bArr = new byte[8];
        Encode(bArr, this.count, 8);
        int i = ((int) (this.count[0] >>> 3)) & 63;
        md5Update(PADDING, i < 56 ? 56 - i : 120 - i);
        md5Update(bArr, 8);
        Encode(this.digest, this.state, 16);
    }

    private void md5Memcpy(byte[] bArr, byte[] bArr2, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            bArr[i + i4] = bArr2[i2 + i4];
        }
    }

    private void md5Transform(byte[] bArr) {
        long j = this.state[0];
        long j2 = this.state[1];
        long j3 = this.state[2];
        long j4 = this.state[3];
        long[] jArr = new long[16];
        Decode(jArr, bArr, 64);
        long FF = FF(j, j2, j3, j4, jArr[0], 7, 3614090360L);
        long FF2 = FF(j4, FF, j2, j3, jArr[1], 12, 3905402710L);
        long FF3 = FF(j3, FF2, FF, j2, jArr[2], 17, 606105819);
        long FF4 = FF(j2, FF3, FF2, FF, jArr[3], 22, 3250441966L);
        long FF5 = FF(FF, FF4, FF3, FF2, jArr[4], 7, 4118548399L);
        long FF6 = FF(FF2, FF5, FF4, FF3, jArr[5], 12, 1200080426);
        long FF7 = FF(FF3, FF6, FF5, FF4, jArr[6], 17, 2821735955L);
        long FF8 = FF(FF4, FF7, FF6, FF5, jArr[7], 22, 4249261313L);
        long FF9 = FF(FF5, FF8, FF7, FF6, jArr[8], 7, 1770035416);
        j2 = FF(FF6, FF9, FF8, FF7, jArr[9], 12, 2336552879L);
        long FF10 = FF(FF7, j2, FF9, FF8, jArr[10], 17, 4294925233L);
        long FF11 = FF(FF8, FF10, j2, FF9, jArr[11], 22, 2304563134L);
        long FF12 = FF(FF9, FF11, FF10, j2, jArr[12], 7, 1804603682);
        j2 = FF(j2, FF12, FF11, FF10, jArr[13], 12, 4254626195L);
        FF3 = FF(FF10, j2, FF12, FF11, jArr[14], 17, 2792965006L);
        FF11 = FF(FF11, FF3, j2, FF12, jArr[15], 22, 1236535329);
        FF12 = GG(FF12, FF11, FF3, j2, jArr[1], 5, 4129170786L);
        long GG = GG(j2, FF12, FF11, FF3, jArr[6], 9, 3225465664L);
        j2 = GG(FF3, GG, FF12, FF11, jArr[11], 14, 643717713);
        long GG2 = GG(FF11, j2, GG, FF12, jArr[0], 20, 3921069994L);
        long GG3 = GG(FF12, GG2, j2, GG, jArr[5], 5, 3593408605L);
        FF7 = GG(GG, GG3, GG2, j2, jArr[10], 9, 38016083);
        j2 = GG(j2, FF7, GG3, GG2, jArr[15], 14, 3634488961L);
        FF3 = GG(GG2, j2, FF7, GG3, jArr[4], 20, 3889429448L);
        GG3 = GG(GG3, FF3, j2, FF7, jArr[9], 5, 568446438);
        FF7 = GG(FF7, GG3, FF3, j2, jArr[14], 9, 3275163606L);
        FF8 = GG(j2, FF7, GG3, FF3, jArr[3], 14, 4107603335L);
        j2 = GG(FF3, FF8, FF7, GG3, jArr[8], 20, 1163531501);
        FF3 = GG(GG3, j2, FF8, FF7, jArr[13], 5, 2850285829L);
        FF7 = GG(FF7, FF3, j2, FF8, jArr[2], 9, 4243563512L);
        FF8 = GG(FF8, FF7, FF3, j2, jArr[7], 14, 1735328473);
        FF9 = GG(j2, FF8, FF7, FF3, jArr[12], 20, 2368359562L);
        j2 = HH(FF3, FF9, FF8, FF7, jArr[5], 4, 4294588738L);
        FF10 = HH(FF7, j2, FF9, FF8, jArr[8], 11, 2272392833L);
        FF11 = HH(FF8, FF10, j2, FF9, jArr[11], 16, 1839030562);
        FF12 = HH(FF9, FF11, FF10, j2, jArr[14], 23, 4259657740L);
        j2 = HH(j2, FF12, FF11, FF10, jArr[1], 4, 2763975236L);
        FF3 = HH(FF10, j2, FF12, FF11, jArr[4], 11, 1272893353);
        FF11 = HH(FF11, FF3, j2, FF12, jArr[7], 16, 4139469664L);
        FF12 = HH(FF12, FF11, FF3, j2, jArr[10], 23, 3200236656L);
        GG = HH(j2, FF12, FF11, FF3, jArr[13], 4, 681279174);
        j2 = HH(FF3, GG, FF12, FF11, jArr[0], 11, 3936430074L);
        GG2 = HH(FF11, j2, GG, FF12, jArr[3], 16, 3572445317L);
        GG3 = HH(FF12, GG2, j2, GG, jArr[6], 23, 76029189);
        FF7 = HH(GG, GG3, GG2, j2, jArr[9], 4, 3654602809L);
        j2 = HH(j2, FF7, GG3, GG2, jArr[12], 11, 3873151461L);
        FF3 = HH(GG2, j2, FF7, GG3, jArr[15], 16, 530742520);
        GG3 = HH(GG3, FF3, j2, FF7, jArr[2], 23, 3299628645L);
        FF7 = II(FF7, GG3, FF3, j2, jArr[0], 6, 4096336452L);
        FF8 = II(j2, FF7, GG3, FF3, jArr[7], 10, 1126891415);
        j2 = II(FF3, FF8, FF7, GG3, jArr[14], 15, 2878612391L);
        FF3 = II(GG3, j2, FF8, FF7, jArr[5], 21, 4237533241L);
        FF7 = II(FF7, FF3, j2, FF8, jArr[12], 6, 1700485571);
        FF8 = II(FF8, FF7, FF3, j2, jArr[3], 10, 2399980690L);
        FF9 = II(j2, FF8, FF7, FF3, jArr[10], 15, 4293915773L);
        j2 = II(FF3, FF9, FF8, FF7, jArr[1], 21, 2240044497L);
        FF10 = II(FF7, j2, FF9, FF8, jArr[8], 6, 1873313359);
        FF11 = II(FF8, FF10, j2, FF9, jArr[15], 10, 4264355552L);
        FF12 = II(FF9, FF11, FF10, j2, jArr[6], 15, 2734768916L);
        j2 = II(j2, FF12, FF11, FF10, jArr[13], 21, 1309151649);
        FF3 = II(FF10, j2, FF12, FF11, jArr[4], 6, 4149444226L);
        FF11 = II(FF11, FF3, j2, FF12, jArr[11], 10, 3174756917L);
        FF12 = II(FF12, FF11, FF3, j2, jArr[2], 15, 718787259);
        long II = II(j2, FF12, FF11, FF3, jArr[9], 21, 3951481745L);
        long[] jArr2 = this.state;
        jArr2[0] = jArr2[0] + FF3;
        jArr2 = this.state;
        jArr2[1] = II + jArr2[1];
        long[] jArr3 = this.state;
        jArr3[2] = jArr3[2] + FF12;
        jArr3 = this.state;
        jArr3[3] = jArr3[3] + FF11;
    }

    private void Encode(byte[] bArr, long[] jArr, int i) {
        int i2 = 0;
        int length = jArr.length;
//        util.LOGI("Encode " + length + " len:" + i, "");
        int i3 = 0;
        while (i2 < i) {
            if (i3 >= length) {
//                util.LOGI("Encode index:" + i3, "");
            }
            bArr[i2] = (byte) ((int) (jArr[i3] & 255));
            bArr[i2 + 1] = (byte) ((int) ((jArr[i3] >>> 8) & 255));
            bArr[i2 + 2] = (byte) ((int) ((jArr[i3] >>> 16) & 255));
            bArr[i2 + 3] = (byte) ((int) ((jArr[i3] >>> 24) & 255));
            i3++;
            i2 += 4;
        }
    }

    private void Decode(long[] jArr, byte[] bArr, int i) {
        int i2 = 0;
        int i3 = 0;
        while (i2 < i) {
            jArr[i3] = ((b2iu(bArr[i2]) | (b2iu(bArr[i2 + 1]) << 8)) | (b2iu(bArr[i2 + 2]) << 16)) | (b2iu(bArr[i2 + 3]) << 24);
            i3++;
            i2 += 4;
        }
    }

    public static long b2iu(byte b) {
        return b < (byte) 0 ? (long) (b & 255) : (long) b;
    }

    public static String byteHEX(byte b) {
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        return new String(new char[]{cArr[(b >>> 4) & 15], cArr[b & 15]});
    }

    public  byte[] toMD5Byte(byte[] bArr) {
        return new MD5().getMD5(bArr);
    }

    public  byte[] toMD5Byte(String str) {
        byte[] bytes;
        try {
            bytes = str.getBytes("ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            bytes = str.getBytes();
        }
        return new MD5().getMD5(bytes);
    }

    public  byte[] toMD5Byte(InputStream inputStream, long j) {
        return new MD5().getMD5(inputStream, j);
    }

    public static String toMD5(byte[] bArr) {
        byte[] md5 = new MD5().getMD5(bArr);
        String str = "";
        for (int i = 0; i < 16; i++) {
            str = str + byteHEX(md5[i]);
        }
        return str;
    }

    public static String toMD5(String str) {
        byte[] bytes;
        try {
            bytes = str.getBytes("ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            bytes = str.getBytes();
        }
        byte[] md5 = new MD5().getMD5(bytes);
        String str2 = "";
        for (int i = 0; i < 16; i++) {
            str2 = str2 + byteHEX(md5[i]);
        }
        return str2;
    }

    public static String getMD5String(byte[] bArr) {
        int i = 0;
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(bArr);
            byte[] digest = instance.digest();
            char[] cArr2 = new char[32];
            int i2 = 0;
            while (i < 16) {
                byte b = digest[i];
                int i3 = i2 + 1;
                cArr2[i2] = cArr[(b >>> 4) & 15];
                i2 = i3 + 1;
                cArr2[i3] = cArr[b & 15];
                i++;
            }
            return new String(cArr2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileMD5(File file) throws IOException {
        int i = 0;
        if (file == null) {
            try {
                return "";
            } catch (Exception e) {
                printException(e, "");
                return "";
            }
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bArr = new byte[8192];
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            while (true) {
                int read = fileInputStream.read(bArr, 0, bArr.length);
                if (read == -1) {
                    break;
                }
                instance.update(bArr, 0, read);
            }
            fileInputStream.close();
            bArr = instance.digest();
            char[] cArr2 = new char[32];
            int i2 = 0;
            while (i < 16) {
                byte b = bArr[i];
                int i3 = i2 + 1;
                cArr2[i2] = cArr[(b >>> 4) & 15];
                i2 = i3 + 1;
                cArr2[i3] = cArr[b & 15];
                i++;
            }
            return new String(cArr2);
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }



    public static String getFileSHA1(File file) {
        MessageDigest md = null;
        FileInputStream fis = null;
        StringBuilder sha1Str = new StringBuilder();
        try {
            fis = new FileInputStream(file);
            MappedByteBuffer mbb = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            md = MessageDigest.getInstance("SHA-1");
            md.update(mbb);
            byte[] digest = md.digest();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    sha1Str.append(0);
                }
                sha1Str.append(shaHex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return sha1Str.toString();
    }



    public static String getByteSHA1(byte[] file) {
        FileInputStream fis = null;
        StringBuilder sha1Str = new StringBuilder();
        try {
            byte[] digest = file;
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    sha1Str.append(0);
                }
                sha1Str.append(shaHex);
            }
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return sha1Str.toString();
    }



    public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return byteArray2Hex(md.digest(convertme));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }



    private static String convToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }



    public static String SHA1(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException  {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convToHex(sha1hash);
    }





    public static void printException(Exception exception, String str) throws IOException {
        Writer stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        exception.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
//        LOGW("exception", stringWriter.toString(), str);
    }

    public static void main(String[] args) throws IOException {

        AllToByte allToByte=new AllToByte();
        //280E2336B3CAABC4B054E9AD56648ABE
        //byte[] data = allToByte.hexToByte("23212F43A38B308F0BA3B787767B601458CCB335128D67D47A43E2B56C9CE94CF235A6987ACC40F4270A5FEC538D92E26D6AE8104E7B0376BB4A9408321C26BEB515F6943D3F559BEB95E87B566D991F2E1DB68E2FF25F66593286187C5EE7DA65A28B1A39DFFA5A37F08A4775B6A02CFD863C2F8389FF1126091FA3AF68697A");



        File file=new File("C:\\Users\\com.uohe\\Desktop\\1093744158\\ev_2022-01-22_10_02_42.mp4");
        String url=("C:\\Users\\com.uohe\\Desktop\\1093744158\\ev_2022-01-22_10_02_42.mp4");

        System.out.println(url.substring(url.lastIndexOf(".")));
        System.out.println("----------------");
//        System.out.println(getMD5String(data));
//        System.out.println("----------------");







        ByteToAll byteToAll=new ByteToAll();

//        byte[]  sub = readFileContent(file.getPath());
//        System.out.println(getMD5String(sub));

        //byte[] dddd=allToByte.hexToByte("FF D8 FF E1 00 9C 45 78 69 66 00 00 49 49 2A 00 08 00 00 00 07 00 08 92 03 00 01 00 00 00 00 00 00 00 12 01 03 00 01 00 00 00 00 00 00 00 01 01 03 00 01 00 00 00 28 00 00 00 32 01 02 00 14 00 00 00 62 00 00 00 07 92 03 00 01 00 00 00 FF FF FF FF 00 01 03 00 01 00 00 00 64 00 00 00 69 87 04 00 01 00 00 00 76 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 01 02 04 00 01 00 00 00 94 00 00 00 02 02 04 00 01 00 00 00 00 00 00 00 00 00 00 00 FF DB 00 43 00 06 04 05 06 05 04 06 06 05 06 07 07 06 08 0A 10 0A 0A 09 09 0A 14 0E 0F 0C 10 17 14 18 18 17 14 16 16 1A 1D 25 1F 1A 1B 23 1C 16 16 20 2C 20 23 26 27 29 2A 29 19 1F 2D 30 2D 28 30 25 28 29 28 FF DB 00 43 01 07 07 07 0A 08 0A 13 0A 0A 13 28 1A 16 1A 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 28 FF C2 00 11 08 00 28 00 64 03 01 22 00 02 11 01 03 11 01 FF C4 00 18 00 01 01 01 01 01 00 00 00 00 00 00 00 00 00 00 00 00 04 05 02 07 FF C4 00 14 01 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF DA 00 0C 03 01 00 02 10 03 10 00 00 01 F5 40 09 0A D8 9B 60 00 00 00 12 51 D8 C2 DA EC 00 00 00 00 00 00 00 00 1F FF C4 00 1D 10 00 02 02 01 05 00 00 00 00 00 00 00 00 00 00 00 02 04 01 03 14 11 12 30 40 50 FF DA 00 08 01 01 00 01 05 02 E8 29 6E 42 AE 3A 4B DB C4 DD 59 0A 80 C0 0D C9 DB 2F 8E BB 7C 1F FF C4 00 14 11 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 FF DA 00 08 01 03 01 01 3F 01 2F FF C4 00 14 11 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 FF DA 00 08 01 02 01 01 3F 01 2F FF C4 00 22 10 00 02 02 01 03 04 03 00 00 00 00 00 00 00 00 00 01 02 03 11 51 00 12 41 13 14 21 30 32 40 50 FF DA 00 08 01 01 00 06 3F 02 FA 10 CD 5B 7A 88 1E B1 7A 89 7B 76 2A F2 2C 7B CB A8 1E 71 C9 F5 CD 0D ED EA 21 4B C5 E8 2A 00 14 78 00 71 AE E9 24 46 2A B4 A9 22 FC 73 47 8B CD 1D 0D E0 06 E4 03 7F 85 FF C4 00 1E 10 01 01 00 01 04 03 01 00 00 00 00 00 00 00 00 00 01 11 21 00 41 61 81 30 40 51 50 FF DA 00 08 01 01 00 01 3F 21 F4 39 42 2D C1 65 EF 5B F9 96 8F 30 B4 3E 4D 9F 1F 08 45 B8 12 CE F4 17 54 02 00 D8 34 42 FE F9 38 27 E1 52 75 8D 36 F0 06 00 3C 30 BF 85 FF DA 00 0C 03 01 00 02 00 03 00 00 00 10 F3 CF 3C F3 CF 3C B1 CF 3C F3 CF 3C F3 CF 3C F3 FF C4 00 14 11 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 FF DA 00 08 01 03 01 01 3F 10 2F FF C4 00 14 11 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 FF DA 00 08 01 02 01 01 3F 10 2F FF C4 00 1F 10 01 01 00 02 02 01 05 00 00 00 00 00 00 00 00 00 01 11 00 21 31 51 30 40 41 50 81 C1 FF DA 00 08 01 01 00 01 3F 10 F4 0F F4 F6 33 C4 2C D2 C2 F4 66 CD 36 F1 0A A5 87 DB A2 50 2B E2 FD 5E C6 50 B3 69 4B D9 83 05 EF 0A 80 34 00 00 1C 61 26 05 FF 00 DD 2C 66 00 A6 AE 2D 01 B3 51 F6 22 01 B1 42 F4 71 F0 5F FF D9 29");



    }









}
