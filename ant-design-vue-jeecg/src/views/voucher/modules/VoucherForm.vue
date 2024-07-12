<template>
   <a-spin :spinning="confirmLoading">
     <j-form-container :disabled="formDisabled">
       <!-- 主表单区域 -->
       <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
         <a-row>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="凭证类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="voucherType">
              <a-input v-model="model.voucherType" placeholder="请输入凭证类型" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="凭证日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="voucherDate">
              <j-date placeholder="请选择凭证日期" v-model="model.voucherDate" style="width: 100%" />
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="凭证号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="voucherNumber">
              <a-input v-model="model.voucherNumber" placeholder="请输入凭证号" ></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="附件数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="attachNumber">
              <a-input-number v-model="model.attachNumber" placeholder="请输入附件数量" style="width: 100%" />
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
            :actionButton="true">
            <template slot="buttonAfter">
              <a-col>
                <a-button type="primary" @click="abc">增加分录</a-button>
              </a-col>
            </template>
          </j-editable-table>
        </a-tab-pane>
      </a-tabs>
    </a-spin>
</template>

<script>

  import { FormTypes,getRefPromise,VALIDATE_NO_PASSED } from '@/utils/JEditableTableUtil'
  import { JEditableTableModelMixin } from '@/mixins/JEditableTableModelMixin'
  import { validateDuplicateValue } from '@/utils/util'

  export default {
    name: 'VoucherForm',
    mixins: [JEditableTableModelMixin],
    components: {
    },
    data() {
      return {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        model:{
            voucherType:"记账凭证",
        },
        validatorRules: {
           voucherType: [
              { required: true, message: '请输入凭证类型!'},
           ],
           voucherDate: [
              { required: true, message: '请输入凭证日期!'},
           ],
           voucherNumber: [
              { required: true, message: '请输入凭证号!'},
           ],
        },
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        refKeys: ['entryDetail', ],
        tableKeys:['entryDetail', ],
        activeKey: 'entryDetail',
        // 分录明细
        entryDetailTable: {
          loading: false,
          dataSource: [],
          columns: [
            {
              title: '摘要',
              key: 'abstract',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '科目编码',
              key: 'subCode',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '科目名称',
              key: 'subName',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '借方金额',
              key: 'debitMoney',
              type: FormTypes.select,
              dictCode:"debit_credit_money",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '贷方金额',
              key: 'creditMoney',
              type: FormTypes.select,
              dictCode:"debit_credit_money",
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '账户',
              key: 'isAccount',
              type: FormTypes.checkbox,
              customValue: ['Y', 'N'],
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '仓库',
              key: 'isWarehouse',
              type: FormTypes.checkbox,
              customValue: ['Y', 'N'],
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '费用',
              key: 'isCost',
              type: FormTypes.checkbox,
              customValue: ['Y', 'N'],
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '明细',
              key: 'isDetail',
              type: FormTypes.checkbox,
              customValue: ['Y', 'N'],
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
            {
              title: '现金流量名称',
              key: 'cashFlowName',
              type: FormTypes.input,
              width:"200px",
              placeholder: '请输入${title}',
              defaultValue:'',
            },
          ]
        },
        url: {
          add: "/voucher/voucher/add",
          edit: "/voucher/voucher/edit",
          entryDetail: {
            list: '/voucher/voucher/queryEntryDetailByMainId'
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
      formDisabled(){
        return this.disabled
      },
    },
    created () {
    },
    methods: {
      abc(){
        this.$router.push({ path: '/initial/AccountingSubjectList', query: {}});
      },
     addBefore(){
            this.entryDetailTable.dataSource=[]
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
          let params = { id: this.model.id }
          this.requestSubTableData(this.url.entryDetail.list, params, this.entryDetailTable)
        }
      },
      //校验所有一对一子表表单
    validateSubForm(allValues){
        return new Promise((resolve,reject)=>{
          Promise.all([
          ]).then(() => {
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
      validateError(msg){
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