package com.abc.filetransfer.mapper;

import com.abc.filetransfer.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 查找所有普通用户
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE role = 'user'")
    List<User> findNormalUsers();

    /**
     * 查找所有管理员用户
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE role = 'admin'")
    List<User> findAdminUsers();

    /**
     * 根据状态查找用户
     * @param status 状态
     * @return 用户列表
     */
    @Select("SELECT * FROM users WHERE status = #{status}")
    List<User> findByStatus(@Param("status") String status);
}