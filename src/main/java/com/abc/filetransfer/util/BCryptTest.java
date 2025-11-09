package com.abc.filetransfer.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("明文密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("密码匹配结果: " + matches);
    }
}