<template>
  <div class="right-board">
    <el-tabs
      v-model="currentTab"
      stretch
      class="center-tabs"
    >
      <el-tab-pane
        label="组件属性"
        name="field"
      />
      <el-tab-pane
        label="表单属性"
        name="form"
      />
    </el-tabs>
    <div class="field-box">
      <a
        class="document-link"
        target="_blank"
        :href="documentLink"
        title="查看组件文档"
      >
        <el-icon>
          <Link />
        </el-icon>
      </a>
      <el-scrollbar class="right-scrollbar">
        <!-- 组件属性 -->
        <el-form
          v-if="activeData"
          v-show="currentTab === 'field' && showField"
          size="default"
          label-width="90px"
          label-position="top"
          style=""
        >
          <el-form-item
            v-if="activeData.changeTag"
            label="组件类型"
          >
            <el-select
              v-model="activeData.tagIcon"
              placeholder="请选择组件类型"
              :style="{ width: '100%' }"
              @change="tagChange"
            >
              <el-option-group
                v-for="group in tagList"
                :key="group.label"
                :label="group.label"
              >
                <el-option
                  v-for="item in group.options"
                  :key="item.label"
                  :label="item.label"
                  :value="item.tagIcon"
                >
                  <IconifyIcon
                    class="node-icon inline-block align-[-2px]"
                    :icon="`svg:${item.tagIcon}`"
                    style="margin-right: 10px"
                  />
                  <span> {{ item.label }}</span>
                </el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="activeData.vModel !== undefined"
            label="字段名"
          >
            <el-input
              v-model="activeData.vModel"
              placeholder="请输入字段名（v-model）"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.componentName !== undefined"
            label="组件名"
          >
            {{ activeData.componentName }}
          </el-form-item>
          <el-form-item
            v-if="activeData.label !== undefined"
            label="标题"
          >
            <el-input
              v-model="activeData.label"
              placeholder="请输入标题"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.placeholder !== undefined"
            label="占位提示"
          >
            <el-input
              v-model="activeData.placeholder"
              placeholder="请输入占位提示"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['start-placeholder'] !== undefined"
            label="开始占位"
          >
            <el-input
              v-model="activeData['start-placeholder']"
              placeholder="请输入占位提示"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['end-placeholder'] !== undefined"
            label="结束占位"
          >
            <el-input
              v-model="activeData['end-placeholder']"
              placeholder="请输入占位提示"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.span !== undefined"
            label="表单栅格"
          >
            <el-slider
              v-model="activeData.span"
              :max="24"
              :min="1"
              :marks="{ 12: '' }"
              @change="spanChange"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.layout === 'rowFormItem'"
            label="栅格间隔"
          >
            <el-input-number
              v-model="activeData.gutter"
              :min="0"
              placeholder="栅格间隔"
            />
          </el-form-item>

          <el-form-item
            v-if="activeData.justify !== undefined"
            label="水平排列"
          >
            <el-select
              v-model="activeData.justify"
              placeholder="请选择水平排列"
              :style="{ width: '100%' }"
            >
              <el-option
                v-for="(item, index) in justifyOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="activeData.align !== undefined"
            label="垂直排列"
          >
            <el-radio-group v-model="activeData.align">
              <el-radio-button value="top" />
              <el-radio-button value="middle" />
              <el-radio-button value="bottom" />
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="activeData.labelWidth !== undefined"
            label="标签宽度"
          >
            <el-input
              v-model.number="activeData.labelWidth"
              type="number"
              placeholder="请输入标签宽度"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.style && activeData.style.width !== undefined"
            label="组件宽度"
          >
            <el-input
              v-model="activeData.style.width"
              placeholder="请输入组件宽度"
              clearable
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.vModel !== undefined"
            label="默认值"
          >
            <el-input
              :model-value="setDefaultValue(activeData.defaultValue)"
              placeholder="请输入默认值"
              @input="onDefaultValueInput"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-checkbox-group'"
            label="至少应选"
          >
            <el-input-number
              :model-value="activeData.min"
              :min="0"
              placeholder="至少应选"
              @update:model-value="activeData.min = $event ?? undefined"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-checkbox-group'"
            label="最多可选"
          >
            <el-input-number
              :model-value="activeData.max"
              :min="0"
              placeholder="最多可选"
              @update:model-value="activeData.max = $event ?? undefined"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.prepend !== undefined"
            label="前缀"
          >
            <el-input
              v-model="activeData.prepend"
              placeholder="请输入前缀"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.append !== undefined"
            label="后缀"
          >
            <el-input
              v-model="activeData.append"
              placeholder="请输入后缀"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['prefix-icon'] !== undefined"
            label="前图标"
          >
            <el-input
              v-model="activeData['prefix-icon']"
              placeholder="请输入前图标名称"
            >
              <template #append>
                <el-button
                  :icon="Pointer"
                  @click="openIconsDialog('prefix-icon')"
                >
                  选择
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item
            v-if="activeData['suffix-icon'] !== undefined"
            label="后图标"
          >
            <el-input
              v-model="activeData['suffix-icon']"
              placeholder="请输入后图标名称"
            >
              <template #append>
                <el-button
                  :icon="Pointer"
                  @click="openIconsDialog('suffix-icon')"
                >
                  选择
                </el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-cascader'"
            label="选项分隔符"
          >
            <el-input
              v-model="activeData.separator"
              placeholder="请输入选项分隔符"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.autosize !== undefined"
            label="最小行数"
          >
            <el-input-number
              v-model="activeData.autosize.minRows"
              :min="1"
              placeholder="最小行数"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.autosize !== undefined"
            label="最大行数"
          >
            <el-input-number
              v-model="activeData.autosize.maxRows"
              :min="1"
              placeholder="最大行数"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.min !== undefined"
            label="最小值"
          >
            <el-input-number
              v-model="activeData.min"
              placeholder="最小值"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.max !== undefined"
            label="最大值"
          >
            <el-input-number
              v-model="activeData.max"
              placeholder="最大值"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.step !== undefined"
            label="步长"
          >
            <el-input-number
              v-model="activeData.step"
              placeholder="步数"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-input-number'"
            label="精度"
          >
            <el-input-number
              v-model="activeData.precision"
              :min="0"
              placeholder="精度"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-input-number'"
            label="按钮位置"
          >
            <el-radio-group v-model="activeData['controls-position']">
              <el-radio-button value=""> 默认 </el-radio-button>
              <el-radio-button value="right"> 右侧 </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="activeData.maxlength !== undefined"
            label="最多输入"
          >
            <el-input
              v-model.number="activeData.maxlength"
              placeholder="请输入字符长度"
            >
              <template #append>
                <span>个字符</span>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item
            v-if="activeData['active-text'] !== undefined"
            label="开启提示"
          >
            <el-input
              v-model="activeData['active-text']"
              placeholder="请输入开启提示"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['inactive-text'] !== undefined"
            label="关闭提示"
          >
            <el-input
              v-model="activeData['inactive-text']"
              placeholder="请输入关闭提示"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['active-value'] !== undefined"
            label="开启值"
          >
            <el-input
              :model-value="setDefaultValue(activeData['active-value'])"
              placeholder="请输入开启值"
              @input="onSwitchValueInput($event, 'active-value')"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['inactive-value'] !== undefined"
            label="关闭值"
          >
            <el-input
              :model-value="setDefaultValue(activeData['inactive-value'])"
              placeholder="请输入关闭值"
              @input="onSwitchValueInput($event, 'inactive-value')"
            />
          </el-form-item>
          <el-form-item
            v-if="
              activeData.type !== undefined &&
              'el-date-picker' === activeData.tag
            "
            label="时间类型"
          >
            <el-select
              v-model="activeData.type"
              placeholder="请选择时间类型"
              :style="{ width: '100%' }"
              @change="dateTypeChange"
            >
              <el-option
                v-for="(item, index) in dateOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="activeData.name !== undefined"
            label="文件字段名"
          >
            <el-input
              v-model="activeData.name"
              placeholder="请输入上传文件字段名"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.accept !== undefined"
            label="文件类型"
          >
            <el-select
              v-model="activeData.accept"
              placeholder="请选择文件类型"
              :style="{ width: '100%' }"
              clearable
            >
              <el-option
                label="图片"
                value="image/*"
              />
              <el-option
                label="视频"
                value="video/*"
              />
              <el-option
                label="音频"
                value="audio/*"
              />
              <el-option
                label="excel"
                value=".xls,.xlsx"
              />
              <el-option
                label="word"
                value=".doc,.docx"
              />
              <el-option
                label="pdf"
                value=".pdf"
              />
              <el-option
                label="txt"
                value=".txt"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="activeData.fileSize !== undefined"
            label="文件大小"
          >
            <el-input
              v-model.number="activeData.fileSize"
              placeholder="请输入文件大小"
            >
              <template #append>
                <el-select
                  v-model="activeData.sizeUnit"
                  :style="{ width: '66px' }"
                >
                  <el-option
                    label="KB"
                    value="KB"
                  />
                  <el-option
                    label="MB"
                    value="MB"
                  />
                  <el-option
                    label="GB"
                    value="GB"
                  />
                </el-select>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item
            v-if="activeData.action !== undefined"
            label="上传地址"
          >
            <el-input
              v-model="activeData.action"
              placeholder="请输入上传地址"
              clearable
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['list-type'] !== undefined"
            label="列表类型"
          >
            <el-radio-group
              v-model="activeData['list-type']"
              size="small"
            >
              <el-radio-button value="text"> text </el-radio-button>
              <el-radio-button value="picture"> picture </el-radio-button>
              <el-radio-button value="picture-card">
                picture-card
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="activeData.buttonText !== undefined"
            v-show="'picture-card' !== activeData['list-type']"
            label="按钮文字"
          >
            <el-input
              v-model="activeData.buttonText"
              placeholder="请输入按钮文字"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['range-separator'] !== undefined"
            label="分隔符"
          >
            <el-input
              v-model="activeData['range-separator']"
              placeholder="请输入分隔符"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['picker-options'] !== undefined"
            label="时间段"
          >
            <el-input
              v-model="activeData['picker-options'].selectableRange"
              placeholder="请输入时间段"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.format !== undefined"
            label="时间格式"
          >
            <el-input
              :model-value="activeData.format"
              placeholder="请输入时间格式"
              @input="setTimeValue($event)"
            />
          </el-form-item>
          <template
            v-if="
              ['el-checkbox-group', 'el-radio-group', 'el-select'].indexOf(
                activeData.tag ?? '',
              ) > -1
            "
          >
            <el-divider>选项</el-divider>
            <draggable
              :list="activeData.options"
              :animation="340"
              group="selectItem"
              handle=".option-drag"
              item-key="label"
            >
              <template
                #item="{
                  element,
                  index,
                }: {
                  element: FieldOption
                  index: number
                }"
              >
                <div
                  :key="index"
                  class="select-item"
                >
                  <div class="select-line-icon option-drag">
                    <el-icon><Operation /></el-icon>
                  </div>
                  <el-input
                    v-model="element.label"
                    placeholder="选项名"
                    size="small"
                  />
                  <el-input
                    placeholder="选项值"
                    size="small"
                    :model-value="String(element.value)"
                    @input="setOptionValue(element, $event)"
                  />
                  <div
                    class="close-btn select-line-icon"
                    @click="activeData?.options?.splice(index, 1)"
                  >
                    <el-icon>
                      <Remove />
                    </el-icon>
                  </div>
                </div>
              </template>
            </draggable>
            <div>
              <el-button
                :icon="CirclePlus"
                style="margin-left: 8px; margin-top: 10px"
                text
                bg
                type="primary"
                @click="addSelectItem"
              >
                添加选项
              </el-button>
            </div>
            <el-divider />
          </template>

          <template v-if="activeData.tag === 'el-cascader'">
            <el-divider>选项</el-divider>
            <el-form-item label="数据类型">
              <el-radio-group
                v-model="activeData.dataType"
                size="small"
              >
                <el-radio-button value="dynamic"> 动态数据 </el-radio-button>
                <el-radio-button value="static"> 静态数据 </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <template v-if="activeData.dataType === 'dynamic'">
              <el-form-item label="标签键名">
                <el-input
                  v-model="activeData.labelKey"
                  placeholder="请输入标签键名"
                />
              </el-form-item>
              <el-form-item label="值键名">
                <el-input
                  v-model="activeData.valueKey"
                  placeholder="请输入值键名"
                />
              </el-form-item>
              <el-form-item label="子级键名">
                <el-input
                  v-model="activeData.childrenKey"
                  placeholder="请输入子级键名"
                />
              </el-form-item>
            </template>

            <el-tree
              v-if="activeData.dataType === 'static'"
              draggable
              :data="activeData.options"
              node-key="id"
              :expand-on-click-node="false"
              :render-content="renderContent"
            />
            <div v-if="activeData.dataType === 'static'">
              <el-button
                :icon="CirclePlus"
                style="margin-left: 0; margin-top: 10px"
                type="primary"
                text
                bg
                @click="addTreeItem"
              >
                添加父级
              </el-button>
            </div>
            <el-divider />
          </template>

          <el-form-item
            v-if="activeData.optionType !== undefined"
            label="选项样式"
          >
            <el-radio-group v-model="activeData.optionType">
              <el-radio-button value="default"> 默认 </el-radio-button>
              <el-radio-button value="button"> 按钮 </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="activeData['active-color'] !== undefined"
            label="开启颜色"
          >
            <el-color-picker v-model="activeData['active-color']" />
          </el-form-item>
          <el-form-item
            v-if="activeData['inactive-color'] !== undefined"
            label="关闭颜色"
          >
            <el-color-picker v-model="activeData['inactive-color']" />
          </el-form-item>

          <el-form-item
            v-if="activeData['allow-half'] !== undefined"
            label="允许半选"
          >
            <el-switch v-model="activeData['allow-half']" />
          </el-form-item>
          <el-form-item
            v-if="activeData['show-text'] !== undefined"
            label="辅助文字"
          >
            <el-switch
              v-model="activeData['show-text']"
              @change="rateTextChange($event === true)"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['show-score'] !== undefined"
            label="显示分数"
          >
            <el-switch
              v-model="activeData['show-score']"
              @change="rateScoreChange($event === true)"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData['show-stops'] !== undefined"
            label="显示间断点"
          >
            <el-switch v-model="activeData['show-stops']" />
          </el-form-item>
          <el-form-item
            v-if="activeData.range !== undefined"
            label="范围选择"
          >
            <el-switch
              v-model="activeData.range"
              @change="rangeChange($event === true)"
            />
          </el-form-item>
          <el-form-item
            v-if="
              activeData.border !== undefined &&
              activeData.optionType === 'default'
            "
            label="是否带边框"
          >
            <el-switch v-model="activeData.border" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-color-picker'"
            label="颜色格式"
          >
            <el-select
              v-model="activeData['color-format']"
              placeholder="请选择颜色格式"
              :style="{ width: '100%' }"
              @change="colorFormatChange"
            >
              <el-option
                v-for="(item, index) in colorFormatOptions"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="
              activeData.size !== undefined &&
              (activeData.optionType === 'button' ||
                activeData.border ||
                activeData.tag === 'el-color-picker')
            "
            label="选项尺寸"
          >
            <el-radio-group v-model="activeData.size">
              <el-radio-button value="large"> 较大 </el-radio-button>
              <el-radio-button value="default"> 默认 </el-radio-button>
              <el-radio-button value="small"> 较小 </el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="activeData['show-word-limit'] !== undefined"
            label="输入统计"
          >
            <el-switch v-model="activeData['show-word-limit']" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-input-number'"
            label="严格步数"
          >
            <el-switch v-model="activeData['step-strictly']" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-cascader' && activeData.props"
            label="是否多选"
          >
            <el-switch
              v-model="activeData.props.props.multiple"
              @change="multipleChange($event === true)"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-cascader'"
            label="展示全路径"
          >
            <el-switch v-model="activeData['show-all-levels']" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-cascader'"
            label="可否筛选"
          >
            <el-switch v-model="activeData.filterable" />
          </el-form-item>
          <el-form-item
            v-if="activeData.clearable !== undefined"
            label="能否清空"
          >
            <el-switch v-model="activeData.clearable" />
          </el-form-item>
          <el-form-item
            v-if="activeData.showTip !== undefined"
            label="显示提示"
          >
            <el-switch v-model="activeData.showTip" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-upload'"
            label="多选文件"
          >
            <el-switch v-model="activeData.multiple" />
          </el-form-item>
          <el-form-item
            v-if="activeData['auto-upload'] !== undefined"
            label="自动上传"
          >
            <el-switch v-model="activeData['auto-upload']" />
          </el-form-item>
          <el-form-item
            v-if="activeData.readonly !== undefined"
            label="是否只读"
          >
            <el-switch v-model="activeData.readonly" />
          </el-form-item>
          <el-form-item
            v-if="activeData.disabled !== undefined"
            label="是否禁用"
          >
            <el-switch v-model="activeData.disabled" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-select'"
            label="是否可搜索"
          >
            <el-switch v-model="activeData.filterable" />
          </el-form-item>
          <el-form-item
            v-if="activeData.tag === 'el-select'"
            label="是否多选"
          >
            <el-switch
              v-model="activeData.multiple"
              @change="multipleChange($event === true)"
            />
          </el-form-item>
          <el-form-item
            v-if="activeData.required !== undefined"
            label="是否必填"
          >
            <el-switch v-model="activeData.required" />
          </el-form-item>

          <template v-if="activeData.layoutTree">
            <el-divider>布局结构树</el-divider>
            <el-tree
              :data="[activeData]"
              :props="layoutTreeProps"
              node-key="renderKey"
              default-expand-all
              draggable
            >
              <template #default="{ node, data }">
                <span class="node-label">
                  <IconifyIcon
                    class="node-icon inline-block align-[-2px]"
                    :icon="`svg:${data.tagIcon}`"
                    style="margin-right: 5px"
                  />
                  {{ node.label }}
                </span>
              </template>
            </el-tree>
          </template>

          <template v-if="activeData.layout === 'colFormItem'">
            <el-divider>正则校验</el-divider>
            <div
              v-for="(item, index) in activeData.regList"
              :key="index"
              class="reg-item"
            >
              <span
                class="close-btn"
                @click="activeData.regList?.splice(index, 1)"
              >
                <el-icon>
                  <Close />
                </el-icon>
              </span>
              <el-form-item label="表达式">
                <el-input
                  v-model="item.pattern"
                  placeholder="请输入正则"
                />
              </el-form-item>
              <el-form-item
                label="错误提示"
                style="margin-bottom: 0"
              >
                <el-input
                  v-model="item.message"
                  placeholder="请输入错误提示"
                />
              </el-form-item>
            </div>
            <div>
              <el-button
                :icon="CirclePlus"
                style="margin-left: 0; margin-top: 10px"
                type="primary"
                text
                bg
                @click="addReg"
              >
                添加规则
              </el-button>
            </div>
          </template>
        </el-form>
        <!-- 表单属性 -->
        <el-form
          v-show="currentTab === 'form'"
          label-width="90px"
          label-position="top"
        >
          <el-form-item label="表单名">
            <el-input
              v-model="formConf.formRef"
              placeholder="请输入表单名（ref）"
            />
          </el-form-item>
          <el-form-item label="表单模型">
            <el-input
              v-model="formConf.formModel"
              placeholder="请输入数据模型"
            />
          </el-form-item>
          <el-form-item label="校验模型">
            <el-input
              v-model="formConf.formRules"
              placeholder="请输入校验模型"
            />
          </el-form-item>
          <el-form-item label="表单尺寸">
            <el-radio-group v-model="formConf.size">
              <el-radio-button value="large">较大</el-radio-button>
              <el-radio-button value="default">默认</el-radio-button>
              <el-radio-button value="small">较小</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标签对齐">
            <el-radio-group v-model="formConf.labelPosition">
              <el-radio-button value="left">左对齐</el-radio-button>
              <el-radio-button value="right">右对齐</el-radio-button>
              <el-radio-button value="top">顶部对齐</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标签宽度">
            <el-input-number
              v-model="formConf.labelWidth"
              placeholder="标签宽度"
            />
          </el-form-item>
          <el-form-item label="栅格间隔">
            <el-input-number
              v-model="formConf.gutter"
              :min="0"
              placeholder="栅格间隔"
            />
          </el-form-item>
          <el-form-item label="禁用表单">
            <el-switch v-model="formConf.disabled" />
          </el-form-item>
          <el-form-item label="表单按钮">
            <el-switch v-model="formConf.formBtns" />
          </el-form-item>
          <el-form-item label="显示未选中组件边框">
            <el-switch v-model="formConf.unFocusedComponentBorder" />
          </el-form-item>
        </el-form>
      </el-scrollbar>
    </div>
    <icons-dialog
      v-model="iconsVisible"
      :current="currentIcon"
      @select="setIcon"
    />
    <treeNode-dialog
      v-model="dialogVisible"
      @commit="addNode"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElLink } from 'element-plus'
