package com.abc.filetransfer.service.impl;

import com.abc.filetransfer.entity.User;
import com.abc.filetransfer.entity.RegistrationAudit;
import com.abc.filetransfer.mapper.UserMapper;
import com.abc.filetransfer.mapper.RegistrationAuditMapper;
import com.abc.filetransfer.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RegistrationAuditMapper registrationAuditMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 更新最后登录时间
            user.setLastLogin(LocalDateTime.now());
            userMapper.updateById(user);
            return user;
        }
        return null;
    }

    @Override
    @Transactional
    public boolean register(RegistrationAudit registrationAudit) {
        registrationAudit.setRegistrationTime(LocalDateTime.now());
        registrationAudit.setStatus("pending");
        // 对密码进行加密
        registrationAudit.setPassword(passwordEncoder.encode(registrationAudit.getPassword()));
        return registrationAuditMapper.insert(registrationAudit) > 0;
    }

    @Override
    @Transactional
    public boolean approveRegistration(Integer registrationId, Integer adminId, boolean approve) {
        RegistrationAudit registration = registrationAuditMapper.selectById(registrationId);
        if (registration == null) {
            return false;
        }

        if (approve) {
            // 创建新用户
            User user = new User();
            user.setUsername(registration.getUsername());
            user.setPassword(passwordEncoder.encode(registration.getPassword()));
            user.setParentUnit(registration.getParentUnit());
            user.setChildUnit(registration.getChildUnit());
            user.setRealName(registration.getRealName());
            user.setTelephone(registration.getTelephone());
            user.setOfficeOa(registration.getOfficeOa());
            user.setRegistrationTime(LocalDateTime.now());
            user.setRole("user"); // 默认为普通用户
            user.setStatus("active"); // 默认激活状态
            user.setApprovedBy(adminId);
            user.setApprovedTime(LocalDateTime.now());
            user.setUploadCount(0);
            user.setDownloadCount(0);

            // 保存用户
            userMapper.insert(user);

            // 更新注册审核状态
            registration.setStatus("approved");
            registrationAuditMapper.updateById(registration);
        } else {
            // 拒绝注册申请
            registration.setStatus("rejected");
            registrationAuditMapper.updateById(registration);
        }

        return true;
    }

    @Override
    public List<RegistrationAudit> findPendingRegistrations() {
        return registrationAuditMapper.findPendingRegistrations();
    }

    @Override
    public List<User> findAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public boolean updateUserInfo(User user) {
        // 如果密码不为空，则进行加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.updateById(user) > 0;
    }

    @Override
    public User findById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean updateUserStatus(Integer userId, String status) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setStatus(status);
            return userMapper.updateById(user) > 0;
        }
        return false;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        return userMapper.deleteById(userId) > 0;
    }
}