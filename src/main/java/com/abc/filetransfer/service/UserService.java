package com.abc.filetransfer.service;

import com.abc.filetransfer.entity.User;
import com.abc.filetransfer.entity.RegistrationAudit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，如果登录失败返回null
     */
    User login(String username, String password);

    /**
     * 用户注册申请
     * @param registrationAudit 注册信息
     * @return 是否成功
     */
    boolean register(RegistrationAudit registrationAudit);

    /**
     * 管理员审核用户注册
     * @param registrationId 注册ID
     * @param adminId 管理员ID
     * @param approve 是否同意
     * @return 是否成功
     */
    boolean approveRegistration(Integer registrationId, Integer adminId, boolean approve);

    /**
     * 查找所有待审核的注册申请
     * @return 注册申请列表
     */
    List<RegistrationAudit> findPendingRegistrations();

    /**
     * 查找所有用户
     * @return 用户列表
     */
    List<User> findAllUsers();

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUserInfo(User user);

    /**
     * 根据ID查找用户
     * @param userId 用户ID
     * @return 用户对象
     */
    User findById(Integer userId);

    /**
     * 停用/启用用户
     * @param userId 用户ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateUserStatus(Integer userId, String status);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Integer userId);
}