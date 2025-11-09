import Vue from 'vue'
import Router from 'vue-router'

// 导入页面组件
const Login = () => import('@/views/login')
const Register = () => import('@/views/register')
const VisitorDownload = () => import('@/views/visitor-download')
const Profile = () => import('@/views/profile')
const UserDashboard = () => import('@/views/user-dashboard')
const AdminDashboard = () => import('@/views/admin-dashboard')

// 导入布局组件
const UserLayout = () => import('@/layouts/UserLayout')
const AdminLayout = () => import('@/layouts/AdminLayout')

// 导入用户面板组件
const Upload = () => import('@/views/user/Upload')
const History = () => import('@/views/user/History')

// 导入管理员面板组件
const UserAudit = () => import('@/views/admin/UserAudit')
const UserList = () => import('@/views/admin/UserList')
const DataManagement = () => import('@/views/admin/DataManagement')

Vue.use(Router)

export const constantRoutes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: Login,
    hidden: true
  },
  {
    path: '/register',
    component: Register,
    hidden: true
  },
  {
    path: '/visitor/download',
    component: VisitorDownload,
    hidden: true
  },
  {
    path: '/user',
    component: UserLayout,
    children: [
      {
        path: 'upload',
        component: Upload
      },
      {
        path: 'history',
        component: History
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: 'user-audit',
        component: UserAudit
      },
      {
        path: 'user-list',
        component: UserList
      },
      {
        path: 'data-management',
        component: DataManagement
      }
    ]
  },
  {
    path: '/profile',
    component: Profile,
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
]

export const dynamicRoutes = [
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router