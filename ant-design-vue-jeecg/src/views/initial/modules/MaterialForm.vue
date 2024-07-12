<template>
  <a-spin :spinning="confirmLoading">
    <j-form-container :disabled="formDisabled">
      <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="物料编码" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="code">
              <a-input v-model="model.code"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="物料名称" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="materialName">
              <a-input v-model="model.materialName"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="规格型号" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="size">
              <a-input v-model="model.size"></a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="所属类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="owningCode">
              <j-tree-select
                ref="treeSelect"
                v-model="model.owningCode"
                dict="byx_material_type,name,id"
                pidValue="0"
              >
              </j-tree-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="计量单位" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unit">
              <j-dict-select-tag type="list" v-model="model.unit" dictCode="unit"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="件装量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="unitLoad">
              <a-input-number v-model="model.unitLoad" style="width: 100%"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="税率" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="rate">
              <a-input-number v-model="model.rate" style="width: 100%" @change="changePr"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="含税单价" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="priceIncludedTax">
              <a-input-number v-model="model.priceIncludedTax" style="width: 100%" @change="changePrice"/>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="无税单价" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="price">
              <a-input-number v-model="model.price" style="width: 100%" @change="changePriceIncludedTax"/>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </j-form-container>
    <a-tabs v-model="activeKey" @change="handleChangeTabs">
      <a-tab-pane tab="详细设置" :key="refKeys[0]" :forceRender="true">
        <a-col :span="12">
          <a-form-model-item label="采购必有订单" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isInContract">
            <j-switch v-model="model.isInContract"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="超采购订单入库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isBuyorderIn">
            <j-switch v-model="model.isBuyorderIn"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="销售必有订单" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isOutContract">
            <j-switch v-model="model.isOutContract"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="超销售订单出库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isBuyorderOut">
            <j-switch v-model="model.isBuyorderOut"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="领用必有订单" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isReceiveOrder">
            <j-switch v-model="model.isReceiveOrder"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="超生产订单领用" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isBuyorderReceive">
            <j-switch v-model="model.isBuyorderReceive"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="领料必须申请" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isApply">
            <j-switch v-model="model.isApply"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="超申请出库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isApplyIn">
            <j-switch v-model="model.isApplyIn"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="超生产订单入库" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isProductIn">
            <j-switch v-model="model.isProductIn"></j-switch>
          </a-form-model-item>
        </a-col>

        <a-col :span="12">
          <a-form-model-item label="超计划生产" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isPlanProduct">
            <j-switch v-model="model.isPlanProduct"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="超计划采购" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isPlanIn">
            <j-switch v-model="model.isPlanIn"></j-switch>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="零星采购" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="isScattered">
            <j-switch v-model="model.isScattered"></j-switch>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
      <a-tab-pane tab="计划设置" :key="refKeys[1]" :forceRender="true">
        <a-col :span="12">
          <a-form-model-item label="计划类型" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="planType">
            <j-dict-select-tag type="list" v-model="model.planType" dictCode="plan_type"
                               style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="前置天数" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="leadDays">
            <a-input-number v-model="model.leadDays" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="日最大供应量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="maximumDailySupply">
            <a-input-number v-model="model.maximumDailySupply" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="最小供应量" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="minimumSupply">
            <a-input-number v-model="model.minimumSupply" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="最高库存" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="maximumStock">
            <a-input-number v-model="model.maximumStock" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="最低库存" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="minimumStock">
            <a-input-number v-model="model.minimumStock" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="安全库存" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="safeStock">
            <a-input-number v-model="model.safeStock" style="width: 100%"/>
          </a-form-model-item>
        </a-col>
      </a-tab-pane>
      <a-tab-pane tab="其他信息" :key="refKeys[2]" :forceRender="true">
        <a-col :span="12">
          <a-form-model-item label="制单人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createBy">
            <a-input v-model="model.createBy" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="制单日期" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="createTime">
            <j-date v-model="model.createTime" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss"
                    style="width: 100%" disabled/>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="更新人" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="updateBy">
            <a-input v-model="model.updateBy" disabled></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
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

import {httpAction, getAction} from '@/api/manage'
import {validateDuplicateValue} from '@/utils/util'
import {getRefPromise} from "@/utils/JEditableTableUtil";

export default {
  name: 'MaterialForm',
  components: {},
  props: {
    //表单禁用
    disabled: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  data() {
    return {
      model: {},
      labelCol: {
        xs: {span: 24},
        sm: {span: 8},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 16},
      },
      confirmLoading: false,
      validatorRules: {
        materialName: [
          {required: true, message: '请输入物料名称!'},
        ],
        owningCode: [
          {required: true, message: '请输入所属分类名称!'},
        ],
        unit: [
          {required: true, message: '请输入计量单位!'},
        ],
        rate: [
          {type: 'number', min: 0, max: 1, message: '税率超出范围!', trigger: 'change'},
        ],
      },
      refKeys: ['detail', 'plan', 'other'],
      activeKey: 'detail',
      url: {
        add: "/initial/material/add",
        edit: "/initial/material/edit",
        queryById: "/initial/material/queryById"
      }
    }
  },
  watch: {},
  computed: {
    formDisabled() {
      return this.disabled
    },
  },
  created() {
    //备份model原始值
    this.modelDefault = JSON.parse(JSON.stringify(this.model));
  },
  methods: {
    changePr() {
      if (this.model.price !== undefined) {
        this.model.priceIncludedTax = (this.model.price * (1 + this.model.rate)).toFixed(3).toString();
      } else if (this.model.priceIncludedTax !== undefined) {
        this.model.price = (this.model.priceIncludedTax / (1 + this.model.rate)).toFixed(3).toString();
      }
    },
    changePrice() {
      if (this.model.rate === undefined) {
        this.$message.warning("请填写税率！");
        return;
      }
      if (this.model.priceIncludedTax !== undefined) {
        this.model.price = (this.model.priceIncludedTax / (1 + this.model.rate)).toFixed(3).toString();
      }
    },
    changePriceIncludedTax() {
      if (this.model.rate === undefined) {
        this.$message.warning("请填写税率！");
        return;
      }
      if (this.model.price !== undefined) {
        this.model.priceIncludedTax = (this.model.price * (1 + this.model.rate)).toFixed(3).toString();
      }
    },
    add() {
      this.edit(this.modelDefault);
    },
    edit(record) {
      this.model = Object.assign({}, record);
      this.visible = true;
    },
    submitForm() {
      const that = this;
      // 触发表单验证
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true;
          let httpurl = '';
          let method = '';
          if (!this.model.id) {
            httpurl += this.url.add;
            method = 'post';
          } else {
            httpurl += this.url.edit;
            method = 'put';
          }
          httpAction(httpurl, this.model, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message);
              that.$emit('ok');
            } else {
              that.$message.warning(res.message);
            }
          }).finally(() => {
            that.confirmLoading = false;
          })
        }

      })
    },
    /** ATab 选项卡切换事件 */
    handleChangeTabs(key) {
      // 自动重置scrollTop状态，防止出现白屏
      getRefPromise(this, key).then(editableTable => {
        editableTable.resetScrollTop()
      })
    },
  }
}
</script>