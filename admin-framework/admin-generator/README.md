# 代码生成器：当前项目适配说明

## 这次改动

- `gen_table.id`、`gen_table_column.id` 是元数据主键；`gen_table_column.table_id` 保留为关联字段。业务表自己的主键仍按实际表结构生成。
- 移除 `tplWebType` 配置、接口字段和数据库列，固定生成 Vue 3 + Element Plus + TypeScript `<script setup>`。保留普通表、树表、主子表、详情页和表单列数配置。
- 生成后端实体、查询 DTO、保存 DTO、`BaseMapperX` Mapper、Service、Controller。查询使用 `LambdaQueryWrapper`；更新使用 `LambdaUpdateWrapper`，允许把可编辑字段清空。不再输出普通 CRUD XML。
- 对接项目的 `ApiResult`、`PageResult.records`、`ExcelService`、`@se.hasPermission` 和当前菜单结构；新增/修改分别验证必填字段，主子表保存使用事务。
- 实体只有在物理表包含项目全部审计字段时才继承 `BaseEntity`，避免查询不存在的字段。支持单列 Long/Integer/String 主键；复合主键明确提示不支持。
- 生成器管理页面改为严格 TS，修复分页、JSON 请求体、编辑路由、预览、复制、ZIP 下载、字段排序和菜单选择。同步表结构保留原来的生成配置。
- `admin-server` 引入 `admin-generator`。生成器自身读取数据库元数据的 Mapper XML 保留，和生成出来的业务 Mapper 是两回事。

## 模板命名约定

Java 模板统一采用小写连字符文件名，名称对应输出类型。`sub-` 表示主子表中的子表模板，`-body.vm` 表示被 `#parse` 引用的公共片段。

| 模板 | 用途 / 输出 |
| --- | --- |
| `entity.java.vm` | 主实体 `Xxx.java`，输出到 `model/<模块>/entity` |
| `sub-entity.java.vm` | 子实体，同样输出到 `entity` |
| `entity-body.vm` | 主实体和子实体共用的正文 |
| `page-req-dto.java.vm` | 查询参数 `XxxPageReqDTO.java` |
| `save-dto.java.vm` / `sub-save-dto.java.vm` | 保存参数 `XxxSaveDTO.java` |
| `save-dto-body.vm` | 保存 DTO 共用的正文 |
| `mapper.java.vm` / `sub-mapper.java.vm` | Mapper |
| `service.java.vm` / `service-impl.java.vm` | Service 接口与实现 |
| `controller.java.vm` | Controller |

模板注册、输出路径映射和 `#parse` 引用使用同一套名称。当前生成器输出 entity 和 DTO；响应 VO 模板尚未实现。

## 数据库执行顺序

2026-09-20 已完成当前开发库 `localhost:3306/admin_platform` 的迁移：两张表主键改为 `id`，保留字段表 `table_id`，移除 `tpl_web_type`。迁移前已备份；当时两张表均为 0 条记录，迁移后结构及关联检查通过。其他环境按以下步骤执行：

1. 从未安装生成器：执行根目录 `sql/generator-init.sql`。
2. 已有 RuoYi 生成器表：备份两张元数据表，然后执行 `sql/generator-migrate.sql`。保留 ID 值，重命名主键，删除已取消的 `tpl_web_type`，补充缺失的表单列数。
3. 需要补充生成器菜单/权限：执行 `sql/generator-menu.sql`，然后在角色管理分配菜单和按钮权限。

`sql/MySQL.sql` 只同步调整了生成器两张表的定义；它是包含其他表删除语句的全量脚本，已有项目不要用它做本次升级。

默认 `gen.allowOverwrite=false`，使用预览和 ZIP 下载。若要启用自定义路径生成，由项目配置打开开关并填写服务端绝对路径。

## 上传方案：本地 Vben 与 RuoYi 对比

本次读取了本地 `E:/vs_workspace/vue-vben-admin`、`E:/mvc_workspace/RuoYi-Vue`。

