<template>
  <div class="task-config">
    <fast-table :option="tableOption">
      <!--      <template #button="scope">-->
      <!--        <el-button :size="scope.size" type="primary" plain @click="inputRun">输入执行</el-button>-->
      <!--      </template>-->
      <fast-table-column prop="id" label="ID" width="60"/>
      <fast-table-column prop="categoryId" label="数据类目ID" width="120" hidden/>
      <fast-table-column-object prop="categoryName" label="数据类目名" required width="160" :filter="0"
                                :table-option="categoryOption" show-field="name"
                                :pick-map="{id: 'categoryId', name: 'categoryName'}"/>
      <fast-table-column-select prop="type" label="类型" required width="100"
                                :options="[{ label: '备份', value: 'bak'}, { label: '归档', value: 'archive'}]"
                                @change="handleTypeChange"/>
      <fast-table-column-input prop="cron" label="执行频率(Cron)" width="180" required/>
      <fast-table-column-input prop="cond" label="数据过滤条件(不含where)" width="180"/>
      <fast-table-column-switch prop="zip" label="是否压缩"/>
      <fast-table-column-switch prop="enable" label="是否启用"/>
      <fast-table-column-select prop="strategy" label="策略" required width="140"
                                :options="[{ label: '保留最近指定天数', value: 'd'}, { label: '保留指定条数', value: 'r'}]"
                                :editable="({editRow}) => editRow.type === 'archive'"/>
      <fast-table-column-number prop="strategyValue" label="策略值" required width="120"
                                :editable="({editRow}) => editRow.type === 'archive'"/>
      <fast-table-column-number prop="keepFate" label="文件保存天数" width="130"/>
      <fast-table-column-date-picker prop="createTime" label="创建时间" :editable="false" width="180"/>
      <el-table-column label="操作" width="90px" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="manualRun(scope)">运行一次</el-button>
        </template>
      </el-table-column>
    </fast-table>
  </div>
</template>

<script>
import {FastTableOption, FastTableColumn} from 'fast-crud-ui3'
import {h} from 'vue'
import http from '../http'

export default {
  name: "TaskConfig",
  data() {
    return {
      tableOption: new FastTableOption({
        context: this,
        baseUrl: 'taskConfig',
        createTimeField: 'createTime',
        style: {
          flexHeight: true
        }
      }),
      categoryOption: new FastTableOption({
        context: this,
        baseUrl: 'dataCategory',
        render: () => {
          return [
            h(FastTableColumn, {prop: 'id', label: 'id'}),
            h(FastTableColumn, {prop: 'code', label: '数据类目编码'}),
            h(FastTableColumn, {prop: 'name', label: '数据类目名'})
          ]
        }
      })
    }
  },
  methods: {
    handleTypeChange(val, {row: {editRow}}) {
      if (val === 'bak') {
        editRow.strategy = null;
        editRow.strategyValue = null;
      }
    },
    inputRun() {
      // TODO 输入执行
    },
    manualRun(scope) {
      const {row: {row: {id}}} = scope
      http.post(`/task/run/${id}`)
    }
  }
}
</script>

<style scoped>

</style>