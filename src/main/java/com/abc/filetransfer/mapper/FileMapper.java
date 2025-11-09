package com.abc.filetransfer.mapper;

import com.abc.filetransfer.entity.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    /**
     * 根据提取码查找文件
     * @param extractCode 提取码
     * @return 文件对象
     */
    @Select("SELECT * FROM files WHERE extract_code = #{extractCode}")
    File findByExtractCode(@Param("extractCode") String extractCode);

    /**
     * 根据用户ID查找文件
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("SELECT * FROM files WHERE user_id = #{userId}")
    List<File> findByUserId(@Param("userId") Integer userId);

    /**
     * 根据状态查找文件
     * @param status 状态
     * @return 文件列表
     */
    @Select("SELECT * FROM files WHERE status = #{status}")
    List<File> findByStatus(@Param("status") String status);

    /**
     * 增加文件下载次数
     * @param fileId 文件ID
     */
    @Update("UPDATE files SET download_count = download_count + 1 WHERE file_id = #{fileId}")
    void incrementDownloadCount(@Param("fileId") Integer fileId);
}