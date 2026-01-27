<template>
  <div class="data-category">
    <fast-table :option="tableOption">
      <fast-table-column prop="id" label="ID" width="90"/>
      <fast-table-column-input prop="code" label="编码" required/>
      <fast-table-column-input prop="name" label="名称" required width="180" :filter="0"/>
      <fast-table-column-input prop="dbName" label="数据库名" required/>
      <fast-table-column-input prop="tableName" label="表名" required/>
      <fast-table-column-input prop="timeFieldName" label="时间字段"/>
      <fast-table-column-date-picker prop="createTime" label="创建时间" :editable="false"/>
    </fast-table>
  </div>
</template>

<script>
import {FastTableOption} from 'fast-crud-ui3'

export default {
  name: "DataCategory",
  data() {
    return {
      tableOption: new FastTableOption({
        context: this,
        module: 'dataCategory',
        sortField: 'createTime',
        createTimeField: 'createTime',
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