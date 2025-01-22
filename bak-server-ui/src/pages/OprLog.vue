<template>
  <div class="opr-log">
    <fast-table :option="tableOption">
      <fast-table-column prop="id" label="ID"/>
      <fast-table-column prop="operator" label="操作人"/>
      <fast-table-column prop="categoryCode" label="数据品类编码"/>
      <fast-table-column prop="categoryName" label="数据品类名称"/>
      <fast-table-column prop="dbName" label="数据库名"/>
      <fast-table-column prop="tableName" label="表名"/>
      <fast-table-column-select prop="type" label="类型" :editable="false"
                                :options="[{ label: '备份', value: 'bak'}, { label: '归档', value: 'archive'}, { label: '还原', value: 'restore'}, { label: '导出', value: 'export'}]"/>
      <fast-table-column-switch prop="success" label="是否成功" :editable="false"/>
      <fast-table-column-file prop="filePath" label="文件" :editable="false"/>
      <fast-table-column-switch prop="expired" label="是否失效" :editable="false"/>
      <fast-table-column prop="cond" label="where条件" :editable="false"/>
      <fast-table-column-textarea prop="msg" label="日志" :editable="false"/>
      <fast-table-column-date-picker prop="createTime" label="创建时间" :editable="false"/>
    </fast-table>
  </div>
</template>

<script>
import {FastTableOption} from 'fast-crud-ui'

export default {
  name: "OprLog",
  data() {
    return {
      tableOption: new FastTableOption({
        context: this,
        module: 'oprLog',
        sortField: 'createTime',
        insertable: false,
        updatable: false,
        deletable: false,
        style: {
          size: 'mini',
          flexHeight: true
        }
      })
    }
  }
}
</script>

<style scoped>

</style>