<template>
  <div class="audit-section">
    <h3>新用户审核</h3>
    <el-table :data="auditData" style="width: 100%" border>
      <el-table-column
        prop="username"
        label="用户名"
        width="120"
      ></el-table-column>
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
        label="申请时间"
        width="180"
      ></el-table-column>
      <el-table-column prop="status" label="审核状态" width="100">
        <template slot-scope="scope">
          <el-tag
            :type="
              scope.row.status === 'pending'
                ? 'warning'
                : scope.row.status === 'approved'
                ? 'success'
                : 'danger'
            "
          >
            {{ formatAuditStatus(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="success"
            @click="approveUser(scope.row, true)"
            :disabled="scope.row.status !== 'pending'"
          >
            同意
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="approveUser(scope.row, false)"
            :disabled="scope.row.status !== 'pending'"
          >
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: "UserAudit",
  data() {
    return {
      auditData: [],
    };
  },
  mounted() {
    this.loadAuditData();
  },
  methods: {
    loadAuditData() {
      request
        .get("/api/admin/registrations/pending")
        .then((response) => {
          if (response.data.success) {
            this.auditData = response.data.data;
          } else {
            this.$message.error("获取审核数据失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$message.error("获取审核数据失败");
        });
    },
    approveUser(row, approve) {
      request
        .post(`/api/admin/registrations/${row.registrationId}/approve`, null, {
          params: { approve: approve },
        })
        .then((response) => {
          if (response.data.success) {
            this.$message.success(response.data.message);
            // 重新加载数据
            this.loadAuditData();
          } else {
            this.$message.error(response.data.message || "操作失败");
          }
        })
        .catch((error) => {
          console.error(error);
          this.$message.error("操作失败");
        });
    },
    formatAuditStatus(status) {
      switch (status) {
        case "pending":
          return "待处理";
        case "approved":
          return "已同意";
        case "rejected":
          return "已拒绝";
        default:
          return status;
      }
    },
  },
};
</script>

<style scoped>
.audit-section h3 {
  margin-top: 0;
  color: #303133;
  font-weight: 600;
  font-size: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.audit-section .el-table {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.audit-section .el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

.audit-section .el-table .el-table__row:hover {
  background-color: #f0f9ff;
}
</style>