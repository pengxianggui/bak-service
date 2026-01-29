#!/bin/bash
export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# 执行还原操作! 直接将指定的sql文件导入到指定的数据库中！注意：是否幂等，取决于sql文件是否可重复执行。

# 检查参数数量
if [ "$#" -ne 6 ]; then
    echo "Usage: $0 <mysql_ip> <mysql_port> <mysql_username> <mysql_password> <db_name> <input_file>"
    exit 1
fi

MYSQL_IP=$1
MYSQL_PORT=$2
MYSQL_USERNAME=$3
MYSQL_PASSWORD=$4
DB_NAME=$5
INPUT_FILE=$6

export MYSQL_PWD="${MYSQL_PASSWORD}"
mysql -h $MYSQL_IP -P $MYSQL_PORT -u "$MYSQL_USERNAME" "${DB_NAME}" < $INPUT_FILE