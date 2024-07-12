<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="单据编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code" disabled></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billsDate">
              <j-date  v-model="model.billsDate" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="产品名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="prodId">
              <j-select-prod v-model="model.prodId"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="BOM名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bomName">
              <a-input v-model="model.bomName" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="默认" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isDefault">
              <j-switch v-model="model.isDefault"></j-switch>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark" ></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="子件明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="bomSubroutineTable.loading"
          :columns="bomSubroutineTable.columns"
          :dataSource="bomSubroutineTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
      <a-tab-pane tab="工艺线路" :key="refKeys[1]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[1]"
          :loading="bomRoutingTable.loading"
          :columns="bomRoutingTable.columns"
          :dataSource="bomRoutingTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true">
        </j-editable-table>
      </a-tab-pane>
      <a-tab-pane tab="其他信息" :key="refKeys[2]" :forceRender="true">
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
            <a-input v-model="model.createBy" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="创建日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
            <j-date v-model="model.createTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss"
                    style="width: 100%" disabled/>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="更新人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateBy">
            <a-input v-model="model.updateBy" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="更新日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateTime">
            <j-date v-model="model.updateTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss"
                    style="width: 100%" disabled/>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectProd from '@/components/jeecgbiz/JSelectProd'
import {putAction} from '@api/manage'

import {validateDuplicateValue} from '@/utils/util'

export default {
  name: 'BomForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectProd
  },
  data() {
    return {
      labelCol: {
        xs: {span: 24},
        sm: {span: 5},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16},
      },
      model: {
      },
      validatorRules: {
        // code: [
        //    { required: true, message: '请输入单据编码!'},
        // ],
        billsDate: [
          {required: true, message: '请输入单据日期!'},
        ],
        prodCode: [
          {required: true, message: '请输入产品名称!'},
        ],
        bomName: [
          {required: true, message: '请输入BOM名称!'},
        ],
      },
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      refKeys: ['bomSubroutine','bomRouting', 'others'],
      tableKeys: ['bomSubroutine','bomRouting'],
      activeKey: 'bomSubroutine',
      // 子件明细
      bomSubroutineTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '物料编码',
            key: 'code',
            type: FormTypes.sel_material,
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '物料名称',
            key: 'materialName',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '规格型号',
            key: 'size',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '计量单位',
            key: 'unit',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '件装量',
            key: 'unitLoad',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '分子用量',
            key: 'numerator',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '分母用量',
            key: 'denominator',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '提前天数',
            key: 'advenceDays',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '产出品',
            key: 'outputLevel',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '足额配料',
            key: 'fullIngredients',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },


          {
            title: 'material_name',
            key: 'material_name',
            type: "hidden"
          },
          {
            title: 'unit_load',
            key: 'unit_load',
            type: "hidden"
          },
        ]
      },
      bomRoutingTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '工序编码',
            key: 'craftCode',
            type: FormTypes.sel_craft,
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '工序名称',
            key: 'craftName',
            type: FormTypes.sel_craft,
            disabled: true,
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '加工单位',
            key: 'craftUnit',
            type: FormTypes.sel_craft,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '加工工时',
            key: 'craftTime',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '工时单价',
            key: 'craftCost',
            type: FormTypes.sel_craft,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '加工单价',
            key: 'processCost',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '料废单价',
            key: 'wastePrice',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '工费单价',
            key: 'wagePrice',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '顺序加工',
            key: 'isOrder',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '委托加工',
            key: 'isConsign',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '序号',
            key: 'serialNumber',
            type: FormTypes.input,
            width: "200px",
            validateRules: [{ // 自定义函数校验 handler
              handler(type, value, row, column, callback, target) {
                if (type === 'blur' || type === 'getValues') {
                  let { values } = target.getValuesSync({ validate: false })
                  let count = 0
                  for (let val of values) {
                    if (val['serialNumber'] === value) {
                      if (++count >= 2) {
                        callback(false, '${title}不能重复')
                        return
                      }
                    }
                  }
                  count = 0;
                  for (let val of values) {
                    if (val['serialNumber'] === '1') {
                      count++;
                    }
                  }
                  if (count <= 0) {
                    callback(false, '${title}必须从1开始')
                    return
                  }
                  callback(true) // true = 通过验证
                } else {
                  callback(); // 不填写或者填写 null 代表不进行任何操作
                }
              },
              message: '${title}默认提示'
            },{required: true, message: '${title}不能为空'}],
          },
        ]
      },
      url: {
        add: "/initial/bom/add",
        edit: "/initial/bom/edit",
        bomSubroutine: {
          list: '/initial/bom/queryBomSubroutineByMainId'
        },
        bomRouting: {
          list: '/initial/bom/queryBomRoutingByBomId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/bom_num'
        },
      }
    }
  },
  props: {
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  computed: {
    formDisabled() {
      return this.disabled
    },
  },
  created() {
  },
  methods: {
    getOrderNum() {
      putAction(this.url.rule.orderNum, this.model).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          this.model.code = res.result
          this.model.billsDate = this.getDate();
        }
      })
    },
    addBefore() {
      this.bomSubroutineTable.dataSource = []
      this.bomRoutingTable.dataSource = []
      this.getOrderNum()
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      this.$nextTick(() => {
      })
      // 加载子表数据
      if (this.model.id) {
        let params = {id: this.model.id}
        this.requestSubTableData(this.url.bomSubroutine.list, params, this.bomSubroutineTable)
        this.requestSubTableData(this.url.bomRouting.list, params, this.bomRoutingTable)
      }
    },
    //校验所有一对一子表表单
    validateSubForm(allValues) {
      return new Promise((resolve, reject) => {
        Promise.all([]).then(() => {
          resolve(allValues)
        }).catch(e => {
          if (e.error === VALIDATE_NO_PASSED) {
            // 如果有未通过表单验证的子表，就自动跳转到它所在的tab
            this.activeKey = e.index == null ? this.activeKey : this.refKeys[e.index]
          } else {
            console.error(e)
          }
        })
      })
    },
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      let main = Object.assign(this.model, allValues.formValue)

      return {
        ...main, // 展开
        bomSubroutineList: allValues.tablesValue[0].values,
        bomRoutingList: allValues.tablesValue[1].values,
      }
    },
    validateError(msg) {
      this.$message.error(msg)
    },
    close() {
      this.visible = false
      this.$emit('close')
      this.$refs.form.clearValidate();
    },
    popupCallback(value, row) {
      this.model = Object.assign(this.model, row);
    },

  }
}
</script>

<style scoped>
</style>