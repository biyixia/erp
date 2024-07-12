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
              <j-date disabled v-model="model.billDate" style="width: 100%"/>
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
            <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="remark">
              <a-input v-model="model.remark"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey">
      <a-tab-pane tab="付款明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="payDetailTable.loading"
          :columns="payDetailTable.columns"
          :dataSource="payDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :add-button="false"
          :actionButton="true">
          <template slot="buttonAfter">
            <a-button type="primary" @click="getReferPiData" :disabled="formDisabled">参照采购发票</a-button>
            <span data-v-2f0c1ef1="" class="gap"></span>
            <a-button type="primary" @click="getReferPoData" :disabled="formDisabled">参照采购订单</a-button>
          </template>
        </j-editable-table>
      </a-tab-pane>
      <a-tab-pane tab="其他信息" :key="refKeys[1]" :forceRender="true">
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
    <j-select-po-pay-modal @ok="selectOK" ref="selectPo"/>
    <j-select-p-i-modal @ok="selectOK" ref="selectPi"/>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectPoPayModal from '@/components/jeecgbiz/modal/JSelectPoPayModal'
import JSelectPIModal from '@/components/jeecgbiz/modal/JSelectPIModal'
import {validateDuplicateValue} from '@/utils/util'
import {getAction, putAction} from "@api/manage";

export default {
  name: 'paymentRequestForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectPoPayModal,
    JSelectPIModal
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
      model: {},
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
      refKeys: ['payDetail', 'other'],
      tableKeys: ['payDetail',],
      activeKey: 'payDetail',
      // 标的明细
      payDetailTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '客商名称',
            key: 'clientId',
            disabled:true,
            type: FormTypes.sel_merchant,
            width:"200px",
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '付款类型',
            key: 'payType',
            type: FormTypes.select,
            dictCode:"payment_type",
            width:"200px",
            disabled:true,
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '申请金额',
            key: 'applyAmount',
            type: FormTypes.input,
            width:"200px",
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '预计付款日期',
            key: 'expectedPayDate',
            type: FormTypes.date,
            width:"200px",
            defaultValue:'',
          },
          {
            title: '单据应付金额',
            key: 'payAmount',
            type: FormTypes.input,
            disabled:true,
            width:"200px",
            defaultValue:'',
            validateRules: [{ required: true, message: '${title}不能为空' }],
          },
          {
            title: '备注',
            key: 'remark',
            type: FormTypes.input,
            width:"200px",
            defaultValue:'',
          },
          {
            title: '来源',
            key: 'source',
            type: FormTypes.input,
            disabled:true,
            width:"200px",
            defaultValue:'',
          },
          {
            title: '来源单号',
            key: 'sourceNumber',
            type: FormTypes.input,
            disabled:true,
            width:"200px",
            defaultValue:'',
          },
          {
            key: 'referId',
            width: "0px",
          },
        ]
      },
      url: {
        add: "/bill/bill/add/pp",
        edit: "/bill/bill/edit",
        payDetail: {
          list: '/bill/bill/queryPayDetailByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/purchase_pay_request'
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
    selectOK(selectPoRows) {
      for (let row of selectPoRows) {
        let rowKey = this.$refs.payDetail.add();
        let setValueItem = {rowKey, values: {}}
        Object.keys(row).forEach(key => {
          if (typeof row[key] === "object" && row[key] != null) {
            Object.keys(row[key]).forEach(key1 => {
              if (row[key][key1] != null) {
                setValueItem.values[key1] = row[key][key1].toString();
              }
            });
          } else if (row[key] != null) {
            setValueItem.values[key] = row[key].toString();
            //部分数据不导入
            if (key === 'number' || key === 'looseItem' || key === 'count' || key === 'taxIncludedAmount' || key === 'amount' || key === 'tax') {
              setValueItem.values[key] = ''
            }
          } else {
            setValueItem.values[key] = ''
          }
        })
        if (Object.keys(setValueItem).length > 0) {
          this.$refs.payDetail.setValues([setValueItem])
        }
      }
    },
    getReferPiData() {
      this.$refs.selectPi.showModal()
    },
    getReferPoData() {
      this.$refs.selectPo.showModal()
    },
    getOrderNum() {
      putAction(this.url.rule.orderNum, this.model).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          this.model.code = res.result
          var date = new Date();
          // 获取当前月份
          var nowMonth = date.getMonth() + 1;
          // 获取当前是几号
          var strDate = date.getDate();
          // 添加分隔符“-”
          var seperator = "-";
          // 对月份进行处理，1-9月在前面添加一个“0”
          if (nowMonth >= 1 && nowMonth <= 9) {
            nowMonth = "0" + nowMonth;
          }
          // 对月份进行处理，1-9号在前面添加一个“0”
          if (strDate >= 0 && strDate <= 9) {
            strDate = "0" + strDate;
          }
          // 最后拼接字符串，得到一个格式为(yyyy-MM-dd)的日期
          var nowDate = date.getFullYear() + seperator + nowMonth + seperator + strDate;
          this.model.billDate = nowDate
        }
      })
    },
    addBefore() {
      this.payDetailTable.dataSource = []
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
        this.requestSubTableData(this.url.payDetail.list, params, this.payDetailTable)
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
        payDetailList: allValues.tablesValue[0].values
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