const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    outputDir: '../bak-server/src/main/resources/static',
    devServer: {
        port: 8087,
        proxy: {
            '/api': {
                target: 'http://localhost:8088',
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            }
        }
    }
})
