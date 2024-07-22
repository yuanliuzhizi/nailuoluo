package com.ruoyi.im.controller;

import java.util.List;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.im.domain.QqRegister;
import com.ruoyi.im.service.IQqRegisterService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * qq二次登录Controller
 * 
 * @author Ghlz
 * @date 2023-12-28
 */
@Controller
@RequestMapping("/system/register")
public class QqRegisterController extends BaseController
{
    private String prefix = "system/register";

    @Autowired
    private IQqRegisterService qqRegisterService;

    @GetMapping()
    public String register()
    {
        return prefix + "/register";
    }

    @GetMapping("/qq")
    public String registerQq()
    {
        return prefix + "/qq";
    }




    /**
     * 查询qq二次登录列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(QqRegister qqRegister)
    {
        SysUser sysUser = ShiroUtils.getSysUser();
        Long id = sysUser.getUserId();
        if (!sysUser.isAdmin()){
            qqRegister.setUserId(id);
        }
        startPage();
        List<QqRegister> list = qqRegisterService.selectQqRegisterList(qqRegister);
        return getDataTable(list);
    }

    /**
     * 导出qq二次登录列表
     */
    @RequiresPermissions("system:register:export")
    @Log(title = "qq二次登录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(QqRegister qqRegister)
    {
        List<QqRegister> list = qqRegisterService.selectQqRegisterList(qqRegister);
        ExcelUtil<QqRegister> util = new ExcelUtil<QqRegister>(QqRegister.class);
        return util.exportExcel(list, "qq二次登录数据");
    }

    /**
     * 新增qq二次登录
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存qq二次登录
     */
    @RequiresPermissions("system:register:add")
    @Log(title = "qq二次登录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(QqRegister qqRegister)
    {
        return toAjax(qqRegisterService.insertQqRegister(qqRegister));
    }

    /**
     * 修改qq二次登录
     */
    @RequiresPermissions("system:register:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        QqRegister qqRegister = qqRegisterService.selectQqRegisterById(id);
        mmap.put("qqRegister", qqRegister);
        return prefix + "/edit";
    }

    /**
     * 修改保存qq二次登录
     */
    @RequiresPermissions("system:register:edit")
    @Log(title = "qq二次登录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(QqRegister qqRegister)
    {
        return toAjax(qqRegisterService.updateQqRegister(qqRegister));
    }

    /**
     * 删除qq二次登录
     */
    @RequiresPermissions("system:register:remove")
    @Log(title = "qq二次登录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(qqRegisterService.deleteQqRegisterByIds(ids));
    }
}
