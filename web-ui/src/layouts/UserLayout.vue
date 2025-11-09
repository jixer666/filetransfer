<template>
  <div class="user-layout">
    <!-- 头部导航 -->
    <el-header class="header">
      <div class="logo">
        <h2>文件流转系统</h2>
      </div>
      <div class="user-info">
        <el-dropdown @command="handleUserCommand">
          <span class="el-dropdown-link">
            <span class="username">{{ currentUser.username }}</span>
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    
    <!-- 主体内容 -->
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
          router>
          <el-menu-item index="/user/upload">
            <i class="el-icon-upload"></i>
            <span slot="title">文件上传</span>
          </el-menu-item>
          <el-menu-item index="/user/history">
            <i class="el-icon-document"></i>
            <span slot="title">历史记录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 内容区域 -->
      <el-main class="content">
        <router-view @file-uploaded="refreshHistory"></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'UserLayout',
  data() {
    return {
      currentUser: {}
    }
  },
  computed: {
    activeMenu() {
      const { meta, path } = this.$route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    }
  },
  mounted() {
    this.getCurrentUser()
  },
  methods: {
    handleMenuSelect(key) {
      // 路由跳转由el-menu的router属性处理
    },
    handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.logout()
      }
    },
    getCurrentUser() {
      request.get('/api/users/current')
        .then(response => {
          if (response.data.success) {
            this.currentUser = response.data.user
          } else {
            this.$message.error('获取用户信息失败')
            // 清除token
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            this.$router.push('/login')
          }
        })
        .catch(error => {
          console.error(error)
          // 清除token
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          this.$router.push('/login')
        })
    },
    logout() {
      request.post('/api/users/logout')
        .then(response => {
          if (response.data.success) {
            this.$message.success('登出成功')
          } else {
            this.$message.error('登出失败')
          }
          // 清除token
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          this.$router.push('/login')
        })
        .catch(error => {
          console.error(error)
          // 即使请求失败，也清除本地token
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          this.$router.push('/login')
        })
    },
    refreshHistory() {
      // 如果当前在历史记录页面，触发刷新
      if (this.$route.path === '/user/history') {
        this.$refs.historyComponent?.loadHistory()
      }
    }
  }
}
</script>

<style scoped>
.user-layout {
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  height: 60px;
}

.logo h2 {
  color: #409EFF;
  margin: 0;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 10px;
  font-size: 14px;
  line-height: 1;
  color: #606266;
  font-weight: 500;
}

.el-icon-arrow-down {
  font-size: 12px;
  line-height: 1;
}

.main-container {
  height: calc(100vh - 60px);
}

.sidebar {
  background-color: #fff;
  box-shadow: 2px 0 12px 0 rgba(0, 0, 0, 0.1);
}

.sidebar-menu {
  height: 100%;
  border: none;
}

.sidebar-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #ecf5ff;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409EFF;
}

.content {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
}
</style>