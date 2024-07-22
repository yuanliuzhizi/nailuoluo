package com.ruoyi.im.controller;

import com.ruoyi.common.core.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * qq关键词回复配置类
 * 
 * @author Ghlz
 * @date 2023-12-28
 */
@Controller
@RequestMapping("/system/conf")
public class QqConfController extends BaseController
{
    private String prefix = "system/conf";


    @GetMapping("/keyword")
    public String keyword()
    {
        return prefix + "/keyword";
    }



}