import type { RenderContentFunction, TreeOptionProps } from 'element-plus'
import {
  Link,
  Pointer,
  Remove,
  Close,
  CirclePlus,
  Plus,
  Delete,
  Operation,
} from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import { IconifyIcon } from '@/assets/icons'
import { inputComponents, selectComponents } from '@/utils/generator/config'
import type {
  ComponentConfig,
  DrawingItem,
  FieldOption,
  FieldValue,
  FormConfig,
  OptionValue,
  PaletteItem,
} from '@/utils/generator/types'
import IconsDialog from './IconsDialog.vue'
import TreeNodeDialog from './TreeNodeDialog.vue'

const dateTimeFormat: Record<string, string> = {
  date: 'YYYY-MM-DD',
  week: 'YYYY 第 ww 周',
  month: 'YYYY-MM',
  year: 'YYYY',
  datetime: 'YYYY-MM-DD HH:mm:ss',
  daterange: 'YYYY-MM-DD',
  monthrange: 'YYYY-MM',
  datetimerange: 'YYYY-MM-DD HH:mm:ss',
}

const props = defineProps<{
  showField: boolean
  activeData: DrawingItem | null
  formConf: FormConfig
}>()
const emit = defineEmits<{ 'tag-change': [item: PaletteItem] }>()
// 属性面板与画布共同编辑同一份配置，不替换父组件传入的对象。
const activeData = computed(() => props.activeData)
const formConf = computed(() => props.formConf)
const currentTab = ref<'field' | 'form'>('field')
const currentNode = ref<FieldOption[] | null>(null)
const dialogVisible = ref(false)
const iconsVisible = ref(false)
type IconField = 'prefix-icon' | 'suffix-icon'
const currentIconModel = ref<IconField | null>(null)
const currentIcon = computed(() =>
  props.activeData && currentIconModel.value
    ? props.activeData[currentIconModel.value]
    : undefined,
)

