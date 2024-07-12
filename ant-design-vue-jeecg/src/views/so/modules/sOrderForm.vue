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
            <a-form-model-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billDate">
              <j-date v-model="model.billDate" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="人员名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="personName">
              <j-select-user-by-dep v-model="model.personName" :multi="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="部门名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="departmentName">
              <j-select-depart v-model="model.departmentName" :multi="true"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="客户名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="clientId">
              <j-select-customer v-model="model.clientId" :multi="false"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="收款政策" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="paymentStyle">
              <j-dict-select-tag type="list" v-model="model.paymentStyle" dictCode="payment_style"
                                 @change="changeDisable"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="标的明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="billingDetailTable.loading"
          :columns="billingDetailTable.columns"
          :dataSource="billingDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          @blurInputValues="updateTaxAmount"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true">
        </j-editable-table>
      </a-tab-pane>
      <a-tab-pane tab="收款计划" :key="refKeys[1]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[1]"
          :loading="collectPayPlanTable.loading"
          :columns="collectPayPlanTable.columns"
          :dataSource="collectPayPlanTable.dataSource"
          :maxHeight="300"
          :disabled="isDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"/>
      </a-tab-pane>
      <a-tab-pane tab="其他信息" :key="refKeys[2]" :forceRender="true">
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="创建人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
            <a-input v-model="model.createBy" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="创建日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
            <j-date v-model="model.createTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"
                    disabled/>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="更新人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateBy">
            <a-input v-model="model.updateBy" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="更新日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateTime">
            <j-date v-model="model.updateTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"
                    disabled/>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
    </a-tabs>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectCustomer from '@/components/jeecgbiz/JSelectCustomer'
import {validateDuplicateValue} from '@/utils/util'
import {putAction} from "@api/manage";

