#!/bin/bash

export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# mysql归档脚本
# 支持连接单个指定数据库表将指定内容(按策略)的数据截取到指定文件中(sql格式), 截取后删除库表里的这些数据
# 支持传入截取策略: d: 删除指定时间(STRATEGY_VALUE)之前的数据; r: 按照TIME_FIELD倒序后，保留最新的指定行数(STRATEGY_VALUE)数据, 即删除指定行数之后的数据。
# 脚本若执行成功, 最后将输出备份文件的路径
# 注意: 为安全，只支持调用此脚本时预先设置环境变量MYSQL_PWD，不接受传入

# 检查参数数量
if [ "$#" -ne 10 ]; then
    echo "Usage: $0 <mysql_ip> <mysql_port> <mysql_user> <database_name> <table_name> <time_field_name> <strategy> <strategy_value> <where_condition> <output_file>"
    exit 1
fi

# 归档脚本不支持自定义where条件, 针对传入的归档策略选择按时间排序剪切或按保留最新指定行数进行剪切, 将命中的数据dump到指定文件后，从库里删除这些数据。
MYSQL_IP=$1  # mysql地址
MYSQL_PORT=$2  # mysql端口
MYSQL_USERNAME=$3  # 用户名
DB_NAME=$4         # 数据库名
TABLE_NAME=$5      # 表名
TIME_FIELD=$6      # 比较的时间字段名——排序
STRATEGY=$7        # 归档策略(数据备份筛选和删除的策略)。d: 删除指定时间(STRATEGY_VALUE)之前的数据; r: 按照TIME_FIELD倒序后，保留最新的指定行数(STRATEGY_VALUE)数据, 即删除指定行数之后的数据。
STRATEGY_VALUE="$8"  # 策略对应的参数值
WHERE_CONDITION="$9" # where条件
OUTPUT_FILE="${10}"     # 输出文件名, 包含路径

echo "------脚本入参--------"
echo "p1: $MYSQL_IP"
echo "p2: $MYSQL_PORT"
echo "p3: $MYSQL_USERNAME"
echo "p4: $DB_NAME"
echo "p5: $TABLE_NAME"
echo "p6: $TIME_FIELD"
echo "p7: $STRATEGY"
echo "p8: $STRATEGY_VALUE"
echo "p9: $WHERE_CONDITION"
echo "p10: $OUTPUT_FILE"
echo "------end-----------"


# 如果WHERE_CONDITION为空，则默认赋值"1=1", 避免语法错误
if [ -z "$WHERE_CONDITION" ]; then
  WHERE_CONDITION="1=1"
fi
echo "开始执行mysqldump导出数据"
if [ $STRATEGY == "d" ]; then
  FINAL_WHERE="$TIME_FIELD <= '$STRATEGY_VALUE' AND $WHERE_CONDITION"
elif [ $STRATEGY == "r" ]; then
  FINAL_WHERE=${WHERE_CONDITION}
  FINAL_WHERE="$TIME_FIELD <= (SELECT $TIME_FIELD FROM $TABLE_NAME WHERE $WHERE_CONDITION ORDER BY $TIME_FIELD DESC LIMIT $STRATEGY_VALUE, 1) AND $WHERE_CONDITION"
else
  echo "策略参数(${STRATEGY})错误，请检查!"
  exit 1
fi

echo "where条件: ${FINAL_WHERE}"
mysqldump -h "${MYSQL_IP}" -P ${MYSQL_PORT} -u "${MYSQL_USERNAME}" \
  --single-transaction --quick --no-create-info \
  "$DB_NAME" "$TABLE_NAME" \
  --where="$FINAL_WHERE" \
  --default-character-set=utf8 > "$OUTPUT_FILE"

if [ $? -eq 0 ]; then
    echo "导出成功!"
    if [ $STRATEGY == "d" ]; then
      DELETE_SQL="DELETE FROM $TABLE_NAME WHERE ${FINAL_WHERE};"
    elif [ $STRATEGY == "r" ]; then
      # 这里不直接拼接FINAL_WHERE是因为: mysql不允许在update/delete语句中直接引用目标表，这里通过JOIN绕下弯
      DELETE_SQL="DELETE t FROM $TABLE_NAME t JOIN (SELECT $TIME_FIELD FROM $TABLE_NAME WHERE $WHERE_CONDITION ORDER BY $TIME_FIELD DESC LIMIT $STRATEGY_VALUE, 1) AS tmp ON t.$TIME_FIELD <= tmp.$TIME_FIELD WHERE $WHERE_CONDITION;"
    else
      echo "策略参数(${STRATEGY})错误，请检查!"
      exit 1
    fi
    echo "开始删除此次针对 $TABLE_NAME 表中已归档的数据, 删除语句: ${DELETE_SQL}"
    mysql -h "${MYSQL_IP}" -P ${MYSQL_PORT} -u "${MYSQL_USERNAME}" -D "$DB_NAME" -e "$DELETE_SQL"
else
    echo "归档 $TABLE_NAME 时出错，未执行删除操作。" >&2
    exit 1
fi

echo "$OUTPUT_FILE"