const {
  dateTypeOptions,
  dateRangeTypeOptions,
  colorFormatOptions,
  justifyOptions,
} = {
  dateTypeOptions: [
    {
      label: '日(date)',
      value: 'date',
    },
    {
      label: '周(week)',
      value: 'week',
    },
    {
      label: '月(month)',
      value: 'month',
    },
    {
      label: '年(year)',
      value: 'year',
    },
    {
      label: '日期时间(datetime)',
      value: 'datetime',
    },
  ],
  dateRangeTypeOptions: [
    {
      label: '日期范围(daterange)',
      value: 'daterange',
    },
    {
      label: '月范围(monthrange)',
      value: 'monthrange',
    },
    {
      label: '日期时间范围(datetimerange)',
      value: 'datetimerange',
    },
  ],
  colorFormatOptions: [
    {
      label: 'hex',
      value: 'hex',
    },
    {
      label: 'rgb',
      value: 'rgb',
    },
    {
      label: 'rgba',
      value: 'rgba',
    },
    {
      label: 'hsv',
      value: 'hsv',
    },
    {
      label: 'hsl',
      value: 'hsl',
    },
  ],
  justifyOptions: [
    {
      label: 'start',
      value: 'start',
    },
    {
      label: 'end',
      value: 'end',
    },
    {
      label: 'center',
      value: 'center',
    },
    {
      label: 'space-around',
      value: 'space-around',
    },
    {
      label: 'space-between',
      value: 'space-between',
    },
  ],
}
const layoutTreeProps: TreeOptionProps = {
  label(data: ComponentConfig) {
    return data.componentName || (data.label ?? '') + ': ' + (data.vModel ?? '')
  },
}
const documentLink = computed(
  () =>
    props.activeData?.document ||
    'https://element-plus.org/zh-CN/guide/installation',
)
const dateOptions = computed(() =>
  props.activeData?.['start-placeholder'] === undefined
    ? dateTypeOptions
    : dateRangeTypeOptions,
)
const tagList = [
  { label: '输入型组件', options: inputComponents },
  { label: '选择型组件', options: selectComponents },
]

