package com.ruoyi.im.tool;

import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;

/* compiled from: P */
public class JceOutputStream {
    private ByteBuffer bs;
//    private OnIllegalArgumentException exceptionHandler;
    protected String sServerEncoding;

    public JceOutputStream(ByteBuffer byteBuffer) {
        this.sServerEncoding = "GBK";
        this.bs = byteBuffer;
    }

    public JceOutputStream(int i) {
        this.sServerEncoding = "GBK";
        this.bs = ByteBuffer.allocate(i);
    }

    public JceOutputStream() {
        this(128);
    }

    public ByteBuffer getByteBuffer() {
        return this.bs;
    }

    public byte[] toByteArray() {
        byte[] obj = new byte[this.bs.position()];
        System.arraycopy(this.bs.array(), 0, obj, 0, this.bs.position());
        return obj;
    }

    public void reserve(int i) {
        if (this.bs.remaining() < i) {
            int capacity = (this.bs.capacity() + i) * 2;
            try {
                ByteBuffer allocate = ByteBuffer.allocate(capacity);
                allocate.put(this.bs.array(), 0, this.bs.position());
                this.bs = allocate;
            } catch (IllegalArgumentException e) {
//                OnIllegalArgumentException onIllegalArgumentException = this.exceptionHandler;
//                if (onIllegalArgumentException != null) {
//                    onIllegalArgumentException.onException(e, this.bs, i, capacity);
//                }
                throw e;
            }
        }
    }

    public void writeHead(byte b, int i) {
        if (i < 15) {
            this.bs.put((byte) (b | (i << 4)));
        } else if (i < 256) {
            this.bs.put((byte) (b | 240));
            this.bs.put((byte) i);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("tag is too large: ");
            stringBuilder.append(i);
//            throw new JceEncodeException(stringBuilder.toString());
        }
    }

    public void write(boolean z, int i) {
        write(z, i);
    }

    public void write(byte b, int i) {
        reserve(3);
        if (b == (byte) 0) {
            writeHead((byte) 12, i);
            return;
        }
        writeHead((byte) 0, i);
        this.bs.put(b);
    }

    public void write(short s, int i) {
        reserve(4);
        if (s < (short) -128 || s > (short) 127) {
            writeHead((byte) 1, i);
            this.bs.putShort(s);
            return;
        }
        write((byte) s, i);
    }

    public void write(int i, int i2) {
        reserve(6);
        if (i < -32768 || i > 32767) {
            writeHead((byte) 2, i2);
            this.bs.putInt(i);
            return;
        }
        write((short) i, i2);
    }

//    public void write(long j, int i) {
//        reserve(10);
//        if (j < WV.ENABLE_WEBAIO_SWITCH || j > 2147483647L) {
//            writeHead((byte) 3, i);
//            this.bs.putLong(j);
//            return;
//        }
//        write((int) j, i);
//    }

    public void write(float f, int i) {
        reserve(6);
        writeHead((byte) 4, i);
        this.bs.putFloat(f);
    }

    public void write(double d, int i) {
        reserve(10);
        writeHead((byte) 5, i);
        this.bs.putDouble(d);
    }

    public void writeStringByte(String str, int i) {
        byte[] hexStr2Bytes = AllToByte.textToByte(str);
        reserve(hexStr2Bytes.length + 10);
        if (hexStr2Bytes.length > 255) {
            writeHead((byte) 7, i);
            this.bs.putInt(hexStr2Bytes.length);
            this.bs.put(hexStr2Bytes);
            return;
        }
        writeHead((byte) 6, i);
        this.bs.put((byte) hexStr2Bytes.length);
        this.bs.put(hexStr2Bytes);
    }

    public void writeByteString(String str, int i) {
        reserve(str.length() + 10);
        byte[] hexStr2Bytes = AllToByte.hexToByte(str);
        if (hexStr2Bytes.length > 255) {
            writeHead((byte) 7, i);
            this.bs.putInt(hexStr2Bytes.length);
            this.bs.put(hexStr2Bytes);
            return;
        }
        writeHead((byte) 6, i);
        this.bs.put((byte) hexStr2Bytes.length);
        this.bs.put(hexStr2Bytes);
    }

