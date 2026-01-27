import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import App from './App.vue'
import "element-plus/theme-chalk/index.css"
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import {createRouter, createWebHashHistory} from 'vue-router'
import FastCrudUI from 'fast-crud-ui3'
import "fast-crud-ui3/lib/style.css"
import http from './http'

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
const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
})

const app = createApp(App)
app.use(ElementPlus, {
    locale: zhCn
})
app.use(FastCrudUI, {
    $http: http,
    $router: router
}).use(router)
app.mount('#app')