watch(
  () => props.activeData?.formId,
  () => {
    dialogVisible.value = false
    iconsVisible.value = false
    currentNode.value = null
    currentIconModel.value = null
  },
)

function addReg(): void {
  const field = props.activeData
  if (field) (field.regList ??= []).push({ pattern: '', message: '' })
}
function addSelectItem(): void {
  const field = props.activeData
  if (field) (field.options ??= []).push({ label: '', value: '' })
}
function addTreeItem(): void {
  const field = props.activeData
  if (!field) return
  currentNode.value = field.options ??= []
  dialogVisible.value = true
}
function findOption(
  id: number,
  options: FieldOption[],
): FieldOption | undefined {
  for (const option of options) {
    if (option.id === id) return option
    const child = findOption(id, option.children ?? [])
    if (child) return child
  }
}
const renderContent: RenderContentFunction = (h, { node, data }) => {
  // Element Plus 的树数据是开放对象，在边界处取 ID，再使用本项目的选项类型。
  const id: unknown = data.id
  const option =
    typeof id === 'number'
      ? findOption(id, props.activeData?.options ?? [])
      : undefined
  return h('div', { class: 'custom-tree-node' }, [
    h('span', node.label),
    h(
      'span',
      { class: 'node-operation' },
      option
        ? [
            h(ElLink, {
              type: 'primary',
              icon: Plus,
              underline: false,
              onClick: () => append(option),
            }),
            h(ElLink, {
              type: 'danger',
              icon: Delete,
              underline: false,
              style: 'margin-left: 5px;',
              onClick: () => remove(option, props.activeData?.options ?? []),
            }),
          ]
        : [],
    ),
  ])
}
function append(option: FieldOption): void {
  currentNode.value = option.children ??= []
  dialogVisible.value = true
}
function remove(option: FieldOption, siblings: FieldOption[]): boolean {
  const index = siblings.indexOf(option)
  if (index >= 0) {
    siblings.splice(index, 1)
    return true
  }
  return siblings.some((item) => remove(option, item.children ?? []))
}
function maxOptionId(options: FieldOption[]): number {
  return options.reduce(
    (max, item) =>
      Math.max(max, item.id ?? 0, maxOptionId(item.children ?? [])),
    100,
  )
}
function addNode(option: FieldOption): void {
  if (props.activeData && currentNode.value) {
    currentNode.value.push({
      ...option,
      id: maxOptionId(props.activeData.options ?? []) + 1,
    })
  }
}

