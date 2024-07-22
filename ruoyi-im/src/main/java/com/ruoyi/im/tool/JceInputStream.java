package com.ruoyi.im.tool;

import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/* compiled from: P */
public final class JceInputStream {
    private ByteBuffer bs;
    protected String sServerEncoding = "GBK";

    /* compiled from: P */
    public static class HeadData {
        public int tag;
        public byte type;

        public void clear() {
            this.type = (byte) 0;
            this.tag = 0;
        }
    }


    //有问题
    public static void main(String[] strArr) {


        // BYTE = (byte) 0;
        // DOUBLE = (byte) 5;
        // FLOAT = (byte) 4;
        // INT = (byte) 2;
        //JCE_MAX_STRING_LENGTH = 104857600;
        // LIST = (byte) 9;
        // LONG = (byte) 3;
        // MAP = (byte) 8;
        // SHORT = (byte) 1;
        // SIMPLE_LIST = (byte) 13;
        // STRING1 = (byte) 6;
        // STRING4 = (byte) 7;
        // STRUCT_BEGIN = (byte) 10;
        // STRUCT_END = (byte) 11;
        // ZERO_TAG = (byte) 12;



        //   public long read(long j, int i, boolean z) {
        //        if (skipToTag(i)) {
        //            int i2 = 0;
        //            HeadData headData = new HeadData();
        //            readHead(headData);
        //            byte b = headData.type;
        //            if (b == (byte) 0) {
        //                i2 = this.bs.get();
        //            } else if (b == (byte) 1) {
        //                i2 = this.bs.getShort();
        //            } else if (b == (byte) 2) {
        //                i2 = this.bs.getInt();
        //            } else if (b == (byte) 3) {
        //                return this.bs.getLong();
        //            } else {
        //                if (b == (byte) 12) {
        //                    return 0;
        //                }
        //                System.out.println("type mismatch.");
        //            }
        //            return (long) i2;
        //        } else if (!z) {
        //            return j;
        //        } else {
        //            System.out.println("require field not exist.");
        //        }
        //        return 0;
        //    }



        String data = "34560E4940";

        JceInputStream jceInputStream=new JceInputStream(AllToByte.hexToByte(data));


        HeadData headData=new HeadData();

        int a=jceInputStream.readHead(headData,jceInputStream.bs);

        System.out.println("类型:"+headData.type);//类型
        System.out.println("头:"+headData.tag);//头


        System.out.println("内容:"+jceInputStream.bs.getInt());//内容
        System.out.println("长度:"+a);//内容

        System.out.println(ByteToAll.byteToHxe(jceInputStream.bs.array()));


//        System.out.println(a);


    }

    public JceInputStream(ByteBuffer byteBuffer) {
        this.bs = byteBuffer;
    }

    public JceInputStream(byte[] bArr) {
        this.bs = ByteBuffer.wrap(bArr);
    }

    public JceInputStream(byte[] bArr, int i) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        this.bs = wrap;
        wrap.position(i);
    }

    public void warp(byte[] bArr) {
        wrap(bArr);
    }

    public void wrap(byte[] bArr) {
        this.bs = ByteBuffer.wrap(bArr);
    }

    public static int readHead(HeadData headData, ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        headData.type = (byte) (b & 15);
        headData.tag = (b & 240) >> 4;
        if (headData.tag != 15) {
            return 1;
        }
        headData.tag = byteBuffer.get() & 255;
        return 2;
    }

    public void readHead(HeadData headData) {
        readHead(headData, this.bs);
    }

    private int peakHead(HeadData headData) {
        return readHead(headData, this.bs.duplicate());
    }

    private void skip(int i) {
        ByteBuffer byteBuffer = this.bs;
        byteBuffer.position(byteBuffer.position() + i);
    }

    public boolean skipToTag(int r6) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.qq.taf.jce.JceInputStream.skipToTag(int):boolean. bs: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JavaClass.getCode(JavaClass.java:48)
