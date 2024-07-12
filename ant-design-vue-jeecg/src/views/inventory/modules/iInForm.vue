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
            <a-form-model-item label="调入仓库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="inWarehouseId">
              <j-select-warehouse ownership="company" v-model="model.inWarehouseId" :multi="false"/>
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
      <a-tab-pane tab="入库明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="billingDetailTable.loading"
          :columns="billingDetailTable.columns"
          :dataSource="billingDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true">
          <template slot="buttonAfter">
            <a-col>
              <a-button type="primary" @click="getReferITData" :disabled="formDisabled">参照库存调拨单</a-button>
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
    <j-select-i-t-modal :in-warehouse-id="model.inWarehouseId" @ok="selectOK" ref="selectIT"/>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectWarehouse from '@/components/jeecgbiz/JSelectWarehouse'
import {validateDuplicateValue} from '@/utils/util'
import {putAction} from "@api/manage";
import JSelectITModal from '@/components/jeecgbiz/modal/JSelectITModal'


export default {
  name: 'iInForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectWarehouse,
    JSelectITModal
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
      refKeys: ['billingDetail', 'other'],
      tableKeys: ['billingDetail'],
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
          {
            key: 'priceIncludedTax',
            type: 'hidden',
          },
          {
            key: 'taxIncludedAmount',
            type: 'hidden',
          },
          {
            key: 'referId',
            type: 'hidden',
          },
        ]
      },
      url: {
        add: "/bill/bill/add/ii",
        edit: "/bill/bill/edit",
        billingDetail: {
          list: '/bill/bill/queryBillingDetailByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/inventory_in_num'
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
        if (row['sumOutBound'] <= 0) {
          this.$message.warning("请先填写调拨出库单！");
          return;
        }
        let rowKey = this.$refs.billingDetail.add();
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
            if (key === 'number' || key === 'looseItem' || key === 'count') {
              setValueItem.values[key] = ''
            }
          } else {
            setValueItem.values[key] = '';
          }
        })
        if (Object.keys(setValueItem).length > 0) {
          this.$refs.billingDetail.setValues([setValueItem])
        }
      }
    },
    getReferITData() {
      if (this.model.inWarehouseId === undefined) {
        this.$message.warning("请先选择调入仓库！");
        return;
      }
      this.$refs.selectIT.queryParam.total = true;
      this.$refs.selectIT.showModal()
    },
    getOrderNum() {
      putAction(this.url.rule.orderNum, this.model).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          this.model.code = res.result
          this.model.billDate = this.getDate()
        }
      })
    },
    handleChangeTabs(key) {
      // 自动重置scrollTop状态，防止出现白屏
      getRefPromise(this, key).then(editableTable => {
        editableTable.resetScrollTop()
      });
    },
    addBefore() {
      this.billingDetailTable.dataSource = []
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
        this.requestSubTableData(this.url.billingDetail.list, params, this.billingDetailTable)
      }
      this.hasPaymentStyle = this.model.paymentStyle === '按条款'
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
        billingDetailList: allValues.tablesValue[0].values,
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