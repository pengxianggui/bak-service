<template>
  <div class="task-config">
    <fast-table :option="tableOption">
      <fast-table-column prop="id" label="ID"/>
      <fast-table-column prop="categoryId" label="数据品类ID" width="120px"/>
      <fast-table-column-object prop="categoryName" label="数据品类名" required first-filter
                                :table-option="categoryOption" show-field="name"
                                :pick-map="{id: 'categoryId', name: 'categoryName'}"/>
      <fast-table-column-select prop="type" label="类型" required
                                :options="[{ label: '备份', value: 'bak'}, { label: '归档', value: 'archive'}]"
                                @change="handleTypeChange"/>
      <fast-table-column-input prop="cron" label="执行频率(Cron)" width="160" required/>
      <fast-table-column-input prop="cond" label="数据过滤条件(where)" width="180"/>
      <fast-table-column-switch prop="zip" label="是否压缩"/>
      <fast-table-column-switch prop="enable" label="是否启用">
        <template #normal="{row}">
          <el-switch v-model="row.row.enable" @change="switchEnable(row.row)"></el-switch>
        </template>
      </fast-table-column-switch>
      <fast-table-column-select prop="strategy" label="策略" required width="120px"
                                :options="[{ label: '保留最近指定天数', value: 'd'}, { label: '保留指定条数', value: 'r'}]"
                                :editable="({editRow}) => editRow.type === 'archive'"/>
      <fast-table-column-number prop="strategyValue" label="策略值" required
                                :editable="({editRow}) => editRow.type === 'archive'"/>
      <fast-table-column-number prop="keepFate" label="文件保存天数" width="130"/>
      <fast-table-column-date-picker prop="createTime" label="创建时间" :editable="false"/>
      <el-table-column label="操作" width="90px" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" icon="el-icon-video-play" @click="runOnce(scope)">运行一次</el-button>
        </template>
      </el-table-column>
      <template #button="scope">
        <el-button :size="scope.size" type="primary" plain @click="inputRun">输入执行</el-button>
      </template>
    </fast-table>
  </div>
</template>

<script>
import {FastTableOption, util} from 'fast-crud-ui'
import BakArchiveParamForm from "../components/BakArchiveParamForm.vue";

export default {
  name: "TaskConfig",
  data() {
    return {
      tableOption: new FastTableOption({
        context: this,
        module: 'taskConfig',
        style: {
          size: 'mini',
          flexHeight: true
        }
      }),
      categoryOption: new FastTableOption({
        context: this,
        module: 'dataCategory',
        render(h) {
          return [
            h('fast-table-column', {props: {prop: 'id', label: 'id'}}),
            h('fast-table-column', {props: {prop: 'code', label: '数据品类编码'}}),
            h('fast-table-column', {props: {prop: 'name', label: '数据品类名', firstFilter: true}})
          ]
        }
      })
    }
  },
  methods: {
    handleTypeChange(val, {editRow}) {
      if (val === 'bak') { // 重新置空策略值，避免脏数据
        editRow.strategy = null;
        editRow.strategyValue = null;
      }
    },
    switchEnable(row) {
      const {id, enable} = row;
      const url = `/taskConfig/${enable ? 'enable' : 'disable'}/${id}`
      this.$http.post(url).then(({data}) => {
        if (data === true) {
          this.$message.success('操作成功')
        } else {
          this.$message.error('操作失败')
        }
      })
    },
    inputRun() {
      util.openDialog({
        component: BakArchiveParamForm, dialogProps: {
          title: '手动输入参数执行备份/归档',
          width: '50%',
          size: 'small'
        }
      }).then((data) => {
        const {type, ...formData} = data
        this.$http.post(`/task/${type}`, formData).then(({data: url}) => {
          this.handleFileUrl(url);
        })
      })
    },
    runOnce(scope) {
      const {row: {row}} = scope
      this.$confirm('确定要运行一次吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => {
        this.$http.post(`/task/run/${row.id}`).then(({code, data: url, msg}) => {
          if(code === 0) {
            this.handleFileUrl(url);
          } else {
            this.$message.error(msg || "执行失败")
          }
        })
      })
    },
    handleFileUrl(url) {
      const baseURL = this.$http.defaults.baseURL
      this.$notify({
        title: '执行成功!',
        dangerouslyUseHTMLString: true,
        message: `<div><a href="${baseURL + url}">点击下载</a>, 你也可以稍后去【操作记录】中找到此次执行记录进行下载</div>`
      });
    }
  }
}
</script>

<style scoped>

</style>