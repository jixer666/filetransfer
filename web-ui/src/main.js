import Vue from 'vue'
import App from './App.vue'
import router from './router'
import "@/assets/css/global.css"
import '@/assets/js/element'
import { Message, MessageBox, Notification } from 'element-ui'


Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  created() {
    Vue.prototype.$bus = this;
    Vue.prototype.$message = Message;
    Vue.prototype.$notify = Notification;
    Vue.prototype.$confirm = MessageBox.confirm;

  },
  router
}).$mount('#app')