<template>
  <div id="app">
    <el-menu class="menu" :default-active="activeMenu" :router="true">
      <el-menu-item index="/task-config">
        <i class="el-icon-menu"></i>
        <span slot="title">任务配置</span>
      </el-menu-item>
      <el-menu-item index="/data-category">
        <i class="el-icon-document"></i>
        <span slot="title">数据品类</span>
      </el-menu-item>
      <el-menu-item index="/opr-log">
        <i class="el-icon-setting"></i>
        <span slot="title">操作记录</span>
      </el-menu-item>
    </el-menu>
    <router-view class="router-view"></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    activeMenu() {
      return this.$route.path;
    }
  }
}
</script>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
#app {
  display: flex;
  .menu {
    width: 200px;
  }
  .router-view {
    flex: 1;
    overflow: auto;
  }
}
</style>
