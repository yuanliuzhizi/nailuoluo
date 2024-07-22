package com.uohe;

import com.ruoyi.common.constant.UploadInfoConstant;
import com.ruoyi.common.json.JSONObject;
import com.ruoyi.common.utils.RedisUtil;
import com.ruoyi.im.entity.User;
import com.ruoyi.im.tool.JCEUnpack;
import com.ruoyi.im.tool.Jce;
import com.ruoyi.im.utils.AllToByte;
import com.ruoyi.im.utils.ByteToAll;
import com.ruoyi.im.utils.ZLibUtils;
import java.util.ArrayList;
import java.util.Map;

public class Test3 {

    public static void main(String[] args) {
        String data = "10032C3C420E7DEEA056296D71712E494D536572766963652E467269656E644C6973745365727669636553657276616E744F626A661847657454726F6F704170706F696E7452656D61726B5265717D0000770800010607475441524553501D0000670A02696058961213B7FB9A2300000000909A52403900010A022EB676B1160CE58589E6A0B8E7B292E5AD90260BE5AD99E5B09AE9A6993535360BE5AD99E5B09AE9A69935354C56006600760086009600A600BCCCDCED00000608020801101CF60F000B4C5C6C0B8C980CA80C";

        unPack(AllToByte.hexToByte(data));

    }

    public static boolean unPack(byte[] part2Outer) {
        JCEUnpack jceUnpack = new JCEUnpack(ZLibUtils.decompress(part2Outer));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readNumber(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readString(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readString(jceUnpack.bs).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v")));
        jceUnpack.wrap(AllToByte.hexToByte((String) ((Map) jceUnpack.readMapSimpleList(jceUnpack.bs).get("v")).get("remain")));
        jceUnpack.wrap(AllToByte.hexToByte((String) jceUnpack.readSimpleList(jceUnpack.bs).get("v")));
        jceUnpack.wrap(AllToByte.hexToByte(Jce.tagSE(ByteToAll.byteToHxe(jceUnpack.bs.array()))));
        //本人qq
        Map qqMap = jceUnpack.readNumber(jceUnpack.bs);
        jceUnpack.wrap(AllToByte.hexToByte((String) qqMap.get("remain")));
        Map map = jceUnpack.readNumber(jceUnpack.bs);
        jceUnpack.wrap(AllToByte.hexToByte((String) map.get("remain")));
        Map qunLinkMap = jceUnpack.readNumber(jceUnpack.bs);
        jceUnpack.wrap(AllToByte.hexToByte((String) qunLinkMap.get("remain")));
        Map userMap = jceUnpack.readList(jceUnpack.bs);
        return false;
    }



}
