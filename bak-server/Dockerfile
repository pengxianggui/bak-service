# 当前Dockerfile是bak-server所需的基础镜像，主要是要求系统必须有几个命令。
# 使用 Eclipse Temurin 提供的 Java 8 镜像（基于 Ubuntu，非常稳定）
#FROM openjdk:8-jre-alpine
FROM eclipse-temurin:8-jre

# 安装 mysql-client 和其他必要工具
# 在 Debian 中，default-mysql-client 会安装官方支持的客户端版本
RUN apt-get update && apt-get install -y \
    bash \
    mysql-client \
    coreutils \
    tzdata \
    sed \
    && rm -rf /var/lib/apt/lists/* \

# 在 apt-get install 后面加上 locales
RUN apt-get update && apt-get install -y locales && \
    locale-gen zh_CN.UTF-8

ENV LANG=zh_CN.UTF-8
ENV LC_ALL=zh_CN.UTF-8

# 设置时区为上海
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 验证命令是否存在
RUN mysql --version && mysqldump --version && sed --version