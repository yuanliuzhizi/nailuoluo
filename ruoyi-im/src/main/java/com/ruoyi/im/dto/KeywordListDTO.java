package com.ruoyi.im.dto;

import com.ruoyi.im.dto.vo.KeywordConfVO;
import com.ruoyi.im.dto.vo.KeywordVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel("设置机器人触发关键词回复配置")
public class KeywordListDTO {

    @Valid
    @ApiModelProperty(value = "触发关键词组")
    @Size(max = 10, message = "关键词数量过多(不可大于10)")
    private List<KeywordConfVO> keywordConfVO;

    public List<KeywordConfVO> getKeywordConfVO() {
        return keywordConfVO;
    }

    public void setKeywordConfVO(List<KeywordConfVO> keywordConfVO) {
        this.keywordConfVO = keywordConfVO;
    }
}
