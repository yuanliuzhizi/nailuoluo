package com.ruoyi.im.service;

import java.util.List;
import com.ruoyi.im.domain.QqRegister;

/**
 * qq二次登录Service接口
 * 
 * @author Ghlz
 * @date 2023-12-28
 */
public interface IQqRegisterService 
{
    /**
     * 查询qq二次登录
     * 
     * @param id qq二次登录主键
     * @return qq二次登录
     */
    public QqRegister selectQqRegisterById(Long id);

    /**
     * 查询qq二次登录
     *
     * @param qq qq二次登录主键
     * @return qq二次登录
     */
    public QqRegister selectQqRegisterByQq(Long qq);

    /**
     * 查询qq二次登录列表
     * 
     * @param qqRegister qq二次登录
     * @return qq二次登录集合
     */
    public List<QqRegister> selectQqRegisterList(QqRegister qqRegister);

    /**
     * 新增qq二次登录
     * 
     * @param qqRegister qq二次登录
     * @return 结果
     */
    public int insertQqRegister(QqRegister qqRegister);

    /**
     * 修改qq二次登录
     * 
     * @param qqRegister qq二次登录
     * @return 结果
     */
    public int updateQqRegister(QqRegister qqRegister);

    /**
     * 批量删除qq二次登录
     * 
     * @param ids 需要删除的qq二次登录主键集合
     * @return 结果
     */
    public int deleteQqRegisterByIds(String ids);

    /**
     * 删除qq二次登录信息
     * 
     * @param id qq二次登录主键
     * @return 结果
     */
    public int deleteQqRegisterById(Long id);
}
