package com.abc.filetransfer.mapper;

import com.abc.filetransfer.entity.RegistrationAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RegistrationAuditMapper extends BaseMapper<RegistrationAudit> {
    /**
     * 根据状态查找注册审核记录
     * @param status 状态
     * @return 注册审核记录列表
     */
    @Select("SELECT * FROM registration_audit WHERE status = #{status} ORDER BY registration_time DESC")
    List<RegistrationAudit> findByStatus(@Param("status") String status);

    /**
     * 查找所有待处理的注册审核记录
     * @return 注册审核记录列表
     */
    @Select("SELECT * FROM registration_audit WHERE status = 'pending' ORDER BY registration_time DESC")
    List<RegistrationAudit> findPendingRegistrations();
}