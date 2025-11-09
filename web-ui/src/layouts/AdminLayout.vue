<template>
  <div class="admin-layout">
    <!-- 头部导航 -->
    <el-header class="header">
      <div class="logo">
        <h2>文件流转系统 - 管理员面板</h2>
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
          router
        >
          <el-submenu index="user-management">
            <template slot="title">
              <i class="el-icon-user"></i>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/user-audit">新用户审核</el-menu-item>
            <el-menu-item index="/admin/user-list">用户管理</el-menu-item>
          </el-submenu>
          <el-menu-item index="/admin/data-management">
            <i class="el-icon-data-analysis"></i>
            <span slot="title">数据管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区域 -->
      <el-main class="content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "AdminLayout",
  data() {
    return {
      currentUser: {}
    };
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
    this.getCurrentUser();
  },
  methods: {
    handleMenuSelect(key) {
      // 路由跳转由el-menu的router属性处理
    },
    handleUserCommand(command) {
      if (command === "profile") {
        this.$router.push("/profile");
      } else if (command === "logout") {
        this.logout();
      }
    },
    getCurrentUser() {
      request
        .get("/api/users/current")
        .then((response) => {
          if (response.data.success) {
            this.currentUser = response.data.user;
            if (this.currentUser.role !== "admin") {
              this.$message.error("权限不足");
              this.$router.push("/login");
            }
          } else {
            this.$message.error("获取用户信息失败");
            this.$router.push("/login");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$router.push("/login");
        });
    },
    logout() {
      request
        .post("/api/users/logout")
        .then((response) => {
          if (response.data.success) {
            this.$message.success("登出成功");
            this.$router.push("/login");
          } else {
            this.$message.error("登出失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$router.push("/login");
        });
    }
  }
};
</script>

<style scoped>
.admin-layout {
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

.el-avatar {
  vertical-align: middle;
  display: flex;
  align-items: center;
  background-color: #409EFF;
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

.sidebar-menu .el-submenu__title:hover,
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