function parseValue(value: string): OptionValue {
  if (value === 'true' || value === 'false') return value === 'true'
  return /^-?\d+(\.\d+)?$/.test(value) ? Number(value) : value
}
function setOptionValue(item: FieldOption, value: string): void {
  item.value = parseValue(value)
}
function setDefaultValue(value: FieldValue): string {
  if (Array.isArray(value)) return value.map(setDefaultValue).join(',')
  return value == null ? '' : String(value)
}
function onDefaultValueInput(value: string): void {
  const field = props.activeData
  if (!field) return
  if (field.tag === 'el-input') {
    field.defaultValue = value
    return
  }
  field.defaultValue = Array.isArray(field.defaultValue)
    ? value === ''
      ? []
      : value.split(',').map(parseValue)
    : parseValue(value)
}
function onSwitchValueInput(
  value: string,
  name: 'active-value' | 'inactive-value',
): void {
  const field = props.activeData
  if (field) field[name] = parseValue(value)
}
function setTimeValue(value: string, type?: string): void {
  const field = props.activeData
  if (!field) return
  field.defaultValue = null
  field['value-format'] = type === 'week' ? dateTimeFormat.date : value
  field.format = value
}
function spanChange(value: number | number[]): void {
  if (typeof value === 'number') formConf.value.span = value
}
function multipleChange(value: boolean): void {
  const field = props.activeData
  if (field) field.defaultValue = value ? [] : ''
}
function dateTypeChange(value: string): void {
  const format = dateTimeFormat[value]
  if (format) setTimeValue(format, value)
}
function rangeChange(value: boolean): void {
  const field = props.activeData
  if (field)
    field.defaultValue = value
      ? [field.min ?? 0, field.max ?? 100]
      : (field.min ?? 0)
}
function rateTextChange(value: boolean): void {
  const field = props.activeData
  if (value && field) field['show-score'] = false
}
function rateScoreChange(value: boolean): void {
  const field = props.activeData
  if (value && field) field['show-text'] = false
}
function colorFormatChange(value: string): void {
  const field = props.activeData
  if (!field) return
  field.defaultValue = null
  field['show-alpha'] = value === 'rgba'
  field.renderKey = Date.now()
}
function openIconsDialog(model: IconField): void {
  currentIconModel.value = model
  iconsVisible.value = true
}
function setIcon(value: string): void {
  const field = props.activeData
  if (field && currentIconModel.value) field[currentIconModel.value] = value
}
function tagChange(tagIcon: string): void {
  const target = [...inputComponents, ...selectComponents].find(
    (item) => item.tagIcon === tagIcon,
  )
  if (target) emit('tag-change', target)
}
</script>

