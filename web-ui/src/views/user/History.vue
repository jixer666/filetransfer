<template>
  <div class="history-section">
    <h3>历史记录</h3>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="refreshHistory">刷新</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="danger" @click="deleteSelected" :disabled="multipleSelection.length === 0">
          批量删除
        </el-button>
      </div>
    </div>
    <el-table :data="historyData" style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="originalName" label="文件名" width="200"></el-table-column>
      <el-table-column prop="fileSize" label="文件大小" width="120">
        <template slot-scope="scope">
          {{ formatFileSize(scope.row.fileSize) }}
        </template>
      </el-table-column>
      <el-table-column prop="extractCode" label="提取码" width="100"></el-table-column>
      <el-table-column prop="deleteCode" label="删除码" width="100"></el-table-column>
      <el-table-column prop="uploadTime" label="上传时间" width="180"></el-table-column>
      <el-table-column prop="expireTime" label="过期时间" width="180"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 'active' ? 'success' : scope.row.status === 'deleted' ? 'danger' : 'warning'">
            {{ formatStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="downloadFile(scope.row)">下载</el-button>
          <el-button size="mini" type="danger" @click="deleteFile(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'History',
  data() {
    return {
      historyData: [],
      multipleSelection: []
    }
  },
  mounted() {
    this.loadHistory()
  },
  methods: {
    loadHistory() {
      request.get('/api/files/history')
        .then(response => {
          if (response.data.success) {
            this.historyData = response.data.data
          } else {
            this.$message.error('获取历史记录失败')
          }
        })
        .catch(error => {
          console.error(error)
          this.$message.error('获取历史记录失败')
        })
    },
    formatFileSize(size) {
      if (size < 1024) {
        return size + ' B'
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + ' KB'
      } else if (size < 1024 * 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + ' MB'
      } else {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
      }
    },
    formatStatus(status) {
      switch (status) {
        case 'active':
          return '有效'
        case 'deleted':
          return '已删除'
        case 'expired':
          return '已过期'
        default:
          return status
      }
    },
    downloadFile(row) {
      // 下载文件，需要携带token进行身份验证
      const token = localStorage.getItem('token');
      if (!token) {
        this.$message.error('用户未登录');
        return;
      }
      
      // 创建一个隐藏的iframe来处理文件下载
      const iframe = document.createElement('iframe');
      iframe.style.display = 'none';
      document.body.appendChild(iframe);
      
      // 创建表单并提交
      const form = document.createElement('form');
      form.method = 'GET';
      form.action = `http://localhost:8080/api/files/download`;
      form.target = iframe.name;
      
      // 添加提取码参数
      const extractCodeInput = document.createElement('input');
      extractCodeInput.type = 'hidden';
      extractCodeInput.name = 'extractCode';
      extractCodeInput.value = row.extractCode;
      form.appendChild(extractCodeInput);
      
      // 添加token到请求头
      const headers = new Headers();
      headers.append('Authorization', `Bearer ${token}`);
      
      // 使用fetch API下载文件
      fetch(`${form.action}?extractCode=${row.extractCode}`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.blob();
      })
      .then(blob => {
        // 创建下载链接
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = row.originalName || 'download';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        this.$message.success('文件下载成功');
      })
      .catch(error => {
        console.error('Download error:', error);
        this.$message.error('文件下载失败: ' + error.message);
      })
      .finally(() => {
        document.body.removeChild(iframe);
      });
    },
    deleteFile(row) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.post('/api/files/delete', { deleteCode: row.deleteCode })
          .then(response => {
            if (response.data.success) {
              this.$message.success('删除成功')
              // 重新加载历史记录
              this.loadHistory()
            } else {
              this.$message.error(response.data.message || '删除失败')
            }
          })
          .catch(error => {
            console.error(error)
            this.$message.error('删除失败')
          })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    deleteSelected() {
      this.$confirm(`此操作将永久删除选中的 ${this.multipleSelection.length} 个文件, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 批量删除文件
        const deletePromises = this.multipleSelection.map(file => 
          request.post('/api/files/delete', { deleteCode: file.deleteCode })
        )
        
        Promise.all(deletePromises)
          .then(() => {
            this.$message.success('批量删除成功')
            this.multipleSelection = []
            // 重新加载历史记录
            this.loadHistory()
          })
          .catch(error => {
            console.error(error)
            this.$message.error('批量删除失败')
          })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    refreshHistory() {
      this.loadHistory()
    }
  }
}
</script>

<style scoped>
.history-section h3 {
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

.history-section .el-table {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.history-section .el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.history-section .el-table .el-table__row:hover {
  background-color: #f0f9ff;
}
</style>