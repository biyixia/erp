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
            <a-form-model-item label="供应商名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="clientId">
              <j-select-merchant v-model="model.clientId" :multi="false"/>
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
      <a-tab-pane tab="加工明细" :key="refKeys[0]" :forceRender="true">
        <j-editable-table
          :ref="refKeys[0]"
          :loading="craftDetailTable.loading"
          :columns="craftDetailTable.columns"
          :dataSource="craftDetailTable.dataSource"
          :maxHeight="300"
          :disabled="formDisabled"
          :rowNumber="true"
          :rowSelection="true"
          :actionButton="true"
          :add-button="false"
        >
          <template slot="buttonAfter">
            <a-col>
              <a-button type="primary" @click="referProco">参照生产委托出库订单</a-button>
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
    <j-select-proco-modal :client-id="model.clientId" @ok="selectOK" ref="selectPro"/>
  </a-spin>
</template>

<script>

import {FormTypes, getRefPromise, VALIDATE_NO_PASSED} from '@/utils/JEditableTableUtil'
import {JEditableTableModelMixin} from '@/mixins/JEditableTableModelMixin'
import JSelectMerchant from '@/components/jeecgbiz/JSelectMerchant'
import JSelectProcoModal from '@/components/jeecgbiz/modal/JSelectProcoModal'
import {validateDuplicateValue} from '@/utils/util'
import {putAction} from "@api/manage";

export default {
  name: 'proConsignInForm',
  mixins: [JEditableTableModelMixin],
  components: {
    JSelectMerchant,
    JSelectProcoModal
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
      refKeys: ['craftDetail', 'other'],
      tableKeys: ['craftDetail',],
      activeKey: 'craftDetail',
      // 工艺线路
      craftDetailTable: {
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '工艺编码',
            key: 'craftCode',
            type: FormTypes.sel_craft,
            width: "200px",
            defaultValue: '',
            validateRules: [{required: true, message: '${title}不能为空'}],
          },
          {
            title: '工艺名称',
            key: 'craftName',
            disabled: true,
            type: FormTypes.sel_craft,
            width: "200px",
          },
          {
            title: '产品编码',
            key: 'productCode',
            disabled: true,
            type: FormTypes.sel_material,
            width: "200px",
          },
          {
            title: '产品名称',
            key: 'materialName',
            disabled: true,
            type: FormTypes.sel_material,
            width: "200px",
          },
          {
            title: '规格型号',
            key: 'size',
            disabled: true,
            type: FormTypes.sel_material,
            width: "200px",
          },
          {
            title: '序号',
            key: 'serialNumber',
            disabled: true,
            type: FormTypes.sel_craft,
            width: "200px",
            defaultValue: '',
          },

          {
            title: '完工数量',
            key: 'completeQuantity',
            type: FormTypes.input,
            width:"200px",
            defaultValue:'',
          },
          {
            title: '废品数量',
            key: 'wasteQuantity',
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
            type: 'hidden',
          },
          {
            key: 'bomRoutingId',
            type: 'hidden',
          },
        ]
      },
      url: {
        add: "/bill/bill/add/proci",
        edit: "/bill/bill/edit",
        craftDetail: {
          list: '/bill/bill/queryCraftDetailByMainId'
        },
        rule: {
          orderNum: '/sys/fillRule/executeRuleByCode/pcil_num'
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
        let rowKey = this.$refs.craftDetail.add();
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
          this.$refs.craftDetail.setValues([setValueItem])
        }
      }
    },
    referProco(){
      if (this.model.clientId === undefined) {
        this.$message.warning("请先选择供应商！");
        return;
      }
      this.$refs.selectPro.showModal();
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
      this.craftDetailTable.dataSource = []
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
        this.requestSubTableData(this.url.craftDetail.list, params, this.craftDetailTable)
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
        craftDetailList: allValues.tablesValue[0].values,
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