<style scoped>
.right-board {
  width: 350px;
  position: absolute;
  right: 0;
  top: 0;
  padding-top: 3px;
}
.right-board:deep(.el-tabs__header) {
  margin: 0;
}
.right-board:deep(.el-input-group__append .el-button) {
  display: inline-flex;
}
.right-board .field-box {
  position: relative;
  height: calc(100vh - 50px - 40px - 42px);
  box-sizing: border-box;
  overflow: hidden;
}
.right-board .el-scrollbar {
  height: 100%;
}
.right-board .el-scrollbar:deep(.el-scrollbar__view) {
  padding: 30px 20px;
}

.reg-item {
  padding: 12px 6px;
  background: var(--el-border-color-extra-light);
  position: relative;
  border-radius: 4px;
}
.reg-item .close-btn {
  position: absolute;
  right: -6px;
  top: -6px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  line-height: 16px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 50%;
  color: #fff;
  z-index: 1;
  cursor: pointer;
  font-size: 12px;
}

.select-item {
  display: flex;
  border: 1px dashed transparent;
  box-sizing: border-box;
}
.select-item .close-btn {
  cursor: pointer;
  color: var(--el-color-danger);
}
.select-item .el-input + .el-input {
  margin-left: 4px;
}

.select-item + .select-item {
  margin-top: 4px;
}

.select-item.sortable-chosen {
  border: 1px dashed var(--el-color-primary);
}

.select-line-icon {
  line-height: 32px;
  font-size: 22px;
  padding: 0 4px;
  color: var(--el-text-color-secondary);
}

.option-drag {
  cursor: move;
}

.time-range .el-date-editor {
  width: 227px;
}
.time-range :deep(.el-icon-time) {
  display: none;
}

.document-link {
  position: absolute;
  display: flex;
  width: 26px;
  height: 26px;
  top: 0;
  left: 0;
  cursor: pointer;
  background: var(--el-color-primary);
  z-index: 1;
  border-radius: 0 0 6px 0;
  justify-content: center;
  align-items: center;
  color: #fff;
  font-size: 18px;
}

.node-label {
  font-size: 14px;
}

.node-icon {
  color: var(--el-text-color-secondary);
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