| 项目 | 本地源码采用的方式 | 本项目取舍 |
| --- | --- | --- |
| Vben | `requestClient.upload` 统一发 FormData；示例通过回调报告结果 | 复用本项目统一请求、鉴权、取消信号和错误处理，按传输事件报告进度 |
| RuoYi | 文件/图片组件封装类型、大小、数量、回显；保存逗号分隔路径；组件自己设置 action/token 和解释响应 | 保留字段路径及表单体验，改为明确 TS 类型和项目 `ApiResult.data` 返回值 |
| 当前实现 | 一个 `FileUpload` 组件通过 `mode` 区分文件/图片 | 支持多文件、拖入、进度、取消、失败重试、图片预览、禁用、表单校验和自定义上传函数 |

新增 `admin-ui/src/components/upload/` 和 `admin-ui/src/api/common/file.ts`，已有示例上传适配器也复用它。

```vue
<script setup lang="ts">
import { ref } from 'vue';
import { FileUpload } from '@/components/upload';
const files = ref('');
const uploading = ref(false);
</script>

<template>
  <FileUpload v-model="files" mode="image" :limit="5" :max-size="10" @uploading="uploading = $event" />
  <el-button :disabled="uploading">保存</el-button>
</template>
```

- 绑定值为逗号分隔的服务器路径，例如 `/profile/upload/2026/09/20/uuid.png`；多文件业务字段应预留足够长度。
- `mode="file"` 默认支持常用文档、图片、压缩包、视频；`mode="image"` 默认支持 jpg/jpeg/png/gif/bmp。客户端默认 10 MB，与当前 Spring 单文件限制一致；服务端仍校验扩展名和大小。
- 上传接口复用 `/common/upload`，存储沿用 `app.profile` 下的 `upload` 目录。没有增加存储数据库表或云存储依赖。
- 新增 `/profile/upload/**` 资源映射；前端访问 URL 按 `VITE_API_BASE` 解析，数据库保存相对路径。
- 修复后台 Instant 日期目录格式化、路径拼接和越界路径检查；通用上传使用 UUID 文件名。
- 移除文件代表从表单解除关联，不会物理删除服务器文件。清理未关联文件需要后续确定文件生命周期策略。
- 生成模板已接入文件/图片控件；富文本沿用现有 Tiptap，图片上传接同一接口；上传期间禁用保存。

## 输出目录

以包 `dev.geo.admin.system`、模块 `system`、业务 `sample` 为例：

- 后端：`admin-module-system/src/main/java/dev/geo/admin/system/` 下的 `model/system/entity`、`model/system/dto`、`mapper/system`、`service/system`、`controller/system`。
- API：`admin-ui/src/api/system/sample.ts`。
- 类型：`admin-ui/src/types/base/api/system/sample.ts`。
- 页面：`admin-ui/src/views/sys/system/sample/index.vue`，按配置附带详情页。
- 菜单：`sql/sample-menu.sql`。

选择新的业务模块名时，输出的是该模块的代码目录；Maven 模块及服务端依赖需要按项目实际模块结构接入。

## 验证

项目根目录执行：

```powershell
mvn -o -pl admin-server -am -DskipTests compile
python admin-framework/admin-generator/tests/check.py
```

前端目录执行：

```powershell
node node_modules/vitest/vitest.mjs run --config vitest.upload.config.ts
```

本次验证结果：服务端编译通过；5 组模板样例输出 63 个文件，38 个生成的 Java 文件编译通过；MyBatis XML、SQL 语法、元数据排序、字符串主键返回数据、本地文件上传和路径校验通过；上传组件 6 项行为测试通过；相关文件 ESLint 通过。

管理页面、上传组件及生成的 Vue/TS 样例未发现目标文件类型错误。项目依赖链中仍有钉钉登录、布局、其他页面等已有类型错误，因此未宣称全项目 type-check 通过。当前开发库迁移已完成；业务接口/浏览器联调尚未完成。

