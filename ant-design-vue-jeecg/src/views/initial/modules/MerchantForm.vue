<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <!-- 主表单区域 -->
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="是否客户" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isCustomer">
              <j-switch v-model="model.isCustomer"></j-switch>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="是否供应商" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isSupplier">
              <j-switch v-model="model.isSupplier"></j-switch>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="客商编号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="客商名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="name">
              <a-input v-model="model.name"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="所属地区" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="area">
              <j-area-linkage type="cascader" v-model="model.area"/>
            </a-form-model-item>
          </a-col>
          <a-col :xs="24" :sm="12">
            <a-form-model-item label="详细地址" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="address">
              <a-input v-model="model.address"></a-input>
            </a-form-model-item>
          </a-col>

          <a-col :xs="24" :sm="12">
            <a-form-model-item label="排序" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="sorter">
              <a-input v-model="model.sorter"></a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <!-- 子表单区域 -->
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="联系信息" :key="refKeys[0]" :forceRender="true">
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="联系人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="contacts">
            <a-input v-model="model.contacts"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="联系电话" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="phoneNum">
            <a-input v-model="model.phoneNum"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="QQ" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="qq">
            <a-input v-model="model.qq"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="微信" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="wechat">
            <a-input v-model="model.wechat"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="电子邮箱" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="email">
            <a-input v-model="model.email"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="传真" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="fax">
            <a-input v-model="model.fax"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="手机号码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="telephone">
            <a-input v-model="model.telephone"></a-input>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
      <a-tab-pane tab="结算信息" :key="refKeys[1]" :forceRender="true">
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="统一信用证号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="creditNum">
            <a-input v-model="model.creditNum"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="币别" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="currency">
            <j-dict-select-tag type="list" v-model="model.currency" dictCode="currency" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="所属银行" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankType">
            <j-dict-select-tag type="list" v-model="model.bankType" dictCode="bank_type" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="开户行" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankName">
            <a-input v-model="model.bankName"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="联行号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankNum">
            <a-input v-model="model.bankNum"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="银行账号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="bankAccount">
            <a-input v-model="model.bankAccount"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="纳税人识别号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="taxNum">
            <a-input v-model="model.taxNum"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="税率" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="taxRate">
            <a-input-number v-model="model.taxRate" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
      <a-tab-pane tab="备注" :key="refKeys[2]" :forceRender="true">
        <a-col>
          <a-form-model-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="description">
            <a-textarea v-decorator="['description']" rows="4"/>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
      <a-tab-pane tab="其它信息" :key="refKeys[2]" :forceRender="true">
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
import {validateDuplicateValue} from '@/utils/util'
import CustomerForm from './CustomerForm.vue'
import SupplierForm from './SupplierForm.vue'

export default {
  name: 'MerchantForm',
  mixins: [JEditableTableModelMixin],
  components: {
    CustomerForm,
    SupplierForm,
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
        isCustomer: '',
        isSupplier: '',
      },
      validatorRules: {
        code: [
          {required: true, message: '请输入客商编号!'},
        ],
        name: [
          {required: true, message: '请输入客商名称!'},
        ],
        contacts: [
          {required: true, message: '请输入联系人!'},
        ],
      },
      // 新增时子表默认添加几行空数据
      addDefaultRowNum: 1,
      refKeys: ['contact','payInfo','description', 'other'],
      tableKeys: [],
      activeKey: 'contact',
      // 客户补充信息
      customerTable: {
        loading: false,
        dataSource: [],
        columns: []
      },
      // 供应商补充信息
      supplierTable: {
        loading: false,
        dataSource: [],
        columns: []
      },
      url: {
        add: "/initial/merchant/add",
        edit: "/initial/merchant/edit",
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
    addBefore() {
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