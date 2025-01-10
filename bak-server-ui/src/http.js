import axios from "axios";

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
        return response.data;
    },
    (error) => {
        return Promise.reject(error)
    }
)

export default http;