*/
        /*
        r5 = this;
        r0 = 0;
        r1 = new com.qq.taf.jce.JceInputStream$HeadData;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        r1.<init>();	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x0006:
        r2 = r5.peakHead(r1);	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        r3 = r1.type;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        r4 = 11;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        if (r3 != r4) goto L_0x0011;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x0010:
        return r0;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x0011:
        r3 = r1.tag;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        if (r6 > r3) goto L_0x001b;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x0015:
        r1 = r1.tag;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        if (r6 != r1) goto L_0x001a;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x0019:
        r0 = 1;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x001a:
        return r0;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
    L_0x001b:
        r5.skip(r2);	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        r2 = r1.type;	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        r5.skipField(r2);	 Catch:{ JceDecodeException -> 0x0024, JceDecodeException -> 0x0024 }
        goto L_0x0006;
    L_0x0024:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qq.taf.jce.JceInputStream.skipToTag(int):boolean");
    }

    public void skipToStructEnd() {
        HeadData headData = new HeadData();
        do {
            readHead(headData);
            skipField(headData.type);
        } while (headData.type != (byte) 11);
    }

    private void skipField() {
        HeadData headData = new HeadData();
        readHead(headData);
        skipField(headData.type);
    }

    private void skipField(byte b) {
        int i = 0;
        int i2;
        switch (b) {
            case (byte) 0:
                skip(1);
                return;
            case (byte) 1:
                skip(2);
                return;
            case (byte) 2:
                skip(4);
                return;
            case (byte) 3:
                skip(8);
                return;
            case (byte) 4:
                skip(4);
                return;
            case (byte) 5:
                skip(8);
                return;
            case (byte) 6:
                i2 = this.bs.get();
                if (i2 < 0) {
                    i2 += 256;
                }
                skip(i2);
                return;
            case (byte) 7:
                skip(this.bs.getInt());
                return;
            case (byte) 8:
                i2 = read(0, 0, true);
                while (i < i2 * 2) {
                    skipField();
                    i++;
                }
                return;
            case (byte) 9:
                i2 = read(0, 0, true);
                while (i < i2) {
                    skipField();
                    i++;
                }
                return;
            case (byte) 10:
                skipToStructEnd();
                return;
            case (byte) 11:
            case (byte) 12:
                return;
            case (byte) 13:
                HeadData headData = new HeadData();
                readHead(headData);
                if (headData.type == (byte) 0) {
                    skip(read(0, 0, true));
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("skipField with invalid type, type value: ");
                stringBuilder.append(b);
                stringBuilder.append(", ");
                stringBuilder.append(headData.type);
                System.out.println(stringBuilder.toString());
            default:
                System.out.println("invalid type.");
        }
    }

    public boolean read(boolean z, int i, boolean z2) {
        return read((byte) 0, i, z2) != (byte) 0;
    }

    public byte read(byte b, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            b = headData.type;
            if (b == (byte) 0) {
                return this.bs.get();
            }
            if (b == (byte) 12) {
                return (byte) 0;
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return b;
        } else {
            System.out.println("require field not exist.");
        }
        return 0;
    }

    public short read(short s, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            if (b == (byte) 0) {
                return (short) this.bs.get();
            }
            if (b == (byte) 1) {
                return this.bs.getShort();
            }
            if (b == (byte) 12) {
                return (short) 0;
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return s;
        } else {
            System.out.println("require field not exist.");
        }
        return 0;
    }

    public int read(int i, int i2, boolean z) {
        if (skipToTag(i2)) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            if (b == (byte) 0) {
                return this.bs.get();
            }
            if (b == (byte) 1) {
                return this.bs.getShort();
            }
            if (b == (byte) 2) {
                return this.bs.getInt();
            }
            if (b == (byte) 12) {
                return 0;
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return i;
        } else {
            System.out.println("require field not exist.");
        }
        return 0;
    }

    public long read(long j, int i, boolean z) {
        if (skipToTag(i)) {
            int i2 = 0;
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            if (b == (byte) 0) {
                i2 = this.bs.get();
            } else if (b == (byte) 1) {
                i2 = this.bs.getShort();
            } else if (b == (byte) 2) {
                i2 = this.bs.getInt();
            } else if (b == (byte) 3) {
                return this.bs.getLong();
            } else {
                if (b == (byte) 12) {
                    return 0;
                }
                System.out.println("type mismatch.");
            }
            return (long) i2;
        } else if (!z) {
            return j;
        } else {
            System.out.println("require field not exist.");
        }
        return 0;
    }

    public float read(float f, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            if (b == (byte) 4) {
                return this.bs.getFloat();
            }
            if (b == (byte) 12) {
                return 0.0f;
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return f;
        } else {
            System.out.println("require field not exist.");
        }
        return 0;
    }

    public double read(double d, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            if (b == (byte) 4) {
                return (double) this.bs.getFloat();
            }
            if (b == (byte) 5) {
                return this.bs.getDouble();
            }
            if (b == (byte) 12) {
                return 0.0d;
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return d;
        } else {
            System.out.println("require field not exist.");
        }
        return 0;
    }

    public String readByteString(String str, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            int i2;
            byte[] bArr;
            if (b == (byte) 6) {
                i2 = this.bs.get();
                if (i2 < 0) {
                    i2 += 256;
                }
                bArr = new byte[i2];
                this.bs.get(bArr);
                return ByteToAll.byteToText(bArr);
            } else if (b == (byte) 7) {
                i2 = this.bs.getInt();
                if (i2 > 104857600 || i2 < 0 || i2 > this.bs.capacity()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("String too long: ");
                    stringBuilder.append(i2);
                    System.out.println(stringBuilder.toString());
                }
                bArr = new byte[i2];
                this.bs.get(bArr);
                return ByteToAll.byteToText(bArr);
            } else {
                System.out.println("type mismatch.");
        }
        return null;
    } else if (!z) {
            return str;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public String read(String r2, int r3, boolean r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.qq.taf.jce.JceInputStream.read(java.lang.String, int, boolean):java.lang.String. bs: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JavaClass.getCode(JavaClass.java:48)
*/
        /*
        r1 = this;
        r3 = r1.skipToTag(r3);
        if (r3 == 0) goto L_0x0080;
    L_0x0006:
        r2 = new com.qq.taf.jce.JceInputStream$HeadData;
        r2.<init>();
        r1.readHead(r2);
        r2 = r2.type;
        r3 = 6;
        if (r2 == r3) goto L_0x0060;
    L_0x0013:
        r3 = 7;
        if (r2 != r3) goto L_0x0057;
    L_0x0016:
        r2 = r1.bs;
        r2 = r2.getInt();
        r3 = 104857600; // 0x6400000 float:3.6111186E-35 double:5.1806538E-316;
        if (r2 > r3) goto L_0x003f;
    L_0x0020:
        if (r2 < 0) goto L_0x003f;
    L_0x0022:
        r3 = r1.bs;
        r3 = r3.capacity();
        if (r2 > r3) goto L_0x003f;
    L_0x002a:
        r2 = new byte[r2];
        r3 = r1.bs;
        r3.get(r2);
        r3 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0039 }
        r4 = r1.sServerEncoding;	 Catch:{ UnsupportedEncodingException -> 0x0039 }
        r3.<init>(r2, r4);	 Catch:{ UnsupportedEncodingException -> 0x0039 }
        goto L_0x007e;
    L_0x0039:
        r3 = new java.lang.String;
        r3.<init>(r2);
        goto L_0x007e;
    L_0x003f:
        r3 = new com.qq.taf.jce.JceDecodeException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = "String too long: ";
        r4.append(r0);
        r4.append(r2);
        r2 = r4.toString();
        r3.<init>(r2);
        throw r3;
    L_0x0057:
        r2 = new com.qq.taf.jce.JceDecodeException;
        r3 = "type mismatch.";
        r2.<init>(r3);
        throw r2;
    L_0x0060:
        r2 = r1.bs;
        r2 = r2.get();
        if (r2 >= 0) goto L_0x006a;
    L_0x0068:
        r2 = r2 + 256;
    L_0x006a:
        r2 = new byte[r2];
        r3 = r1.bs;
        r3.get(r2);
        r3 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0079 }
        r4 = r1.sServerEncoding;	 Catch:{ UnsupportedEncodingException -> 0x0079 }
        r3.<init>(r2, r4);	 Catch:{ UnsupportedEncodingException -> 0x0079 }
        goto L_0x007e;
    L_0x0079:
        r3 = new java.lang.String;
        r3.<init>(r2);
    L_0x007e:
        r2 = r3;
        goto L_0x0082;
    L_0x0080:
        if (r4 != 0) goto L_0x0083;
    L_0x0082:
        return r2;
    L_0x0083:
        r2 = new com.qq.taf.jce.JceDecodeException;
        r3 = "require field not exist.";
        r2.<init>(r3);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qq.taf.jce.JceInputStream.read(java.lang.String, int, boolean):java.lang.String");
    }

    public String readString(int r3, boolean r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.qq.taf.jce.JceInputStream.readString(int, boolean):java.lang.String. bs: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JavaClass.getCode(JavaClass.java:48)
*/
        /*
        r2 = this;
        r3 = r2.skipToTag(r3);
        if (r3 == 0) goto L_0x007f;
    L_0x0006:
        r3 = new com.qq.taf.jce.JceInputStream$HeadData;
        r3.<init>();
        r2.readHead(r3);
        r3 = r3.type;
        r4 = 6;
        if (r3 == r4) goto L_0x0060;
    L_0x0013:
        r4 = 7;
        if (r3 != r4) goto L_0x0057;
    L_0x0016:
        r3 = r2.bs;
        r3 = r3.getInt();
        r4 = 104857600; // 0x6400000 float:3.6111186E-35 double:5.1806538E-316;
        if (r3 > r4) goto L_0x003f;
    L_0x0020:
        if (r3 < 0) goto L_0x003f;
    L_0x0022:
        r4 = r2.bs;
        r4 = r4.capacity();
        if (r3 > r4) goto L_0x003f;
    L_0x002a:
        r3 = new byte[r3];
        r4 = r2.bs;
        r4.get(r3);
        r4 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0039 }
        r0 = r2.sServerEncoding;	 Catch:{ UnsupportedEncodingException -> 0x0039 }
        r4.<init>(r3, r0);	 Catch:{ UnsupportedEncodingException -> 0x0039 }
        goto L_0x0082;
    L_0x0039:
        r4 = new java.lang.String;
        r4.<init>(r3);
        goto L_0x0082;
    L_0x003f:
        r4 = new com.qq.taf.jce.JceDecodeException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "String too long: ";
        r0.append(r1);
        r0.append(r3);
        r3 = r0.toString();
        r4.<init>(r3);
        throw r4;
    L_0x0057:
        r3 = new com.qq.taf.jce.JceDecodeException;
        r4 = "type mismatch.";
        r3.<init>(r4);
        throw r3;
    L_0x0060:
        r3 = r2.bs;
        r3 = r3.get();
        if (r3 >= 0) goto L_0x006a;
    L_0x0068:
        r3 = r3 + 256;
    L_0x006a:
        r3 = new byte[r3];
        r4 = r2.bs;
        r4.get(r3);
        r4 = new java.lang.String;	 Catch:{ UnsupportedEncodingException -> 0x0079 }
        r0 = r2.sServerEncoding;	 Catch:{ UnsupportedEncodingException -> 0x0079 }
        r4.<init>(r3, r0);	 Catch:{ UnsupportedEncodingException -> 0x0079 }
        goto L_0x0082;
    L_0x0079:
        r4 = new java.lang.String;
        r4.<init>(r3);
        goto L_0x0082;
    L_0x007f:
        if (r4 != 0) goto L_0x0083;
    L_0x0081:
        r4 = 0;
    L_0x0082:
        return r4;
    L_0x0083:
        r3 = new com.qq.taf.jce.JceDecodeException;
        r4 = "require field not exist.";
        r3.<init>(r4);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qq.taf.jce.JceInputStream.readString(int, boolean):java.lang.String");
    }

    public String[] read(String[] strArr, int i, boolean z) {
        return (String[]) readArray((Object[]) strArr, i, z);
    }

    public Map<String, String> readStringMap(int i, boolean z) {
        Map hashMap = new HashMap();
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 8) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    for (int i2 = 0; i2 < read; i2++) {
                        hashMap.put(readString(0, true), readString(1, true));
        }
        return null;
    } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("size invalid: ");
                    stringBuilder.append(read);
                    System.out.println(stringBuilder.toString());
        }
        return null;
    }
            System.out.println("type mismatch.");
        } else if (z) {
            System.out.println("require field not exist.");
        }
        return hashMap;
    }

    public <K, V> HashMap<K, V> readMap(Map<K, V> map, int i, boolean z) {
        return (HashMap) readMap(new HashMap(), map, i, z);
    }

    private <K, V> Map<Object, Object> readMap(Map<Object, Object> map, Map<K, V> map2, int i, boolean z) {
        if (map2 != null) {
            if (!map2.isEmpty()) {
                Entry entry = (Entry) map2.entrySet().iterator().next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (skipToTag(i)) {
                    HeadData headData = new HeadData();
                    readHead(headData);
                    if (headData.type == (byte) 8) {
                        int read = read(0, 0, true);
                        if (read >= 0) {
                            for (int i2 = 0; i2 < read; i2++) {
                                map.put(read(key, 0, true), read(value, 1, true));
        }
        return null;
    } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("size invalid: ");
                            stringBuilder.append(read);
                            System.out.println(stringBuilder.toString());
        }
        return null;
    }
                    System.out.println("type mismatch.");
                } else if (z) {
                    System.out.println("require field not exist.");
                }
                return map;
        }
        return null;
    }
        return new HashMap();
    }

    public List readList(int i, boolean z) {
        List arrayList = new ArrayList();
//        if (skipToTag(i)) {
        if (true) {
            HeadData headData = new HeadData();
            readHead(headData);
            String str = "type mismatch.";
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    for (int i2 = 0; i2 < read; i2++) {
                        HeadData headData2 = new HeadData();
                        readHead(headData2);
                        switch (headData2.type) {
                            case (byte) 0:
                                skip(1);
                                break;
                            case (byte) 1:
                                skip(2);
                                break;
                            case (byte) 2:
                                skip(4);
                                break;
                            case (byte) 3:
                                skip(8);
                                break;
                            case (byte) 4:
                                skip(4);
                                break;
                            case (byte) 5:
                                skip(8);
                                break;
                            case (byte) 6:
                                int i3 = this.bs.get();
                                if (i3 < 0) {
                                    i3 += 256;
                                }
                                skip(i3);
                                break;
                            case (byte) 7:
                                skip(this.bs.getInt());
                                break;
                            case (byte) 8:
                            case (byte) 9:
                                break;
                            case (byte) 10:
                                try {
                                    JceStruct jceStruct = (JceStruct) Class.forName(JceStruct.class.getName()).getConstructor(new Class[0]).newInstance(new Object[0]);
                                    jceStruct.readFrom(this);
                                    skipToStructEnd();
                                    arrayList.add(jceStruct);
                                    break;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(str);
                                    stringBuilder.append(e);
                                    System.out.println(stringBuilder.toString());
                                }
                            case (byte) 12:
                                arrayList.add(new Integer(0));
                                break;
                            default:
                                System.out.println(str);
        }
        return null;
    }
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("size invalid: ");
                    stringBuilder2.append(read);
                    System.out.println(stringBuilder2.toString());
        }
        return null;
    }
            System.out.println(str);
        } else if (z) {
            System.out.println("require field not exist.");
        }
        return arrayList;
    }

    public boolean[] read(boolean[] zArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    boolean[] zArr2 = new boolean[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        zArr2[i2] = read(zArr2[0], 0, true);
                    }
                    return zArr2;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public byte[] read(byte[] bArr, int i, boolean z) {
//        if (skipToTag(i)) {
        if (true) {
            HeadData headData = new HeadData();
            readHead(headData);
            byte b = headData.type;
            if (b == (byte) 9) {
                int read = read(0, 0, true);
                if (read < 0 || read > this.bs.capacity()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("size invalid: ");
                    stringBuilder.append(read);
                    System.out.println(stringBuilder.toString());
                }
                byte[] bArr2 = new byte[read];
                for (int i2 = 0; i2 < read; i2++) {
                    bArr2[i2] = read(bArr2[0], 0, true);
                }
                return bArr2;
            } else if (b == (byte) 13) {
                HeadData headData2 = new HeadData();
                readHead(headData2);
                String str = ", ";
                String str2 = ", type: ";
                if (headData2.type == (byte) 0) {
                    int read2 = read(0, 0, true);
                    if (read2 < 0 || read2 > this.bs.capacity()) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("invalid size, tag: ");
                        stringBuilder2.append(i);
                        stringBuilder2.append(str2);
                        stringBuilder2.append(headData.type);
                        stringBuilder2.append(str);
                        stringBuilder2.append(headData2.type);
                        stringBuilder2.append(", size: ");
                        stringBuilder2.append(read2);
                        System.out.println(stringBuilder2.toString());
                    }
                    bArr = new byte[read2];
                    this.bs.get(bArr);
                    return bArr;
                }
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("type mismatch, tag: ");
                stringBuilder3.append(i);
                stringBuilder3.append(str2);
                stringBuilder3.append(headData.type);
                stringBuilder3.append(str);
                stringBuilder3.append(headData2.type);
                System.out.println(stringBuilder3.toString());
            } else {
                System.out.println("type mismatch.");
        }
        return null;
    } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public short[] read(short[] sArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    short[] sArr2 = new short[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        sArr2[i2] = read(sArr2[0], 0, true);
                    }
                    return sArr2;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public int[] read(int[] iArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    int[] iArr2 = new int[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        iArr2[i2] = read(iArr2[0], 0, true);
                    }
                    return iArr2;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public long[] read(long[] jArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    long[] jArr2 = new long[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        jArr2[i2] = read(jArr2[0], 0, true);
                    }
                    return jArr2;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public float[] read(float[] fArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    float[] fArr2 = new float[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        fArr2[i2] = read(fArr2[0], 0, true);
                    }
                    return fArr2;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public double[] read(double[] dArr, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    double[] dArr2 = new double[read];
                    for (int i2 = 0; i2 < read; i2++) {
                        dArr2[i2] = read(dArr2[0], 0, true);
                    }
                    return dArr2;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public <T> T[] readArray(T[] tArr, int i, boolean z) {
        if (tArr != null && tArr.length != 0) {
            return readArrayImpl(tArr[0], i, z);
        }
        System.out.println("unable to get type of key and value.");

        return null;
    }

    public <T> List<T> readArray(List<T> list, int i, boolean z) {
        if (list != null) {
            if (!list.isEmpty()) {
                int i2 = 0;
                Object[] readArrayImpl = readArrayImpl(list.get(0), i, z);
                if (readArrayImpl == null) {
                    return null;
                }
                List arrayList = new ArrayList();
                while (i2 < readArrayImpl.length) {
                    arrayList.add(readArrayImpl[i2]);
                    i2++;
                }
                return arrayList;
        }
        return null;
    }
        return new ArrayList();
    }

    private <T> T[] readArrayImpl(T t, int i, boolean z) {
        if (skipToTag(i)) {
            HeadData headData = new HeadData();
            readHead(headData);
            if (headData.type == (byte) 9) {
                int read = read(0, 0, true);
                if (read >= 0) {
                    Object[] objArr = (Object[]) Array.newInstance(t.getClass(), read);
                    for (int i2 = 0; i2 < read; i2++) {
                        objArr[i2] = read((Object) t, 0, true);
                    }
                    return (T[]) objArr;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("size invalid: ");
                stringBuilder.append(read);
                System.out.println(stringBuilder.toString());
            }
            System.out.println("type mismatch.");
        } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public JceStruct directRead(JceStruct jceStruct, int i, boolean z) {
        if (skipToTag(i)) {
            try {
                jceStruct = jceStruct.newInit();
                HeadData headData = new HeadData();
                readHead(headData);
                if (headData.type == (byte) 10) {
                    jceStruct.readFrom(this);
                    skipToStructEnd();
                    return jceStruct;
                }
                System.out.println("type mismatch.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
        }
        return null;
    } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public JceStruct read(JceStruct jceStruct, int i, boolean z) {
        if (skipToTag(i)) {
            try {
                jceStruct = (JceStruct) jceStruct.getClass().newInstance();
                HeadData headData = new HeadData();
                readHead(headData);
                if (headData.type == (byte) 10) {
                    jceStruct.readFrom(this);
                    skipToStructEnd();
                    return jceStruct;
                }
                System.out.println("type mismatch.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
        }
        return null;
    } else if (!z) {
            return null;
        } else {
            System.out.println("require field not exist.");
        }
        return null;
    }

    public JceStruct[] read(JceStruct[] jceStructArr, int i, boolean z) {
        return (JceStruct[]) readArray((Object[]) jceStructArr, i, z);
    }

    public <T> Object read(T t, int i, boolean z) {
        if (t instanceof Byte) {
            return Byte.valueOf(read((byte) 0, i, z));
        }
        if (t instanceof Boolean) {
            return Boolean.valueOf(read(false, i, z));
        }
        if (t instanceof Short) {
            return Short.valueOf(read((short) 0, i, z));
        }
        if (t instanceof Integer) {
            return Integer.valueOf(read(0, i, z));
        }
        if (t instanceof Long) {
            return Long.valueOf(read(0, i, z));
        }
        if (t instanceof Float) {
            return Float.valueOf(read(0.0f, i, z));
        }
        if (t instanceof Double) {
            return Double.valueOf(read(0.0d, i, z));
        }
        if (t instanceof String) {
            return readString(i, z);
        }
        if (t instanceof Map) {
            return readMap((Map) t, i, z);
        }
        if (t instanceof List) {
            return readArray((List) t, i, z);
        }
        if (t instanceof JceStruct) {
            return read((JceStruct) t, i, z);
        }
        if (t.getClass().isArray()) {
            if (!(t instanceof byte[])) {
                if (!(t instanceof Byte[])) {
                    if (t instanceof boolean[]) {
                        return read((boolean[]) null, i, z);
                    }
                    if (t instanceof short[]) {
                        return read((short[]) null, i, z);
                    }
                    if (t instanceof int[]) {
                        return read((int[]) null, i, z);
                    }
                    if (t instanceof long[]) {
                        return read((long[]) null, i, z);
                    }
                    if (t instanceof float[]) {
                        return read((float[]) null, i, z);
                    }
                    if (t instanceof double[]) {
                        return read((double[]) null, i, z);
                    }
                    return readArray((Object[]) t, i, z);
        }
        return null;
    }
            return read((byte[]) null, i, z);
        }
        System.out.println("read object error: unsupport type.");

        return null;
    }

    public int setServerEncoding(String str) {
        this.sServerEncoding = str;
        return 0;
    }

    public ByteBuffer getBs() {
        return this.bs;
    }
}
