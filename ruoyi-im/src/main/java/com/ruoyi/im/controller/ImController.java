package com.ruoyi.im.controller;

import com.ruoyi.common.core.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * IMController
 * 
 * @author Guanghelizi
 * @date 2023-06-02
 */
@Controller
@RequestMapping("/im")
public class ImController extends BaseController
{
    private String prefix = "im";

    @GetMapping("/index")
    public String index()
    {
        return prefix + "/index";
    }
    @GetMapping("/test")
    public String test()
    {
        return prefix + "/test";
    }




}
