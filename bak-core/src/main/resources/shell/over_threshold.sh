#!/bin/bash

export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# 注意: 为安全，只支持调用此脚本时预先设置环境变量MYSQL_PWD，不接受传入

# 检查参数数量
if [ "$#" -ne 9 ]; then
    echo "Usage: $0 <mysql_ip> <mysql_port> <mysql_user> <database_name> <table_name> <time_field_name> <strategy> <strategy_value> <where_condition>"
    exit 1
fi

# 阈值检测脚本
MYSQL_IP=$1  # mysql地址
MYSQL_PORT=$2  # mysql端口
MYSQL_USERNAME=$3  # 用户名
DB_NAME=$4         # 数据库名
TABLE_NAME=$5      # 表名
TIME_FIELD=$6      # 比较的时间字段名
STRATEGY=$7        # 策略: r-条数比较; d-时间比较
STRATEGY_VALUE="$8"  # 策略对应的参数值
WHERE_CONDITION="$9"

# 如果WHERE_CONDITION为空，则默认赋值"1=1", 避免语法错误
if [ -z "$WHERE_CONDITION" ]; then
  WHERE_CONDITION="1=1"
fi
# 查询语句
if [ $STRATEGY == "d" ]; then
  QUERY="SELECT COUNT(1) > 0 FROM \`$TABLE_NAME\` WHERE \`$TIME_FIELD\` <= '$STRATEGY_VALUE' AND $WHERE_CONDITION;"
elif [ $STRATEGY == "r" ]; then
  QUERY="SELECT COUNT(1) > $STRATEGY_VALUE FROM $TABLE_NAME WHERE $WHERE_CONDITION;"
else
  exit 1
fi

RESULT=$(mysql -h "$MYSQL_IP" -P $MYSQL_PORT -u "$MYSQL_USERNAME" -D "$DB_NAME" -se "$QUERY")
# 判断查询结果并返回布尔值
if [ "$RESULT" -eq 1 ]; then
    echo "true"
else
    echo "false"
fi
