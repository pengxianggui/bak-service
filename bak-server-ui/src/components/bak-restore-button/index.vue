<template>
  <div style="display: inline-block;">
    <el-button-group class="buttons">
      <el-button @click="bak" :type="textButton ? 'text' : 'success'" :size="size" icon="el-icon-folder-add">备份
      </el-button>
      <el-button @click="restore" :type="textButton ? 'text' : 'warning'" :size="size" icon="el-icon-refresh">还原
      </el-button>
    </el-button-group>
  </div>
</template>

<script>
import {FastTableOption, util} from 'fast-crud-ui'
import BakArchiveParamForm from "@/components/BakArchiveParamForm.vue";
// 备份还原按钮组：针对单个数据品类全表/单条 记录的备份还原操作：涉及：
//  1. 两个按钮：备份/还原
//  2. 两个弹窗: 执行备份/归档、或创建任务配置; 筛选指定备份记录进行还原。
export default {
  name: "BakRestoreButton",
  props: {
    // 数据品类编码
    categoryCode: {
      type: String,
      required: true
    },
    pkName: String,
    pkVal: [String, Number],
    size: {
      type: String,
      default: () => 'default'
    },
    textButton: Boolean
  },
  data() {
    return {}
  },
  methods: {
    bak() {
      util.openDialog({
        component: BakArchiveParamForm,
        props: {
          categoryCode: this.categoryCode,
          cond: util.isEmpty(this.pkVal) ? null : `${this.pkName}=${this.pkVal}`
        },
        dialogProps: {
          title: '针对【操作记录】执行备份/归档',
          width: '50%',
          size: 'small'
        }
      }).then((data) => {
        const {type, ...formData} = data
        this.$http.post(`/task/${type}`, formData).then(({code, data: url, msg}) => {
          if(code === 0) {
            this.handleFileUrl(url);
          } else {
            this.$message.error(msg || "执行失败")
          }
        })
      })
    },
    restore() {
      util.pick({
        option: new FastTableOption({
          title: '选择备份记录进行还原',
          module: 'oprLog',
          sortField: 'createTime',
          conds: [
            {col: 'type', val: 'bak'},
            {col: 'success', val: true},
          ],
          render(h) {
            return [
              h('fast-table-column', {props: {prop: 'id', label: 'id'}}),
              h('fast-table-column', {props: {prop: 'operator', label: '操作人', width: '90'}}),
              h('fast-table-column', {props: {prop: 'categoryCode', label: '数据品类编码', width: '120'}}),
              h('fast-table-column', {props: {prop: 'categoryName', label: '数据品类名称', width: '160'}}),
              // h('fast-table-column', {props: {prop: 'dbName', label: '数据库名'}}),
              // h('fast-table-column', {props: {prop: 'tableName', label: '表名'}}),
              // h('fast-table-column-select', {
              //   props: {
              //     prop: 'type',
              //     label: '类型',
              //     options: "[{ label: '备份', value: 'bak'}, { label: '归档', value: 'archive'}, { label: '还原', value: 'restore'}, { label: '导出', value: 'export'}]"
              //   }
              // }),
              h('fast-table-column-switch', {props: {prop: 'success', label: '是否执行成功'}}),
              h('fast-table-column-file', {props: {prop: 'fileUrl', label: '文件'}}),
              h('fast-table-column-switch', {props: {prop: 'expired', label: '是否失效'}}),
              h('fast-table-column', {props: {prop: 'cond', label: 'where条件'}}),
              h('fast-table-column', {props: {prop: 'msg', label: '日志', showOverflowTooltip: true, width: '150'}}),
              h('fast-table-column', {props: {prop: 'createTime', label: '执行时间', width: '180'}}),
            ]
          }
        }),
        multiple: false,
        dialog: {
          width: '80%'
        }
      }).then((data) => {
        const {row} = data
        this.$http.post(`/task/restore/${row.id}`).then(({code, msg}) => {
          if (code === 0) {
            this.$message.success(msg || '还原成功!')
          } else {
            this.$message.error(msg || '还原失败!')
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
.buttons {
  margin: 0 8px;
}
</style>
