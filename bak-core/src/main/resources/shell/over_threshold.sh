#!/bin/bash

export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# 检查参数数量
if [ "$#" -ne 9 ]; then
    echo "Usage: $0 <mysql_ip> <mysql_port> <mysql_user> <mysql_password> <database_name> <table_name> <time_field_name> <strategy> <strategy_value>"
    exit 1
fi

# 阈值检测脚本
MYSQL_IP=$1  # mysql地址
MYSQL_PORT=$2  # mysql端口
MYSQL_USERNAME=$3  # 用户名
MYSQL_PASSWORD=$4  # 密码
DB_NAME=$5         # 数据库名
TABLE_NAME=$6      # 表名
TIME_FIELD=$7      # 比较的时间字段名
STRATEGY=$8        # 策略: r-条数比较; d-时间比较
STRATEGY_VALUE=$9  # 策略对应的参数值

export MYSQL_PWD="${MYSQL_PASSWORD}"

# 查询语句
if [ $STRATEGY == "d" ]; then
  QUERY="SELECT COUNT(1) > 0 FROM \`$TABLE_NAME\` where \`$TIME_FIELD\` <= '$STRATEGY_VALUE';"
elif [ $STRATEGY == "r" ]; then
  QUERY="SELECT COUNT(1) > $STRATEGY_VALUE FROM $TABLE_NAME;"
else
  exit 1
fi

RESULT=$(mysql -h "$MYSQL_IP" -u "$MYSQL_USERNAME" -p"$MYSQL_PASSWORD" -D "$DB_NAME" -se "$QUERY")
# 判断查询结果并返回布尔值
if [ "$RESULT" -eq 1 ]; then
    echo "true"
else
    echo "false"
fi
