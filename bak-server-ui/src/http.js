import axios from "axios";
import {Message} from 'element-ui'

const http = axios.create({
    baseURL: process.env.VUE_APP_MODE === 'toBackend' ? '' : '/api',
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
        return response.data;
    },
    (error) => {
        Message.error(error.message);
        return Promise.reject(error)
    }
)

export default http;