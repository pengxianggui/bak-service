#!/bin/bash

export LANG=zh_CN.UTF-8
export LC_ALL=zh_CN.UTF-8

# mysql备份脚本，支持连接单个指定数据库表、支持配置where条件，支持指定导出文件的输出路径，包含后缀名(仅支持的后缀名为: sql、txt、csv)
# 脚本若执行成功, 最后将输出备份文件的路径
# 注意: 为安全，只支持调用此脚本时预先设置环境变量MYSQL_PWD，不接受传入

# 检查参数数量
if [ "$#" -ne 7 ]; then
    echo "Usage: $0 <mysql_ip> <mysql_port> <mysql_username> <db_name> <table_name> <where_condition> <output_file>"
    exit 1
fi

MYSQL_IP=$1
MYSQL_PORT=$2
MYSQL_USERNAME=$3
DB_NAME=$4
TABLE_NAME=$5
WHERE_CONDITION="$6"
OUTPUT_FILE="$7"
DELIMITER=',' # csv、txt时列的分割符


echo "------脚本入参--------"
echo "p1: $MYSQL_IP"
echo "p2: $MYSQL_PORT"
echo "p3: $MYSQL_USERNAME"
echo "p4: $DB_NAME"
echo "p5: $TABLE_NAME"
echo "p6: $WHERE_CONDITION"
echo "p7: $OUTPUT_FILE"
echo "------end-----------"

# 获取文件扩展名
FILE_EXTENSION="${OUTPUT_FILE##*.}"
# 如果文件后缀不是sql、txt、csv，则直接退出
if [[ ! "$FILE_EXTENSION" =~ ^(sql|txt|csv|xlsx)$ ]]; then
  echo "Invalid file extension: $FILE_EXTENSION, only support: sql,txt,csv"
  exit 1
fi

# 如果WHERE_CONDITION为空，则默认赋值"1=1", 避免语法错误
if [ -z "$WHERE_CONDITION" ]; then
  WHERE_CONDITION="1=1"
fi

# 如果文件后缀是sql, 则使用mysqldump
if [ "$FILE_EXTENSION" = "sql" ]; then
  # 将delete语句写入开头, 以便还原
  echo "DELETE FROM \`${DB_NAME}\`.\`$TABLE_NAME\` WHERE $WHERE_CONDITION;" >> $OUTPUT_FILE
  mysqldump -h $MYSQL_IP -P $MYSQL_PORT -u "$MYSQL_USERNAME" --single-transaction --quick --no-create-info "$DB_NAME" "$TABLE_NAME" --where="$WHERE_CONDITION"  --default-character-set=utf8 >> $OUTPUT_FILE
else
  # 如果是其它，则使用mysql配合sed等命令，以便输出csv、txt时保持正确、可读的格式。
  # 获取表结构，确定哪些字段是 BIT 类型, 进行转换；同时如果类型为字符类型，则给结果加上引号避免含有英文逗号导致csv格式错误
  SELECT_COLUMNS=""
  COLUMN_TITLE="" # 表头
  COLUMN_INFO=$(mysql -h $MYSQL_IP -P $MYSQL_PORT -u "$MYSQL_USERNAME" -D "$DB_NAME" -e "SHOW FULL COLUMNS FROM \`$TABLE_NAME\`;" --default-character-set=utf8 | tail -n +2)
  BIT_COLUMNS=($(echo "$COLUMN_INFO" | awk '{if ($2 == "bit(1)") print $1}'))
  while IFS= read -r line; do
      COLUMN=$(echo "$line" | awk '{print $1}')
      COLUMN_TYPE=$(echo "$line" | awk '{print $2}')
      COLUMN_COMMENT=$(echo "$line" | awk -F'\t' '{print $NF}')
      COLUMN_TITLE+="\"${COLUMN_COMMENT}\"${DELIMITER}"
      if [[ " ${BIT_COLUMNS[@]} " =~ " ${COLUMN} " ]]; then
          SELECT_COLUMNS+="IF(\`${COLUMN}\` = b'1', 1, 0) AS \`${COLUMN}\`, "
      elif [[ "$COLUMN_TYPE" == *"text"* ]] || [[ "$COLUMN_TYPE" == *"varchar"* ]]; then
          SELECT_COLUMNS+="CONCAT('\"', REPLACE(\`${COLUMN}\`, '\"', '\"\"'), '\"') AS \`${COLUMN}\`, "
      else
          SELECT_COLUMNS+="\`${COLUMN}\`, "
      fi
  done <<< "$COLUMN_INFO"
  SELECT_COLUMNS=${SELECT_COLUMNS%, }  # 去掉最后一个逗号

  # 拼接表头
  COLUMN_TITLE=${COLUMN_TITLE%"${DELIMITER}" } # 去掉最后一个分隔符

  case $FILE_EXTENSION in
      txt)
          # 写入表头
          echo "$COLUMN_TITLE" > $OUTPUT_FILE
          # 导出为 TXT 文件，使用制表符分隔
          mysql -h $MYSQL_IP -P $MYSQL_PORT -u "$MYSQL_USERNAME" -e "SELECT $SELECT_COLUMNS FROM \`$DB_NAME\`.\`$TABLE_NAME\` WHERE $WHERE_CONDITION;"  --default-character-set=utf8 | sed '1d' | tr '\t' $DELIMITER >> $OUTPUT_FILE
          ;;
      csv)
          # 写入表头
          echo "$COLUMN_TITLE" > $OUTPUT_FILE
          # 导出为 CSV 文件，使用逗号分隔
          mysql -h $MYSQL_IP -P $MYSQL_PORT -u "$MYSQL_USERNAME" -e "SELECT $SELECT_COLUMNS FROM \`$DB_NAME\`.\`$TABLE_NAME\` WHERE $WHERE_CONDITION;"  --default-character-set=utf8 | sed '1d' | tr '\t' $DELIMITER >> $OUTPUT_FILE
          ;;
      xlsx)
          # 导出为 xlsx 文件: 先导出为csv, 再使用libreoffice转为xlsx
          TEMP_CSV_FILE="${OUTPUT_FILE%.*}.csv"
          # 写入表头
          echo "$COLUMN_TITLE" > $TEMP_CSV_FILE
          mysql -h $MYSQL_IP -P $MYSQL_PORT -u "$MYSQL_USERNAME" -e "SELECT $SELECT_COLUMNS FROM \`$DB_NAME\`.\`$TABLE_NAME\` WHERE $WHERE_CONDITION;"  --default-character-set=utf8 | sed '1d' | tr '\t' $DELIMITER >> $TEMP_CSV_FILE
          soffice --headless --convert-to xlsx --infilter="CSV:44,34,76,1" $TEMP_CSV_FILE --outdir "$(dirname "$OUTPUT_FILE")"
          if [[ -e "$OUTPUT_FILE" ]]; then
              echo "${OUTPUT_FILE}已生成, 删除临时文件$TEMP_CSV_FILE"
              rm -r $TEMP_CSV_FILE
          else
              echo "${OUTPUT_FILE}未生成, csv转xlsx失败。降级返回csv: $TEMP_CSV_FILE"
              OUTPUT_FILE=$TEMP_CSV_FILE
          fi
          ;;
      *)
          echo "Unsupported file format: $FILE_EXTENSION"
          echo
          exit 1
          ;;
  esac
fi

echo "$OUTPUT_FILE"
