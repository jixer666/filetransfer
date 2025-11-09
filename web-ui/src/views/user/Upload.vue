<template>
  <div class="upload-section">
    <h3>文件上传</h3>
    <div class="upload-area">
      <el-upload
        class="upload-demo"
        drag
        action="http://localhost:8080/api/files/upload"
        :on-progress="handleProgress"
        :on-success="handleSuccess"
        :on-error="handleError"
        :before-upload="beforeUpload"
        :with-credentials="false"
        :headers="uploadHeaders"
        multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          只能上传单个文件或单个压缩包，且大小不超过2GB
        </div>
      </el-upload>
      
      <!-- 上传进度显示 -->
      <div v-if="showProgress" class="progress-section">
        <el-progress :percentage="uploadPercentage" :stroke-width="15"></el-progress>
        <p>文件上传中...</p>
      </div>
    </div>
    
    <!-- 提取码显示 -->
    <div v-if="showExtractCode" class="extract-code-section">
      <el-card class="code-card">
        <div slot="header" class="clearfix">
          <span>文件上传成功</span>
        </div>
        <div class="code-content">
          <p>提取码：<strong>{{ extractCode }}</strong></p>
          <p>删除码：<strong>{{ deleteCode }}</strong></p>
          <p>文件将在48小时后自动删除</p>
          <el-button type="primary" @click="copyCode">复制提取码</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Upload',
  data() {
    return {
      uploadPercentage: 0,
      showProgress: false,
      showExtractCode: false,
      extractCode: '',
      deleteCode: '',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    }
  },
  methods: {
    beforeUpload(file) {
      const isLt2G = file.size / 1024 / 1024 / 1024 < 2
      if (!isLt2G) {
        this.$message.error('上传文件大小不能超过 2GB!')
      }
      return isLt2G
    },
    handleProgress(event, file, fileList) {
      this.showProgress = true
      this.uploadPercentage = Math.round(event.percent)
    },
    handleSuccess(response, file, fileList) {
      this.showProgress = false
      if (response.success) {
        this.showExtractCode = true
        this.extractCode = response.data.extractCode
        this.deleteCode = response.data.deleteCode
        this.$message.success('文件上传成功')
        // 触发事件通知父组件刷新历史记录
        this.$emit('file-uploaded')
      } else {
        this.$message.error(response.message || '文件上传失败')
      }
    },
    handleError(error, file, fileList) {
      this.showProgress = false
      this.$message.error('文件上传失败')
      console.error(error)
    },
    copyCode() {
      const text = this.extractCode
      const textarea = document.createElement('textarea')
      textarea.value = text
      document.body.appendChild(textarea)
      textarea.select()
      document.execCommand('copy')
      document.body.removeChild(textarea)
      this.$message.success('提取码已复制到剪贴板')
    }
  }
}
</script>

<style scoped>
.upload-section h3 {
  margin-top: 0;
  color: #303133;
  font-weight: 600;
  font-size: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.upload-area {
  background: #fff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}

.upload-area:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.el-upload-dragger {
  border: 2px dashed #409EFF;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-upload-dragger:hover {
  border-color: #409EFF;
  background-color: #f0f9ff;
}

.progress-section {
  margin-top: 30px;
  text-align: center;
}

.extract-code-section {
  margin-top: 30px;
}

.code-card {
  max-width: 500px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.code-card .el-card__header {
  background-color: #409EFF;
  color: #fff;
  font-weight: 500;
  border-radius: 8px 8px 0 0;
}

.code-content p {
  margin: 15px 0;
  font-size: 16px;
}

.code-content strong {
  font-size: 20px;
  color: #409EFF;
  font-weight: 600;
}
</style>