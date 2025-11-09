<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header" class="clearfix">
        <span>个人中心</span>
      </div>
      
      <el-form ref="profileForm" :model="profileForm" :rules="profileRules" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="用户角色">
          <el-input v-model="profileForm.role" disabled></el-input>
        </el-form-item>
        
        <el-form-item label="本级主要单位" prop="parentUnit">
          <el-input v-model="profileForm.parentUnit"></el-input>
        </el-form-item>
        
        <el-form-item label="下级所属单位" prop="childUnit">
          <el-input v-model="profileForm.childUnit"></el-input>
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="profileForm.realName"></el-input>
        </el-form-item>
        
        <el-form-item label="单位固定电话" prop="telephone">
          <el-input v-model="profileForm.telephone"></el-input>
        </el-form-item>
        
        <el-form-item label="OA办公账号" prop="officeOa">
          <el-input v-model="profileForm.officeOa"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="profileForm.password" type="password" show-password></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="profileForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Profile',
  data() {
    // 确认密码验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value && value !== this.profileForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    
    return {
      profileForm: {
        userId: '',
        username: '',
        role: '',
        parentUnit: '',
        childUnit: '',
        realName: '',
        telephone: '',
        officeOa: '',
        password: '',
        confirmPassword: ''
      },
      profileRules: {
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
        ],
        password: [
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadProfile()
  },
  methods: {
    loadProfile() {
      request.get('/api/users/current')
        .then(response => {
          if (response.data.success) {
            const user = response.data.user
            this.profileForm.userId = user.userId
            this.profileForm.username = user.username
            this.profileForm.role = user.role === 'admin' ? '管理员' : '普通用户'
            this.profileForm.parentUnit = user.parentUnit
            this.profileForm.childUnit = user.childUnit
            this.profileForm.realName = user.realName
            this.profileForm.telephone = user.telephone
            this.profileForm.officeOa = user.officeOa
          } else {
            this.$message.error('获取用户信息失败')
            this.$router.push('/login')
          }
        })
        .catch(error => {
          console.error(error)
          this.$router.push('/login')
        })
    },
    saveProfile() {
      this.$refs.profileForm.validate((valid) => {
        if (valid) {
          // 构造更新数据对象
          const updateData = {
            userId: this.profileForm.userId,
            parentUnit: this.profileForm.parentUnit,
            childUnit: this.profileForm.childUnit,
            realName: this.profileForm.realName,
            telephone: this.profileForm.telephone,
            officeOa: this.profileForm.officeOa
          }
          
          // 只有当密码不为空时才更新密码
          if (this.profileForm.password) {
            updateData.password = this.profileForm.password
          }
          
          request.put('/api/users/update', updateData)
            .then(response => {
              if (response.data.success) {
                this.$message.success('个人信息保存成功')
              } else {
                this.$message.error(response.data.message || '保存失败')
              }
            })
            .catch(error => {
              console.error(error)
              this.$message.error('保存失败')
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    resetForm() {
      this.$refs.profileForm.resetFields()
      this.loadProfile()
    }
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  padding: 30px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.profile-card {
  width: 600px;
}

.profile-card .el-form {
  margin-top: 20px;
}
</style>