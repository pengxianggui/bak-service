# 备份服务

> 一个通用的备份、还原、导出、归档服务。
> 通过mysql和mysqldump实现针对数据库表的备份、还原、导出、归档操作。

- 备份：可针对特定库、表，甚至特定数据(where条件)进行备份。可定时、可手动。
- 还原：可指定备份记录进行还原
- 导出：可导出指定库、表，甚至特定数据(where条件)，导出格式可为sql、txt、csv、xlsx
- 归档：可针对特定库、表的数据执行“剪切”操作，实现阈值(总条数/时长)控制。可定时、可手动。

## 技术栈
- SpringBoot
- Vue
- Fast-Crud

## 模块
- bak-core: 核心逻辑, 非spring boot项目建议引入此模块
- bak-spring-boot-starter: 基于bak-core的封装，springboot建议引入此模块，开箱即用
- bak-server: 可独立部署的备份/归档服务，独立于你的项目运行
- bak-server-ui: bak-server配套的前端，可独立部署，也可打包到bak-server中。如果你引用的是bak-spring-boot-starter，前端代码可以借鉴拷贝bak-server-ui中的组件。
