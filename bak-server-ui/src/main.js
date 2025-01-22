import Vue from 'vue'
import VueRouter from 'vue-router'
import App from './App.vue'
import ElementUI from 'element-ui'
import "element-ui/lib/theme-chalk/index.css";
import FastCrudUI from 'fast-crud-ui'
import "fast-crud-ui/lib/style.css"
import http from './http'

Vue.config.productionTip = false
Vue.prototype.$http = http
Vue.use(VueRouter)
const routes = [
    {
        path: '/',
        name: 'Home',
        redirect: '/task-config'
    },
    {
        path: '/task-config',
        name: 'TaskConfig',
        component: () => import('./pages/TaskConfig.vue')
    }, {
        path: '/data-category',
        name: 'DataCategory',
        component: () => import('./pages/DataCategory.vue')
    }, {
        path: '/opr-log',
        name: 'OprLog',
        component: () => import('./pages/OprLog.vue')
    }
]
const router = new VueRouter({
    routes: routes
})

Vue.use(ElementUI)
Vue.use(FastCrudUI, {
    $http: http
})

new Vue({
    router,
    render: h => h(App),
}).$mount('#app')
