#!/bin/bash

export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# mysql归档脚本
# 支持连接单个指定数据库表将指定内容(按策略)的数据截取到指定文件中(sql格式), 截取后删除库表里的这些数据
# 支持传入截取策略: d: 删除指定时间(STRATEGY_VALUE)之前的数据; r: 按照TIME_FIELD倒序后，保留最新的指定行数(STRATEGY_VALUE)数据, 即删除指定行数之后的数据。
# 脚本若执行成功, 最后将输出备份文件的路径

# 检查参数数量
if [ "$#" -ne 11 ]; then
    echo "Usage: $0 <mysql_ip> <mysql_port> <mysql_user> <mysql_password> <database_name> <table_name> <time_field_name> <strategy> <strategy_value> <where_condition> <output_file>"
    exit 1
fi

# 归档脚本不支持自定义where条件, 针对传入的归档策略选择按时间排序剪切或按保留最新指定行数进行剪切, 将命中的数据dump到指定文件后，从库里删除这些数据。
MYSQL_IP=$1  # mysql地址
MYSQL_PORT=$2  # mysql端口
MYSQL_USERNAME=$3  # 用户名
MYSQL_PASSWORD=$4  # 密码
DB_NAME=$5         # 数据库名
TABLE_NAME=$6      # 表名
TIME_FIELD=$7      # 比较的时间字段名——排序
STRATEGY=$8        # 归档策略(数据备份筛选和删除的策略)。d: 删除指定时间(STRATEGY_VALUE)之前的数据; r: 按照TIME_FIELD倒序后，保留最新的指定行数(STRATEGY_VALUE)数据, 即删除指定行数之后的数据。
STRATEGY_VALUE=$9  # 策略对应的参数值
WHERE_CONDITION=${10} # where条件
OUTPUT_FILE=${11}     # 输出文件名, 包含路径

# 如果WHERE_CONDITION为空，则默认赋值"1=1", 避免语法错误
if [ -z "$WHERE_CONDITION" ]; then
  WHERE_CONDITION="1=1"
fi
echo $WHERE_CONDITION
if [ $STRATEGY == "d" ]; then
  mysqldump -h "${MYSQL_IP}" -P ${MYSQL_PORT} -u "${MYSQL_USERNAME}" -p"${MYSQL_PASSWORD}" --single-transaction --quick --no-create-info "$DB_NAME" "$TABLE_NAME" --where="$TIME_FIELD <= '$STRATEGY_VALUE' AND $WHERE_CONDITION" --default-character-set=utf8 > "$OUTPUT_FILE"
elif [ $STRATEGY == "r" ]; then
  mysqldump -h "${MYSQL_IP}" -P ${MYSQL_PORT} -u "${MYSQL_USERNAME}" -p"${MYSQL_PASSWORD}" --single-transaction --quick --no-create-info "$DB_NAME" "$TABLE_NAME" --where="$TIME_FIELD <= (SELECT $TIME_FIELD FROM $TABLE_NAME WHERE $WHERE_CONDITION ORDER BY $TIME_FIELD DESC LIMIT $STRATEGY_VALUE, 1) AND $WHERE_CONDITION" --default-character-set=utf8 > "$OUTPUT_FILE"
else
  echo "策略参数(${STRATEGY})错误，请检查!"
  echo
  exit 1
fi

if [ $? -eq 0 ]; then
    if [ $STRATEGY == "d" ]; then
      DELETE_SQL="DELETE FROM $TABLE_NAME WHERE $TIME_FIELD <= '$STRATEGY_VALUE' AND $WHERE_CONDITION;"
    elif [ $STRATEGY == "r" ]; then
      DELETE_SQL="DELETE t FROM $TABLE_NAME t JOIN (SELECT $TIME_FIELD FROM $TABLE_NAME WHERE $WHERE_CONDITION ORDER BY $TIME_FIELD DESC LIMIT $STRATEGY_VALUE, 1) AS tmp ON t.$TIME_FIELD <= tmp.$TIME_FIELD WHERE $WHERE_CONDITION;"
    else
      echo "策略参数(${STRATEGY})错误，请检查!"
      echo
      exit 1
    fi
    echo
    echo "删除此次针对 $TABLE_NAME 表中已归档的数据, 删除语句: ${DELETE_SQL}"
    echo
    mysql -h "${MYSQL_IP}" -P ${MYSQL_PORT} -u "${MYSQL_USERNAME}" -p"${MYSQL_PASSWORD}" -D "$DB_NAME" -e "$DELETE_SQL"
else
    echo "归档 $TABLE_NAME 时出错，未执行删除操作。" >&2
    exit 1
fi

echo "$OUTPUT_FILE"