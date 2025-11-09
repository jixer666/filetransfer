<template>
  <div class="register-container">
    <div class="register-form">
      <div class="register-title">
        <h2>用户注册</h2>
        <p>请填写以下信息完成注册</p>
      </div>
      
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-width="120px" class="form-content">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="本级主要单位" prop="parentUnit">
          <el-input v-model="registerForm.parentUnit" placeholder="请输入本级主要单位"></el-input>
        </el-form-item>
        
        <el-form-item label="下级所属单位" prop="childUnit">
          <el-input v-model="registerForm.childUnit" placeholder="请输入下级所属单位"></el-input>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="固定电话" prop="telephone">
          <el-input v-model="registerForm.telephone" placeholder="请输入单位固定电话"></el-input>
        </el-form-item>
        
        <el-form-item label="OA办公账号" prop="officeOa">
          <el-input v-model="registerForm.officeOa" placeholder="请输入OA办公账号"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitRegister" :loading="loading">提交注册申请</el-button>
          <el-button @click="backToLogin">返回登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Register',
  data() {
    // 确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    
    return {
      loading: false,
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        parentUnit: '',
        childUnit: '',
        realName: '',
        telephone: '',
        officeOa: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ],
        parentUnit: [
          { required: true, message: '请输入本级主要单位', trigger: 'blur' }
        ],
        childUnit: [
          { required: true, message: '请输入下级所属单位', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        telephone: [
          { required: true, message: '请输入单位固定电话', trigger: 'blur' }
        ],
        officeOa: [
          { required: true, message: '请输入OA办公账号', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    submitRegister() {
      this.$refs.registerForm.validate((valid) => {
        if (valid) {
          this.loading = true
          // 构造注册数据对象
          const registrationData = {
            username: this.registerForm.username,
            password: this.registerForm.password,
            parentUnit: this.registerForm.parentUnit,
            childUnit: this.registerForm.childUnit,
            realName: this.registerForm.realName,
            telephone: this.registerForm.telephone,
            officeOa: this.registerForm.officeOa
          }
          
          request.post('/api/users/register', registrationData)
            .then(response => {
              this.loading = false
              if (response.data.success) {
                this.$message.success(response.data.message)
                this.$router.push('/login')
              } else {
                this.$message.error(response.data.message)
              }
            })
            .catch(error => {
              this.loading = false
              this.$message.error('注册请求失败')
              console.error(error)
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    backToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.register-form {
  width: 600px;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
}

.register-title h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.register-title p {
  font-size: 14px;
  color: #909399;
}

.form-content {
  margin-top: 20px;
}

.form-content .el-form-item {
  margin-bottom: 20px;
}
</style>