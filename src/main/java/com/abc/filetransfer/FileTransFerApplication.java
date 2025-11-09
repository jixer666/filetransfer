package com.abc.filetransfer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.abc.filetransfer.mapper")
@EnableScheduling
public class FileTransFerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileTransFerApplication.class, args);
    }
}