export default {
  name: 'sOrderForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectCustomer
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
      isDisabled: '',
      hasPaymentStyle: '',
      completeDetail: '',
      model: {
        paymentStyle: ''
      },
      validatorRules: {
        billDate: [
          {required: true, message: '请输入单据日期!'},
        ],
        personName: [
          {required: true, message: '请输入人员名称!'},
        ],
        departmentName: [
          {required: true, message: '请输入部门名称!'},
        ],
      },
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      refKeys: ['billingDetail', 'collectPayPlan', 'other'],
      tableKeys: ['billingDetail', 'collectPayPlan',],
      activeKey: 'billingDetail',
      // 标的明细
      billingDetailTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '物料编码',
            key: 'materialCode',
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
            title: '件数',
            key: 'number',
            type: FormTypes.inputNumber,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '散件',
            key: 'looseItem',
            type: FormTypes.inputNumber,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '数量',
            key: 'count',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '含税单价',
            key: 'priceIncludedTax',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '含税金额',
            key: 'taxIncludedAmount',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            statistics: ['sum'],
            defaultValue: '',
          },
          {
            title: '税率',
            key: 'rate',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '无税单价',
            key: 'price',
            type: FormTypes.sel_material,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '无税金额',
            key: 'amount',
            type: FormTypes.inputNumber,
            width: "200px",
            disabled: true,
            statistics: ['sum'],
            defaultValue: '',
          },
          {
            title: '税额',
            key: 'tax',
            type: FormTypes.inputNumber,
            disabled: true,
            width: "200px",
            statistics: ['sum'],
            defaultValue: '',
          },
          {
            title: '预计发货日期',
            key: 'expectedDelivery',
            type: FormTypes.date,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '来源',
            key: 'source',
            type: FormTypes.input,
            disabled: true,
            width: "200px",
            defaultValue: '手工录入',
          },
          {
            title: '来源单号',
            key: 'sourceNumber',
            type: FormTypes.input,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
        ]
      },
      // 收款计划
      collectPayPlanTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '收款类型',
            key: 'payType',
            type: FormTypes.select,
            dictCode: "payment_type",
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '收款比例(%)',
            key: 'payRatio',
            type: FormTypes.inputNumber,
            width: "200px",
            statistics: ['sum'],
            validateRules: [{ // 自定义函数校验 handler
              handler(type, value, row, column, callback, target) {
                if (value > 100 || value < 0) {
                  callback(false, '${title}必须在0-100的范围内')
                  return
                }
                if (type === 'blur' || type === 'getValues') {
                  let payRatio = (Number)(target.getStatisticsValue('payRatio'))
                  if (payRatio !== 100) {
                    callback(false, '${title}之和必须等于100')
                    return
                  }
                  callback(true);
                } else {
                  callback();
                }
              },
              message: '${title}默认提示'
            }],
            defaultValue: '',
          },
          {
            title: '预计收款日期',
            key: 'expectedPayDate',
            type: FormTypes.date,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '收款金额',
            key: 'payAmount',
            type: FormTypes.inputNumber,
            width: "200px",
            statistics: ['sum'],
            disabled: true,
            defaultValue: '',
          },
          {
            title: '备注',
            key: 'remark',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
        ]
      },
      url: {
        add: "/bill/bill/add/so",
        edit: "/bill/bill/edit",
        billingDetail: {
          list: '/bill/bill/queryBillingDetailByMainId'
        },
        collectPayPlan: {
          list: '/bill/bill/queryCollectPayPlanByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/sell_order_num'
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
    updateTaxAmount(inputValues) {
      let taxIncludedAmount = this.$refs.billingDetail.getStatisticsValue('taxIncludedAmount');
      let {values} = this.$refs.collectPayPlan.getValuesSync({validate: false})
      for (let i = 0; i < values.length; i++) {
        if (values[i]['payRatio']) {
          let rowKey = values[i]['id']
          let setValueItem = {rowKey, values: {}}
          setValueItem.values['payAmount'] = values[i]['payRatio'] / 100 * taxIncludedAmount
          this.$refs.collectPayPlan.setValues([setValueItem])
        }
      }
    },
    getOrderNum() {
      putAction(this.url.rule.orderNum, this.model).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          this.model.code = res.result
          this.model.billDate = this.getDate();
        }
      })
    },
    handleChangeTabs(key) {
      if (key === 'collectPayPlan') {
        this.isDisabled = !(this.hasPaymentStyle && this.completeDetail)
        let taxIncludedAmount = (Number)(this.$refs.billingDetail.getStatisticsValue('taxIncludedAmount'))
        if (key === 'collectPayPlan' || taxIncludedAmount === null) {
          if (taxIncludedAmount === 0) {
            this.completeDetail = false;
            this.$message.warning("请先填写标的明细");
            return
          } else {
            this.completeDetail = true;
          }
          if (!this.hasPaymentStyle) {
            this.$message.warning("仅按条款付款需要使用付款计划");
            return;
          }
          this.isDisabled = !(this.hasPaymentStyle && this.completeDetail)
          this.$refs.collectPayPlan.taxIncludedAmount = taxIncludedAmount
        }
        // 自动重置scrollTop状态，防止出现白屏
        getRefPromise(this, key).then(editableTable => {
          editableTable.resetScrollTop()
        });
      }
    },
    changeDisable() {
      this.$refs.collectPayPlan.initialize()
      this.hasPaymentStyle = this.model.paymentStyle === '按条款'
      this.isDisabled = !(this.hasPaymentStyle && this.completeDetail)
      this.$refs.collectPayPlan.taxIncludedAmount = this.$refs.billingDetail.getStatisticsValue('taxIncludedAmount')
    }
    ,
    addBefore() {
      this.billingDetailTable.dataSource = []
      this.collectPayPlanTable.dataSource = []
      this.getOrderNum()
    }
    ,
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    }
    ,
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      this.$nextTick(() => {
      })
      // 加载子表数据
      if (this.model.id) {
        let params = {id: this.model.id}
        this.requestSubTableData(this.url.billingDetail.list, params, this.billingDetailTable)
        this.requestSubTableData(this.url.collectPayPlan.list, params, this.collectPayPlanTable)
      }
      this.hasPaymentStyle = this.model.paymentStyle === '按条款'

    }
    ,
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
    }
    ,
    /** 整理成formData */
    classifyIntoFormData(allValues) {
      let main = Object.assign(this.model, allValues.formValue)

      return {
        ...main, // 展开
        billingDetailList: allValues.tablesValue[0].values,
        collectPayPlanList: this.model.paymentStyle === '按条款' ? allValues.tablesValue[1].values : null,
      }
    }
    ,
    validateError(msg) {
      this.$message.error(msg)
    }
    ,
    close() {
      this.visible = false
      this.$emit('close')
      this.$refs.form.clearValidate();
    }
    ,
    popupCallback(value, row) {
      this.model = Object.assign(this.model, row);
    }
    ,

  }
}
</script>

<style scoped>
</style>