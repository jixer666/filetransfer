package com.abc.filetransfer.mapper;

import com.abc.filetransfer.entity.DownloadLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DownloadLogMapper extends BaseMapper<DownloadLog> {
    /**
     * 根据文件ID查找下载记录
     * @param fileId 文件ID
     * @return 下载记录列表
     */
    @Select("SELECT * FROM download_logs WHERE file_id = #{fileId}")
    List<DownloadLog> findByFileId(@Param("fileId") Integer fileId);

    /**
     * 根据用户ID查找下载记录
     * @param userId 用户ID
     * @return 下载记录列表
     */
    @Select("SELECT * FROM download_logs WHERE user_id = #{userId}")
    List<DownloadLog> findByUserId(@Param("userId") Integer userId);
}