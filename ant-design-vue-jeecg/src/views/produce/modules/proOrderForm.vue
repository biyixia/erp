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
            <a-form-model-item label="产品名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="productId">
              <j-select-prod v-model="model.productId" @selectOK="getUnitLoad"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="BOM名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bomId">
              <j-select-bom v-model="model.bomId" :prod-id="model.productId"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="件装量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unitLoad">
              <a-input-number v-model="model.unitLoad" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="生产数量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="produceNumber" :rules="[
                { validator: checkProduceNumber, trigger: ['blur'] }
            ]">
              <a-input-number @blur="getNumber" v-model="model.produceNumber" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="生产件数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="number">
              <a-input-number v-model="model.number" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="生产散件" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="looseItem">
              <a-input-number v-model="model.looseItem" disabled style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="完工日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="demandedDate">
              <j-date v-model="model.demandedDate" style="width: 100%"/>
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
      <a-tab-pane tab="领料明细" :key="refKeys[0]" :forceRender="true">
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
              <a-button type="primary" @click="getStandard">生成标准用量</a-button>
            </a-col>
          </template>
        </j-editable-table>
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
          <template slot="buttonAfter">
            <a-col>
              <a-button type="primary" @click="getStandardCraft">生成标准工艺</a-button>
            </a-col>
          </template>
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
import JSelectProd from '@/components/jeecgbiz/JSelectProd'
import JSelectBom from '@/components/jeecgbiz/JSelectBom'
import {validateDuplicateValue} from '@/utils/util'
import {putAction, getAction} from "@api/manage";

export default {
  name: 'proOrderForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectProd,
    JSelectBom
  },
  data() {
    return {
      planNumber: -1,
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
      refKeys: ['billingDetail', 'bomRouting', 'other'],
      tableKeys: ['billingDetail', 'bomRouting',],
      activeKey: 'billingDetail',
      // 领料明细
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
            title: '分子数量',
            key: 'numerator',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '分母数量',
            key: 'denominator',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '标准用量',
            key: 'standardDosage',
            type: FormTypes.input,
            disabled: true,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '数量',
            key: 'count',
            type: FormTypes.input,
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
            type: FormTypes.input,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '散件',
            key: 'looseItem',
            type: FormTypes.input,
            width: "200px",
            defaultValue: '0',
          },
          {
            title: '需求日期',
            key: 'demandedDate',
            type: FormTypes.date,
            width: "200px",
            defaultValue: '',
          },
          {
            title: '是否产出品',
            key: 'isOutput',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
            width: "200px",
            defaultValue: '',
          },
          {
            title: '是否足额配料',
            key: 'fullIngredients',
            type: FormTypes.checkbox,
            customValue: ['Y', 'N'],
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
          },
          {
            key: 'priceIncludedTax',
            type: 'hidden'
          },
          {
            key: 'price',
            type: 'hidden'
          },
          {
            key: 'taxIncludedAmount',
            type: 'hidden'
          },
          {
            key: 'amount',
            type: 'hidden'
          },
          {
            key: 'tax',
            type: 'hidden'
          },
        ]
      },
      // 工艺线路
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
            title: '工废单价',
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
            defaultValue: '',
          },
        ]
      },
      url: {
        add: "/bill/bill/add/proo",
        edit: "/bill/bill/edit",
        bomList: '/initial/bom/queryBomSubroutineByMainId',
        bomRoutingList: '/initial/bom/queryBomRoutingByBomId',
        billingDetail: {
          list: '/bill/bill/queryBillingDetailByMainId'
        },
        bomRouting: {
          list: '/bill/bill/queryBomRoutingByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/pol_num'
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
    checkProduceNumber(rule, value, callback) {
      if (this.planNumber > 0) {
        if (value > this.planNumber) {
          let params = this.model.productId
          getAction('/initial/material/list', params).then(res => {
            if (res.result.records[0].isPlanProduct === 'N') {
              callback(new Error("不允许超计划生产"))
              return
            }
            callback();
          })
          return;
        }
        callback();
      } else {
        callback();
      }
    },
    editBefore(record) {
      if (record.product !== undefined)
        this.unitLoad = record.product.unitLoad
    },
    getNumber() {
      if (this.model.produceNumber !== undefined) {
        this.model.number = parseInt(this.model.produceNumber / this.model.unitLoad)
        this.model.looseItem = this.model.produceNumber % this.model.unitLoad
      }
    },
    getUnitLoad(rows) {
      this.model.unitLoad = rows[0].unitLoad
    },
    getStandardCraft() {
      if (this.model.bomId === undefined) {
        this.$message.warning("请先选择物料清单！")
        return;
      }
      getAction(this.url.bomRoutingList, {
        id: this.model.bomId
      }).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          for (let row of res.result.records) {
            console.log(row)
            let rowKey = this.$refs.bomRouting.add();
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
              this.$refs.bomRouting.setValues([setValueItem])
            }
          }
        }
      })
    },
    getStandard() {
      if (this.model.bomId === undefined) {
        this.$message.warning("请先选择物料清单！")
        return;
      }
      getAction(this.url.bomList, {
        id: this.model.bomId
      }).then(res => {
        // 执行成功，获取返回的值，并赋到页面上
        if (res.success) {
          for (let row of res.result.records) {
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
                //生成子件标准用量以及件数、散件
                let count = row['numerator'] / row['denominator'] * this.model.produceNumber
                setValueItem.values['standardDosage'] = count
                setValueItem.values['materialCode'] = row['code']
                setValueItem.values['demandedDate'] = this.model.demandedDate
                setValueItem.values['count'] = count
                setValueItem.values['number'] = parseInt(count / row['unitLoad'])
                setValueItem.values['looseItem'] = (count % row['unitLoad']).toFixed(3)
                if (row['material'] !== undefined) {
                  setValueItem.values['taxIncludedAmount'] = count * row['material']['priceIncludedTax'];
                  setValueItem.values['amount'] = (count * row['material']['price']).toFixed(3)
                  setValueItem.values['tax'] = (count * (row['material']['priceIncludedTax'] - row['material']['price'])).toFixed(3)
                }
                //设置来源
                setValueItem.values['source'] = '生成标准用量'
              } else {
                setValueItem.values[key] = ''
              }
            })
            if (Object.keys(setValueItem).length > 0) {
              this.$refs.billingDetail.setValues([setValueItem])
            }
          }
        }
      });
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
    addBefore() {
      this.billingDetailTable.dataSource = []
      this.bomRoutingTable.dataSource = []
      this.getOrderNum()
    },
    getAllTable() {
      let values = this.tableKeys.map(key => getRefPromise(this, key))
      return Promise.all(values)
    },
    /** 调用完edit()方法之后会自动调用此方法 */
    editAfter() {
      this.model.unitLoad = this.unitLoad
      this.getNumber()
      this.$nextTick(() => {
      })
      // 加载子表数据
      if (this.model.id) {
        let params = {id: this.model.id}
        this.requestSubTableData(this.url.billingDetail.list, params, this.billingDetailTable)
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
        billingDetailList: allValues.tablesValue[0].values,
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
      this.$router.push('/produce/proOrderList')
    },
    popupCallback(value, row) {
      this.model = Object.assign(this.model, row);
    },
  }
}
</script>

<style scoped>
.tableHidden {
  display: none;
}
</style>