    public void write(String r3, int r4) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.qq.taf.jce.JceOutputStream.write(java.lang.String, int):void. bs: []
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
        r0 = r2.sServerEncoding;	 Catch:{ UnsupportedEncodingException -> 0x0007 }
        r3 = r3.getBytes(r0);	 Catch:{ UnsupportedEncodingException -> 0x0007 }
        goto L_0x000b;
    L_0x0007:
        r3 = r3.getBytes();
    L_0x000b:
        r0 = r3.length;
        r0 = r0 + 10;
        r2.reserve(r0);
        r0 = r3.length;
        r1 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r0 <= r1) goto L_0x0026;
    L_0x0016:
        r0 = 7;
        r2.writeHead(r0, r4);
        r4 = r2.bs;
        r0 = r3.length;
        r4.putInt(r0);
        r4 = r2.bs;
        r4.put(r3);
        goto L_0x0036;
    L_0x0026:
        r0 = 6;
        r2.writeHead(r0, r4);
        r4 = r2.bs;
        r0 = r3.length;
        r0 = (byte) r0;
        r4.put(r0);
        r4 = r2.bs;
        r4.put(r3);
    L_0x0036:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qq.taf.jce.JceOutputStream.write(java.lang.String, int):void");
    }


    //
    public <K, V> void write(Map<K, V> map, int i) {
        int i2;
        reserve(8);
        writeHead((byte) 8, i);
        if (map == null) {
            i2 = 0;
        } else {
            i2 = map.size();
        }
        write(i2, 0);
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                write(entry.getKey(), 0);
                writeStringByte((String) entry.getValue(), 1);
            }
        }
    }

    public void write(boolean[] zArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(zArr.length, 0);
        for (boolean write : zArr) {
            write(write, 0);
        }
    }

    public void write(byte[] bArr, int i) {
        reserve(bArr.length + 8);
        writeHead((byte) 13, i);
        writeHead((byte) 0, 0);
        write(bArr.length, 0);
        this.bs.put(bArr);
    }

    public void write(short[] sArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(sArr.length, 0);
        for (short write : sArr) {
            write(write, 0);
        }
    }

    public void write(int[] iArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(iArr.length, 0);
        for (int write : iArr) {
            write(write, 0);
        }
    }

    public void write(long[] jArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(jArr.length, 0);
        for (long write : jArr) {
            write(write, 0);
        }
    }

    public void write(float[] fArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(fArr.length, 0);
        for (float write : fArr) {
            write(write, 0);
        }
    }

    public void write(double[] dArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(dArr.length, 0);
        for (double write : dArr) {
            write(write, 0);
        }
    }

    public <T> void write(T[] tArr, int i) {
        writeArray(tArr, i);
    }

    private void writeArray(Object[] objArr, int i) {
        reserve(8);
        writeHead((byte) 9, i);
        write(objArr.length, 0);
        for (Object write : objArr) {
            write(write, 0);
        }
    }

    public <T> void write(Collection<T> collection, int i) {
        int i2;
        reserve(8);
        writeHead((byte) 9, i);//list头(tag)的
        if (collection == null) {
            i2 = 0;
        } else {
            i2 = collection.size();
        }
        write(i2, 0);//这个是jct结构的指定的list大小
        if (collection != null) {
            for (T write : collection) {
                write((Object) write, 0);
            }
        }
    }

