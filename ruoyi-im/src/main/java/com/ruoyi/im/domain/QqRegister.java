package com.ruoyi.im.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * qq二次登录对象 qq_register
 * 
 * @author Ghlz
 * @date 2023-12-28
 */
public class QqRegister extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 登录id */
    @Excel(name = "登录id")
    private Long userId;

    /** qq */
    @Excel(name = "qq")
    private Long qq;

    /** tgt_key */
    @Excel(name = "tgt_key")
    private String tgtKey;

    /** tlv_t0065 */
    @Excel(name = "tlv_t0065")
    private String tlvT0065;

    /** tlv_t0065 */
    @Excel(name = "tlv_t0065")
    private String tlvT0018;

    /** tlv_t0019 */
    @Excel(name = "tlv_t0019")
    private String tlvT0019;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setQq(Long qq) 
    {
        this.qq = qq;
    }

    public Long getQq() 
    {
        return qq;
    }
    public void setTgtKey(String tgtKey) 
    {
        this.tgtKey = tgtKey;
    }

    public String getTgtKey() 
    {
        return tgtKey;
    }
    public void setTlvT0065(String tlvT0065) 
    {
        this.tlvT0065 = tlvT0065;
    }

    public String getTlvT0065() 
    {
        return tlvT0065;
    }
    public void setTlvT0018(String tlvT0018) 
    {
        this.tlvT0018 = tlvT0018;
    }

    public String getTlvT0018() 
    {
        return tlvT0018;
    }
    public void setTlvT0019(String tlvT0019) 
    {
        this.tlvT0019 = tlvT0019;
    }

    public String getTlvT0019() 
    {
        return tlvT0019;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("qq", getQq())
            .append("tgtKey", getTgtKey())
            .append("tlvT0065", getTlvT0065())
            .append("tlvT0018", getTlvT0018())
            .append("tlvT0019", getTlvT0019())
            .toString();
    }
}
