<template>
  <div class="user-list-section">
    <h3>用户管理</h3>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="addUser">新增用户</el-button>
        <el-button
          type="danger"
          @click="deleteUsers"
          :disabled="multipleSelection.length === 0"
        >
          批量删除
        </el-button>
      </div>
      <div class="toolbar-right"></div>
    </div>
    <el-table
      :data="userData"
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
        prop="username"
        label="用户名"
        width="120"
      ></el-table-column>
      <el-table-column prop="role" label="用户角色" width="100">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.role === 'admin' ? 'danger' : 'primary'"
          >
            {{ scope.row.role === "admin" ? "管理员" : "普通用户" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="parentUnit"
        label="本级主要单位"
        width="150"
      ></el-table-column>
      <el-table-column
        prop="childUnit"
        label="下级所属单位"
        width="150"
      ></el-table-column>
      <el-table-column
        prop="realName"
        label="真实姓名"
        width="100"
      ></el-table-column>
      <el-table-column
        prop="telephone"
        label="固定电话"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="officeOa"
        label="OA办公账号"
        width="120"
      ></el-table-column>
      <el-table-column
        prop="registrationTime"
        label="注册时间"
        width="180"
      ></el-table-column>
      <el-table-column
        prop="uploadCount"
        label="上传次数"
        width="100"
      ></el-table-column>
      <el-table-column
        prop="downloadCount"
        label="下载次数"
        width="100"
      ></el-table-column>
      <el-table-column
        prop="lastLogin"
        label="最后登录时间"
        width="180"
      ></el-table-column>
      <el-table-column prop="status" label="账户状态" width="100">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.status === 'active' ? 'success' : 'danger'"
          >
            {{ scope.row.status === "active" ? "正常" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="warning"
            @click="toggleUserStatus(scope.row)"
          >
            {{ scope.row.status === "active" ? "停用" : "启用" }}
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="deleteUser(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "UserList",
  data() {
    return {
      userData: [],
      multipleSelection: [],
    };
  },
  mounted() {
    this.loadUserData();
  },
  methods: {
    loadUserData() {
      request
        .get("/api/admin/users")
        .then((response) => {
          if (response.data.success) {
            this.userData = response.data.data;
            console.log("User data loaded:", this.userData); // 添加调试信息
          } else {
            this.$message.error("获取用户数据失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$message.error("获取用户数据失败");
        });
    },
    toggleUserStatus(row) {
      const newStatus = row.status === "active" ? "inactive" : "active";
      request
        .post(`/api/admin/users/${row.userId}/status`, null, {
          params: { status: newStatus },
        })
        .then((response) => {
          if (response.data.success) {
            this.$message.success("用户状态更新成功");
            // 更新本地数据
            row.status = newStatus;
          } else {
            this.$message.error(response.data.message || "状态更新失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$message.error("状态更新失败");
        });
    },
    deleteUser(row) {
      this.$confirm("此操作将永久删除该用户, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          request
            .delete(`/api/admin/users/${row.userId}`)
            .then((response) => {
              if (response.data.success) {
                this.$message.success("删除成功");
                // 重新加载数据
                this.loadUserData();
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
    deleteUsers() {
      this.$confirm(
        `此操作将永久删除选中的 ${this.multipleSelection.length} 个用户, 是否继续?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          // 批量删除用户
          const deletePromises = this.multipleSelection.map((user) =>
            request.delete(`/api/admin/users/${user.userId}`)
          );

          Promise.all(deletePromises)
            .then(() => {
              this.$message.success("批量删除成功");
              this.multipleSelection = [];
              // 重新加载数据
              this.loadUserData();
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
    handleSelectionChange(val) {
      this.multipleSelection = val;
      console.log("Selected users:", val); // 添加调试信息
    },
    addUser() {
      // TODO: 实现新增用户功能
      this.$message.info("新增用户功能待实现");
    }
  },
};
</script>

<style scoped>
.user-list-section h3 {
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

.user-list-section .el-table {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-list-section .el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.user-list-section .el-table .el-table__row:hover {
  background-color: #f0f9ff;
}
</style>