<template>
  <div class="opr-log">
    <fast-table :option="tableOption">
      <fast-table-column prop="id" label="ID" width="90"/>
      <fast-table-column prop="operator" label="操作人" width="90"/>
      <fast-table-column prop="categoryCode" label="数据品类编码" width="140"/>
      <fast-table-column prop="categoryName" label="数据品类名称" width="160" :filter="0"/>
      <fast-table-column prop="dbName" label="数据库名" width="120"/>
      <fast-table-column prop="tableName" label="表名" width="130"/>
      <fast-table-column-select prop="type" label="类型" :quick-filter="true"
                                :options="[{ label: '备份', value: 'bak'}, { label: '归档', value: 'archive'}, { label: '还原', value: 'restore'}, { label: '导出', value: 'export'}]"/>
      <fast-table-column-switch prop="success" label="是否成功" :quick-filter="true"/>
      <fast-table-column-file prop="fileUrl" label="文件" width="150"/>
      <fast-table-column-switch prop="expired" label="是否失效"/>
      <fast-table-column prop="cond" label="where条件" width="160"/>
      <fast-table-column-textarea prop="msg" label="日志" width="150" :show-length="80"/>
      <fast-table-column-date-picker prop="createTime" label="创建时间" width="180"/>
      <template #button>
        <bak-archive-button size="mini" category-code="oprLog"></bak-archive-button>
      </template>
      <el-table-column label="操作" width="90px" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" icon="el-icon-video-play" @click="restore(scope)" v-if="scope.row.row.type === 'bak'">还原</el-button>
        </template>
      </el-table-column>
    </fast-table>
  </div>
</template>

<script>
import {FastTableOption} from 'fast-crud-ui3'
import BakArchiveButton from '../components/bak-restore-button'
import {FastTableOption} from 'fast-crud-ui'
import http from '../http'

export default {
  name: "OprLog",
  components: {BakArchiveButton},
  data() {
    return {
      tableOption: new FastTableOption({
        context: this,
        module: 'oprLog',
        sortField: 'createTime',
        createTimeField: 'createTime',
        insertable: false,
        updatable: false,
        deletable: false,
        enableMulti: false,
        style: {
          size: 'mini',
          flexHeight: true
        }
      })
    }
  },
  methods: {
    restore(scope) {
      this.$confirm('此操作会更改此数据品类对应的表数据', '确定要还原吗？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const {row: {row}} = scope
        http.post(`/task/restore/${row.id}`).then(({msg}) => {
          this.$message.success(msg || '还原成功!');
        });
      }).catch((error) => {
        this.$message.error(error.message || '还原失败!');
      })
    }
  }
}
</script>

<style scoped>

</style>