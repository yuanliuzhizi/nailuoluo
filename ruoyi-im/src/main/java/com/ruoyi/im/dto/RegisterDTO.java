package com.ruoyi.im.dto;

import javax.validation.constraints.NotNull;

public class RegisterDTO {

    @NotNull(message = "id不可空")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
