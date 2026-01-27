import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {
    const env = loadEnv(mode, process.cwd(), 'VITE_')
    return {
        server: {
            host: 'localhost',
            port: 5173,
            proxy: {
                '/api': {
                    target: env.VITE_PROXY_URL,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, '')
                }
            }
        },
        plugins: [
            vue(), // vite为vue3而生, 配置此项支持vue2
            vueJsx(), // 支持vue2中使用jsx语法
            Components({
                resolvers: [ElementPlusResolver()], // 使得无需手动引入element-ui组件，自动识别按需引入
            })
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('./src', import.meta.url)), // 配置别名,简化导入时路径书写
                // vue: 'vue/dist/vue.esm.js' // Vue 2.7 的完整 ESM 构建
            }
        },
        build: {
            outDir: mode === 'backend' ? '../bak-server/src/main/resources/static' : 'dist'
        },
        css: {
            preprocessorOptions: {
                scss: {
                    api: "modern-compiler"
                }
            }
        }
    }
})
