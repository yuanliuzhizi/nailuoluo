package com.ruoyi.im.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.im.mapper.QqRegisterMapper;
import com.ruoyi.im.domain.QqRegister;
import com.ruoyi.im.service.IQqRegisterService;
import com.ruoyi.common.core.text.Convert;

/**
 * qq二次登录Service业务层处理
 * 
 * @author Ghlz
 * @date 2023-12-28
 */
@Service
public class QqRegisterServiceImpl implements IQqRegisterService 
{
    @Autowired
    private QqRegisterMapper qqRegisterMapper;

    /**
     * 查询qq二次登录
     * 
     * @param id qq二次登录主键
     * @return qq二次登录
     */
    @Override
    public QqRegister selectQqRegisterById(Long id)
    {
        return qqRegisterMapper.selectQqRegisterById(id);
    }

    @Override
    public QqRegister selectQqRegisterByQq(Long qq) {
        return qqRegisterMapper.selectQqRegisterByQq(qq);
    }

    /**
     * 查询qq二次登录列表
     * 
     * @param qqRegister qq二次登录
     * @return qq二次登录
     */
    @Override
    public List<QqRegister> selectQqRegisterList(QqRegister qqRegister)
    {
        return qqRegisterMapper.selectQqRegisterList(qqRegister);
    }

    /**
     * 新增qq二次登录
     * 
     * @param qqRegister qq二次登录
     * @return 结果
     */
    @Override
    public int insertQqRegister(QqRegister qqRegister)
    {
        return qqRegisterMapper.insertQqRegister(qqRegister);
    }

    /**
     * 修改qq二次登录
     * 
     * @param qqRegister qq二次登录
     * @return 结果
     */
    @Override
    public int updateQqRegister(QqRegister qqRegister)
    {
        return qqRegisterMapper.updateQqRegister(qqRegister);
    }

    /**
     * 批量删除qq二次登录
     * 
     * @param ids 需要删除的qq二次登录主键
     * @return 结果
     */
    @Override
    public int deleteQqRegisterByIds(String ids)
    {
        return qqRegisterMapper.deleteQqRegisterByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除qq二次登录信息
     * 
     * @param id qq二次登录主键
     * @return 结果
     */
    @Override
    public int deleteQqRegisterById(Long id)
    {
        return qqRegisterMapper.deleteQqRegisterById(id);
    }
}
