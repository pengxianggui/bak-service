import axios from "axios";
import {ElMessage} from "element-plus";

const http = axios.create({
    baseURL: '/api'
});

http.interceptors.request.use(
    (config) => {
        return config;
    },
    (error) => {
        return Promise.reject(error)
    }
)
http.interceptors.response.use(
    (response) => {
        // 如果后端有自定义响应体, 则返回内层的业务数据
        return response.data.data;
    },
    (error) => {
        ElMessage.error('系统错误')
        return Promise.reject(error)
    }
)

export default http;