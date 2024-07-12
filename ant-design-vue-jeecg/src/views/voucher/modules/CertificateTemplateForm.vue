<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="单据编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code" placeholder="请输入单据编码" disabled></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billDate">
              <j-date placeholder="请选择单据日期" v-model="model.billDate" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="凭证类别" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="voucherType">
              <j-dict-select-tag type="list" v-model="model.voucherType" dictCode="voucher_type" placeholder="请选择凭证类别"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="单据类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="billType">
              <j-dict-select-tag type="list" v-model="model.billType" dictCode="bill_type" placeholder="请选择单据类型"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="分录明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="entryDetailTable.loading"
          :columns="entryDetailTable.columns"
          :dataSource="entryDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          :add-button="false"
        >
          <template slot="buttonAfter">
            <a-col>
              <a-button type="primary" @click="referAccountingSub">增加分录</a-button>
            </a-col>
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
    <j-select-account-sub-modal @ok="selectOK" ref="selectPro"/>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectAccountSubModal from '@/components/jeecgbiz/modal/JSelectAccountSubModal'
import {validateDuplicateValue} from '@/utils/util'
import {putAction} from "@api/manage";

export default {
  name: 'CertificateTemplateForm',
  mixins: [JEditableTableModelMixin],
  components: {JSelectAccountSubModal},
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
      model: {},
      validatorRules: {
        voucherType: [
          {required: true, message: '请输入凭证类别!'},
        ],
        billType: [
          {required: true, message: '请输入单据类型!'},
        ],
      },
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      refKeys: ['entryDetail','others'],
      tableKeys: ['entryDetail',],
      activeKey: 'entryDetail',
      // 分录明细
      entryDetailTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '摘要',
            key: 'digest',
            type: FormTypes.input,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '科目编码',
            key: 'subCode',
            disabled: true,
            type: FormTypes.input,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '科目名称',
            key: 'subName',
            disabled: true,
            type: FormTypes.input,
            width: "200px",
            placeholder: '请输入${title}',
            defaultValue: '',
          },
          {
            title: '借方金额',
            key: 'debitMoney',
            type: FormTypes.select,
            dictCode: "debit_credit_money",
            width: "200px",
            defaultValue: '',
          },
          {
            title: '贷方金额',
            key: 'creditMoney',
            type: FormTypes.select,
            dictCode: "debit_credit_money",
            width: "200px",
            defaultValue: '',
          },
          {
            title: '账户',
            key: 'isAccount',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '仓库',
            key: 'isWarehouse',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '费用',
            key: 'isCost',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '明细',
            key: 'isDetail',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '现金流量名称',
            key: 'cashFlowName',
            disabled: true,
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
        ]
      },
      url: {
        add: "/voucher/certificateTemplate/add",
        edit: "/voucher/certificateTemplate/edit",
        entryDetail: {
          list: '/voucher/certificateTemplate/queryEntryDetailByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/certificate_template_num'
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
        let rowKey = this.$refs.entryDetail.add();
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
          } else {
            setValueItem.values[key] = ''
          }
        })
        if (Object.keys(setValueItem).length > 0) {
          this.$refs.entryDetail.setValues([setValueItem])
        }
      }
    },
    referAccountingSub(){
      this.$refs.selectPro.showModal()
    },
    getOrderNum() {
      putAction(this.url.rule.orderNum, this.model).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          this.model.code = res.result
        }
      })
    },
    addBefore() {
      this.getOrderNum();
      this.entryDetailTable.dataSource = []
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
        this.requestSubTableData(this.url.entryDetail.list, params, this.entryDetailTable)
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
        entryDetailList: allValues.tablesValue[0].values,
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

  }
}
</script>

<style scoped>
</style>