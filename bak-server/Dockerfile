# 使用轻量的 Alpine Java 8 镜像
FROM openjdk:8-jre-alpine

# 安装 mysql-client (包含 mysql, mysqldump)
# tail, sed, tr 在 Alpine 自带的 busybox 中已内置，
# 但为了确保功能完整，我们可以安装 coreutils
RUN apk add --no-cache \
    bash \
    mysql-client \
    coreutils \
    tzdata \
    sed

# 设置时区为上海
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 验证命令是否存在
RUN mysql --version && mysqldump --version && sed --version