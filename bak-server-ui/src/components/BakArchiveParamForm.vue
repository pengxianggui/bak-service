<template>
  <el-form :model="formData" ref="form" label-position="top">
    <el-row :gutter="20">
      <!-- 考虑到归档操作涉及数据删除,是比较敏感的, 限定入口只有已建的任务 -->
<!--      <el-col :span="12">-->
<!--        <el-form-item prop="type" label="类型" required>-->
<!--          <el-select v-model="formData.type">-->
<!--            <el-option label="备份" value="bak"></el-option>-->
<!--            <el-option label="归档" value="archive"></el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
<!--      </el-col>-->
<!--      <el-col :span="12">-->
<!--        <el-form-item prop="categoryCode" label="选择数据类目">-->
<!--          <fast-object-picker v-model="formData.categoryCode"-->
<!--                              :table-option="categoryOption"-->
<!--                              :pick-object="formData"-->
<!--                              :pick-map="{dbName: 'dbName', tableName: 'tableName', timeFieldName: 'timeFieldName'}"-->
<!--                              show-field="code"-->
<!--                              :multiple="false"></fast-object-picker>-->
<!--        </el-form-item>-->
<!--      </el-col>-->
    </el-row>
    <el-row :gutter="20" v-if="!categoryCode">
      <el-col :span="12">
        <el-form-item prop="dbName" label="数据库名" required>
          <el-input v-model="formData.dbName"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="tableName" label="表名" required>
          <el-input v-model="formData.tableName"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item prop="cond" label="where条件(例如: id=1)">
      <el-input v-model="formData.cond"></el-input>
    </el-form-item>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item prop="zip" label="是否ZIP压缩文件" required>
          <el-switch v-model="formData.zip" :active-value="true" :inactive-value="false"></el-switch>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="keepFate" label="文件留存天数">
          <el-input v-model="formData.keepFate"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-form-item prop="timeFieldName" label="时间字段" required v-if="formData.type === 'archive'">
          <el-input v-model="formData.timeFieldName"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item prop="strategy" label="策略" required v-if="formData.type === 'archive'">
          <el-select v-model="formData.strategy">
            <el-option label="数据保留最近指定天数, 其它归档" value="d"></el-option>
            <el-option label="数据保留指定条数, 其它归档" value="r"></el-option>
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="8">
        <el-form-item prop="strategyValue" label="策略值" required v-if="formData.type === 'archive'">
          <el-input-number v-model="formData.strategyValue"></el-input-number>
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item>
      <div style="display: flex; justify-content: space-between">
        <span style="flex: 1"></span>
        <el-button type="primary" @click="execute">执行{{ formData.type === 'bak' ? '备份' : (formData.type === 'archive' ? '归档' : '')}}</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-form-item>
  </el-form>
</template>

<script>
import {h} from 'vue'
import {FastTableOption, FastTableColumn} from 'fast-crud-ui3'

export default {
  name: "BakArchiveParamForm",
  props: {
    categoryCode: {
      type: String,
      required: false
    },
    cond: {
      type: String,
      required: false
    }
  },
  data() {
    return {
      formData: {
        type: 'bak',
        categoryCode: this.categoryCode,
        dbName: null,
        tableName: null,
        cond: this.cond,
        zip: true,
        keepFate: null,
        timeFieldName: null,
        strategy: null,
        strategyValue: null
      },
      categoryOption: new FastTableOption({
        context: this,
        module: 'dataCategory',
        render: () => {
          return [
            h(FastTableColumn, {prop: 'id', label: 'id'}),
            h(FastTableColumn, {prop: 'code', label: '数据类目编码'}),
            h(FastTableColumn, {prop: 'name', label: '数据类目名', firstFilter: true})
          ]
        }
      })
    }
  },
  methods: {
    execute() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return;
        }
        this.$emit('ok', this.formData)
      })
    },
    cancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>

</style>