package com.ruoyi.im.entity;

import java.io.Serializable;


/**
 * key 类
 * 2021-10-15
 * *严格
 *
 * by GuangHeLiZi
 * 更多请鉴
 */
public class Ecdh implements Serializable {

    private String share_key;
    private String public_key;
    private String tgt_key;
    private String session_key;//tlv03 05
    private String token_key;//tlv01 0E

    public String getShare_key() {
        return share_key;
    }

    public void setShare_key(String share_key) {
        this.share_key = share_key;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getTgt_key() {
        return tgt_key;
    }

    public void setTgt_key(String tgt_key) {
        this.tgt_key = tgt_key;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getToken_key() {
        return token_key;
    }

    public void setToken_key(String token_key) {
        this.token_key = token_key;
    }


}
