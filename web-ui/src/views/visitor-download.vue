<template>
  <div class="visitor-container">
    <div class="header">
      <div class="logo">
        <h2>文件流转系统</h2>
      </div>
      <div class="nav">
        <el-button type="text" @click="toLogin">登录</el-button>
        <el-button type="text" @click="toRegister">注册</el-button>
      </div>
    </div>
    
    <div class="download-section">
      <div class="download-box">
        <h3>访客下载</h3>
        <p>请输入提取码下载文件</p>
        
        <el-form ref="downloadForm" :model="downloadForm" :rules="downloadRules" class="download-form">
          <el-form-item prop="extractCode">
            <el-input 
              v-model="downloadForm.extractCode" 
              placeholder="请输入5位提取码" 
              prefix-icon="el-icon-key"
              maxlength="5">
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              class="download-button" 
              @click="handleDownload"
              :loading="loading">
              下载文件
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'VisitorDownload',
  data() {
    return {
      downloadForm: {
        extractCode: ''
      },
      downloadRules: {
        extractCode: [
          { required: true, message: '请输入提取码', trigger: 'blur' },
          { min: 5, max: 5, message: '提取码长度为5位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleDownload() {
      this.$refs.downloadForm.validate((valid) => {
        if (valid) {
          this.loading = true
          // 使用GET请求方式下载文件
          const downloadUrl = `http://localhost:8080/api/files/download/visitor?extractCode=${this.downloadForm.extractCode}`
          window.open(downloadUrl, '_blank')
          this.loading = false
          this.$message.success('正在下载文件...')
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    toLogin() {
      this.$router.push('/login')
    },
    toRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.visitor-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 50px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.logo h2 {
  color: #17B3A3;
  margin: 0;
}

.nav .el-button {
  margin-left: 20px;
  font-size: 16px;
}

.download-section {
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 80px);
}

.download-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}

.download-box h3 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.download-box p {
  font-size: 14px;
  color: #909399;
  margin-bottom: 30px;
}

.download-form {
  margin-top: 20px;
}

.download-button {
  width: 100%;
  margin-top: 10px;
}
</style>