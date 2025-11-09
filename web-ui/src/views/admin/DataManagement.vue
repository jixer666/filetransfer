<template>
  <div class="data-management-section">
    <h3>数据管理</h3>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="addFile">新增文件</el-button>
        <el-button
          type="danger"
          @click="deleteFiles"
          :disabled="multipleFileSelection.length === 0"
        >
          批量删除
        </el-button>
      </div>
      <div class="toolbar-right"></div>
    </div>
    <el-table
      :data="fileData"
      style="width: 100%"
      border
      @selection-change="handleFileSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
        prop="username"
        label="上传用户"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="originalName"
        label="文件名"
        width="200"
      ></el-table-column>
      <el-table-column
        prop="fileType"
        label="文件类型"
        width="100"
      ></el-table-column>
      <el-table-column prop="fileSize" label="文件大小" width="100">
        <template slot-scope="scope">
          {{ formatFileSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="extractCode"
        label="提取码"
        width="100"
      ></el-table-column>
      <el-table-column
        prop="deleteCode"
        label="删除码"
        width="100"
      ></el-table-column>
      <el-table-column
        prop="uploadTime"
        label="上传时间"
        width="180"
      ></el-table-column>
      <el-table-column
        prop="expireTime"
        label="过期时间"
        width="180"
      ></el-table-column>
      <el-table-column
        prop="downloadCount"
        label="下载次数"
        width="100"
      ></el-table-column>
      <el-table-column prop="status" label="文件状态" width="100">
        <template slot-scope="scope">
          <el-tag
            :type="
              scope.row.status === 'active'
                ? 'success'
                : scope.row.status === 'deleted'
                ? 'danger'
                : 'warning'
            "
          >
            {{ formatFileStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="viewFileDetails(scope.row)"
            >更多</el-button
          >
          <el-button
            size="mini"
            type="danger"
            @click="deleteFile(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 文件详情弹窗 -->
    <el-dialog
      title="文件详情"
      :visible.sync="fileDetailDialogVisible"
      width="700px"
    >
      <div v-if="currentFileDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="上传用户">{{
            currentFileDetail.username
          }}</el-descriptions-item>
          <el-descriptions-item label="文件名">{{
            currentFileDetail.originalName
          }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{
            currentFileDetail.fileType
          }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{
            formatFileSize(currentFileDetail.fileSize)
          }}</el-descriptions-item>
          <el-descriptions-item label="提取码">{{
            currentFileDetail.extractCode
          }}</el-descriptions-item>
          <el-descriptions-item label="删除码">{{
            currentFileDetail.deleteCode
          }}</el-descriptions-item>
          <el-descriptions-item label="上传时间">{{
            currentFileDetail.uploadTime
          }}</el-descriptions-item>
          <el-descriptions-item label="过期时间">{{
            currentFileDetail.expireTime
          }}</el-descriptions-item>
          <el-descriptions-item label="下载次数">{{
            currentFileDetail.downloadCount
          }}</el-descriptions-item>
          <el-descriptions-item label="文件状态">{{
            formatFileStatus(currentFileDetail.status)
          }}</el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px">
          <h4>下载记录</h4>
          <el-table
            :data="downloadRecords"
            style="width: 100%"
            border
            size="small"
          >
            <el-table-column
              prop="username"
              label="下载用户"
              width="120"
            ></el-table-column>
            <el-table-column
              prop="downloadTime"
              label="下载时间"
              width="180"
            ></el-table-column>
            <el-table-column
              prop="ipAddress"
              label="IP地址"
              width="150"
            ></el-table-column>
          </el-table>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="fileDetailDialogVisible = false"
          >关 闭</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "DataManagement",
  data() {
    return {
      multipleFileSelection: [],
      fileDetailDialogVisible: false,
      fileData: [],
      currentFileDetail: null,
      downloadRecords: [],
    };
  },
  mounted() {
    this.loadFileData();
  },
  methods: {
    loadFileData() {
      request
        .get("/api/admin/files")
        .then((response) => {
          if (response.data.success) {
            this.fileData = response.data.data;
            console.log("File data loaded:", this.fileData); // 添加调试信息
          } else {
            this.$message.error("获取文件数据失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$message.error("获取文件数据失败");
        });
    },
    handleFileSelectionChange(val) {
      this.multipleFileSelection = val;
      console.log("Selected files:", val); // 添加调试信息
    },
    viewFileDetails(row) {
      this.currentFileDetail = row;
      // 获取下载记录
      request
        .get(`/api/admin/files/${row.fileId}/downloads`)
        .then((response) => {
          if (response.data.success) {
            this.downloadRecords = response.data.data;
            this.fileDetailDialogVisible = true;
          } else {
            this.$message.error("获取下载记录失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$message.error("获取下载记录失败");
        });
    },
    deleteFile(row) {
      this.$confirm("此操作将永久删除该文件, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          request
            .delete(`/api/admin/files/${row.fileId}`)
            .then((response) => {
              if (response.data.success) {
                this.$message.success("删除成功");
                // 重新加载数据
                this.loadFileData();
              } else {
                this.$message.error(response.data.message || "删除失败");
              }
            })
            .catch((error) => {
              console.error(error);
              this.$message.error("删除失败");
            });
        })
        .catch(() => {
          this.$message.info("已取消删除");
        });
    },
    deleteFiles() {
      this.$confirm(
        `此操作将永久删除选中的 ${this.multipleFileSelection.length} 个文件, 是否继续?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          // 批量删除文件
          const deletePromises = this.multipleFileSelection.map((file) =>
            request.delete(`/api/admin/files/${file.fileId}`)
          );

          Promise.all(deletePromises)
            .then(() => {
              this.$message.success("批量删除成功");
              this.multipleFileSelection = [];
              // 重新加载数据
              this.loadFileData();
            })
            .catch((error) => {
              console.error(error);
              this.$message.error("批量删除失败");
            });
        })
        .catch(() => {
          this.$message.info("已取消删除");
        });
    },
    formatFileStatus(status) {
      switch (status) {
        case "active":
          return "有效";
        case "deleted":
          return "已删除";
        case "expired":
          return "已过期";
        default:
          return status;
      }
    },
    formatFileSize(size) {
      if (size < 1024) {
        return size + " B";
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + " KB";
      } else if (size < 1024 * 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + " MB";
      } else {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + " GB";
      }
    },
    addFile() {
      // TODO: 实现新增文件功能
      this.$message.info("新增文件功能待实现");
    }
  },
};
</script>

<style scoped>
.data-management-section h3 {
  margin-top: 0;
  color: #303133;
  font-weight: 600;
  font-size: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.toolbar-left {
  flex: 1;
}

.toolbar-right {
  flex: 1;
  text-align: right;
}

.data-management-section .el-table {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.data-management-section .el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.data-management-section .el-table .el-table__row:hover {
  background-color: #f0f9ff;
}

.el-dialog {
  border-radius: 8px;
}

.el-dialog__header {
  background-color: #409EFF;
  color: #fff;
  font-weight: 500;
  border-radius: 8px 8px 0 0;
}

.el-dialog__body {
  padding: 20px;
}

.el-descriptions {
  background: #fff;
  border-radius: 4px;
}

.el-descriptions__header {
  margin-bottom: 10px;
}

.el-descriptions__title {
  font-weight: 600;
  color: #303133;
}
</style>