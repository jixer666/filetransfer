<template>
  <div class="login-container">
    <div class="login-form">
      <div class="login-title">
        <h2>文件流转系统</h2>
        <p>安全高效的文件传输平台</p>
      </div>
      
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="form-content">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            prefix-icon="el-icon-user"
            clearable>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="el-icon-lock"
            show-password>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-button" 
            @click="handleLogin"
            :loading="loading">
            登录
          </el-button>
        </el-form-item>
        
        <div class="links-container">
          <div class="register-link">
            <span>还没有账号？</span>
            <el-button type="text" @click="toRegister">立即注册</el-button>
          </div>
          
          <div class="visitor-link">
            <el-button type="text" @click="toVisitorMode">访客模式</el-button>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true
          request.post('/api/users/login', this.loginForm)
            .then(response => {
              this.loading = false
              if (response.data.success) {
                // 登录成功，保存token到localStorage
                if (response.data.token) {
                  localStorage.setItem('token', response.data.token)
                }
                // 保存用户信息到localStorage
                localStorage.setItem('user', JSON.stringify(response.data.user))
                this.$message.success('登录成功')
                // 根据用户角色跳转到不同页面
                if (response.data.user.role === 'admin') {
                  this.$router.push('/admin/user-audit')
                } else {
                  this.$router.push('/user/upload')
                }
              } else {
                // 登录失败
                this.$message.error(response.data.message)
              }
            })
            .catch(error => {
              this.loading = false
              this.$message.error('登录请求失败')
              console.error(error)
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    toRegister() {
      this.$router.push('/register')
    },
    toVisitorMode() {
      this.$router.push('/visitor/download')
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-form {
  width: 400px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
}

.login-title h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.login-title p {
  font-size: 14px;
  color: #909399;
}

.form-content {
  margin-top: 20px;
}

.login-button {
  width: 100%;
  margin-top: 10px;
}

.links-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.register-link {
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
}

.visitor-link {
  text-align: center;
}
</style>