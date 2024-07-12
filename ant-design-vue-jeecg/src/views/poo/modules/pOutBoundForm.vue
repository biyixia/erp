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
            <a-form-model-item label="供应商名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="clientId">
              <j-select-merchant v-model="model.clientId" :multi="false"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="调入仓库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="inWarehouseId">
              <j-select-warehouse ownership="provider" v-model="model.inWarehouseId" :multi="false"/>
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
              <a-button type="primary" @click="getReferPoData" :disabled="formDisabled">参照采购订单</a-button>
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
    <j-select-po-modal :client-id="model.clientId" @ok="selectOK" ref="selectPo"/>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectPoModal from '@/components/jeecgbiz/modal/JSelectPoModal'
import JSelectMerchant from '@/components/jeecgbiz/JSelectMerchant'
import JSelectWarehouse from '@/components/jeecgbiz/JSelectWarehouse'
import {validateDuplicateValue} from '@/utils/util'
import {getAction, putAction} from "@api/manage";

export default {
  name: 'pOutBoundForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectPoModal,
    JSelectMerchant,
    JSelectWarehouse
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
      refKeys: ['billingDetail', 'other'],
      tableKeys: ['billingDetail',],
      activeKey: 'billingDetail',
      // 入库明细
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
            validateRules: [{ // 自定义函数校验 handler
              handler(type, value, row, column, callback, target) {
                if (type === 'change' || type === 'getValues') {
                  if (typeof value === 'string') {
                    let {values} = target.getValuesSync({validate: false, rowIds: [row.id]})
                    let params = {}
                    params.code = values[0].materialCode;
                    if (values[0].standardDosage === '') {
                      getAction('/initial/material/list', params).then(res => {
                        if (res.success) {
                          if (res.result.records[0].isInContract === 'Y') {
                            callback(false, '该物料只能根据采购订单录入')
                            return
                          }
                          callback(true);
                        }
                      })
                    }
                    callback(true);
                  }
                  callback(true);
                } else {
                  callback(); // 不填写或者填写 null 代表不进行任何操作
                }
              },
              message: '${title}默认提示'
            }, {required: true, message: '${title}不能为空'}],
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
            validateRules: [{ // 自定义函数校验 handler
              handler(type, value, row, column, callback, target) {
                if (type === 'change' || type === 'getValues') {
                  let {values} = target.getValuesSync({validate: false, rowIds: [row.id]})
                  let params = {}
                  params.code = values[0].materialCode;
                  if (values[0].standardDosage !== '') {
                    getAction('/initial/material/list', params).then(res => {
                      if (res.success) {
                        if (res.result.records[0].isBuyorderIn === 'N' && values[0].count > values[0].standardDosage) {
                          callback(false, '该物料不允许超过采购订单的采购数量入库')
                          return
                        }
                        callback(true);
                      }
                    })
                  }
                  callback(true);
                } else {
                  callback(); // 不填写或者填写 null 代表不进行任何操作
                }
              },
              message: '${title}默认提示'
            }, {required: true, message: '${title}不能为空'}],
          },
          {
            title: '散件',
            key: 'looseItem',
            type: FormTypes.inputNumber,
            width: "200px",
            defaultValue: '',
            validateRules: [{ // 自定义函数校验 handler
              handler(type, value, row, column, callback, target) {
                if (type === 'change' || type === 'getValues') {
                  let {values} = target.getValuesSync({validate: false, rowIds: [row.id]})
                  let params = {}
                  params.code = values[0].materialCode;
                  if (values[0].standardDosage !== '') {
                    getAction('/initial/material/list', params).then(res => {
                      if (res.success) {
                        if (res.result.records[0].isBuyorderIn === 'N' && values[0].count > values[0].standardDosage) {
                          callback(false, '该物料不允许超过采购订单的采购数量入库')
                          return
                        }
                        callback(true);
                      }
                    })
                  }
                  callback(true);
                } else {
                  callback(); // 不填写或者填写 null 代表不进行任何操作
                }
              },
              message: '${title}默认提示'
            }, {required: true, message: '${title}不能为空'}],
          },
          {
            key: 'standardDosage',
            type: 'hidden',
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
            key: 'referId',
            width: "0px",
          },
        ]
      },
      url: {
        add: "/bill/bill/add/poo",
        edit: "/bill/bill/edit",
        billingDetail: {
          list: '/bill/bill/queryBillingDetailByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/purchase_out_bound_num'
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
        let rowKey = this.$refs.billingDetail.add();
        let setValueItem = {rowKey, values: {}}
        Object.keys(row).forEach(key => {
          if (this.model.clientId === undefined) {
            this.model.clientId = row['bill']['clientId'];
          }
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
    getReferPoData() {
      if (this.model.clientId === undefined) {
        this.$message.warning("请先选择供应商")
        return
      }
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
        billingDetailList: allValues.tablesValue[0].values
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