//    public void write(JceStruct jceStruct, int i) {
//        reserve(2);
//        writeHead((byte) 10, i);
//        jceStruct.writeTo(this);
//        reserve(2);
//        writeHead((byte) 11, 0);
//    }

    public void write(Byte b, int i) {
        write(b.byteValue(), i);
    }

    public void write(Boolean bool, int i) {
        write(bool.booleanValue(), i);
    }

    public void write(Short sh, int i) {
        write(sh.shortValue(), i);
    }

    public void write(Integer num, int i) {
        write(num.intValue(), i);
    }

    public void write(Long l, int i) {
        write(l.longValue(), i);
    }

    public void write(Float f, int i) {
        write(f.floatValue(), i);
    }

    public void write(Double d, int i) {
        write(d.doubleValue(), i);
    }

    public void write(Object obj, int i) {
        if (obj instanceof Byte) {
            write(((Byte) obj).byteValue(), i);
        } else if (obj instanceof Boolean) {
            write(((Boolean) obj).booleanValue(), i);
        } else if (obj instanceof Short) {
            write(((Short) obj).shortValue(), i);
        } else if (obj instanceof Integer) {
            write(((Integer) obj).intValue(), i);
        } else if (obj instanceof Long) {
            write(((Long) obj).longValue(), i);
        } else if (obj instanceof Float) {
            write(((Float) obj).floatValue(), i);
        } else if (obj instanceof Double) {
            write(((Double) obj).doubleValue(), i);
        } else if (obj instanceof String) {
            writeStringByte((String) obj, i);
        } else if (obj instanceof Map) {
            write((Map) obj, i);
        } else if (obj instanceof List) {
            write((List) obj, i);
        }

//        else if (obj instanceof JceStruct) {
//            write((JceStruct) obj, i);
//        }

        else if (obj instanceof byte[]) {
            write((byte[]) obj, i);
        } else if (obj instanceof boolean[]) {
            write((boolean[]) obj, i);
        } else if (obj instanceof short[]) {
            write((short[]) obj, i);
        } else if (obj instanceof int[]) {
            write((int[]) obj, i);
        } else if (obj instanceof long[]) {
            write((long[]) obj, i);
        } else if (obj instanceof float[]) {
            write((float[]) obj, i);
        } else if (obj instanceof double[]) {
            write((double[]) obj, i);
        } else if (obj.getClass().isArray()) {
            writeArray((Object[]) obj, i);
        } else if (obj instanceof Collection) {
            write((Collection) obj, i);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("write object error: unsupport type. ");
            stringBuilder.append(obj.getClass());
//            throw new JceEncodeException(stringBuilder.toString());
        }
    }

    public int setServerEncoding(String str) {
        this.sServerEncoding = str;
        return 0;
    }


    public static void main(String[] strArr) {
        JceOutputStream jceOutputStream = new JceOutputStream();

        String data = "你好";

//        jceOutputStream.write(AllToByte.textToByte(data),1);
//        jceOutputStream.writeHead((byte)13,1);
//        jceOutputStream.writeStringByte(data,15);
//        jceOutputStream.write(0, 0);
        List list=new ArrayList();
        Collection<List> collection =list;
        list.add("OA");
        jceOutputStream.write(collection, 0);

//        Map map = new HashMap();
//
//        map.put(1, "111");
//
//        jceOutputStream.write(map, 25);
        System.out.println(ByteToAll.byteToHxe(jceOutputStream.toByteArray()));
        System.out.println(Arrays.toString(jceOutputStream.toByteArray()));

//
//        Pack pack = new Pack();
//        int a=12;
//
//        if(a>=15){
//            pack.setByte((byte)(12|240));//填充
//            pack.setByte((byte)a);
//            System.out.println("jce数据："+ByteToAll.byteToHxe(pack.getAll()));
//            byte[] bytes=pack.getAll();
//
//            byte type = (byte) (bytes[0] & 15);
//
//            System.out.println("判断类型:"+type);
//
//            // BYTE = (byte) 0;
//            // DOUBLE = (byte) 5;
//            // FLOAT = (byte) 4;
//            // INT = (byte) 2;
//            //JCE_MAX_STRING_LENGTH = 104857600;
//            // LIST = (byte) 9;
//            // LONG = (byte) 3;
//            // MAP = (byte) 8;
//            // SHORT = (byte) 1;
//            // SIMPLE_LIST = (byte) 13;
//            // STRING1 = (byte) 6;
//            // STRING4 = (byte) 7;
//            // STRUCT_BEGIN = (byte) 10;
//            // STRUCT_END = (byte) 11;
//            // ZERO_TAG = (byte) 12;
//
//
//            for (int i = 0; i < bytes.length; i++) {
//                int tag = ((bytes[i] & 240) >> 4);
//                System.out.println("头："+tag);
//                if(tag!=15){
////                    byte tagData1 =(byte) (bytes[i] & 255);
//                    byte tagData1 = (byte)((bytes[i] & 240) >> 4);
//                    System.out.println("不等于十五:"+tagData1);
//                }else {
//                    byte tagData1 = (byte)((bytes[i] & 240) >> 4);
//                    System.out.println("等于十五:"+tagData1);
//                }
//            }
//
////            System.out.println("结果："+tagData);
//
//
//        }else {
//            byte b=((byte)(12|(a<<4)));//1
//            byte type = (byte) (b & 15);
//            byte tag = (byte)((b & 240) >> 4);
//
//        }





    }
}
