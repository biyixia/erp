<template>
  <j-form-container :disabled="disabled">
    <a-form-model ref="form" :model="model" :rules="validatorRules" slot="detail">
      <a-row>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="所属部门" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="depName">
            <a-input v-model="model.depName"></a-input>
          </a-form-model-item>
        </a-col>
        <a-col :xs="24" :sm="12">
          <a-form-model-item label="部门业务员" :labelCol="labelCol" :wrapperCol="wrapperCol" prop="depPerson">
            <a-input v-model="model.depPerson"></a-input>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
 </j-form-container>
</template>
<script>
  import { getAction } from '@/api/manage'
  import { validateDuplicateValue } from '@/utils/util'
  import { VALIDATE_NO_PASSED } from '@/utils/JEditableTableUtil'

  export default {
    name: 'SupplierForm',
    components: { 
    },
    props:{
      disabled: {
        type: Boolean,
        default: false,
        required: false
      }
    },
    data () {
      return {
        model:{
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        validatorRules: {
           depName: [
              { required: true, message: '请输入所属部门!'},
           ],
           depPerson: [
              { required: true, message: '请输入部门业务员!'},
           ],
        },
        confirmLoading: false,
      }
    },
    created () {
    //备份model原始值
      this.modelDefault = JSON.parse(JSON.stringify(this.model));
    },
    methods:{
      initFormData(url,id){
        this.clearFormData()
        if(!id){
          this.edit(this.modelDefault);
        }else{
          getAction(url,{id:id}).then(res=>{
            if(res.success){
              let records = res.result.records
              if(records && records.length>0){
                this.edit(records[0])
              }
            }
          })
        }
      },
      edit(record){
        this.model = Object.assign({}, record)
      },
      getFormData(){
        let formdata_arr = []
         this.$refs.form.validate(valid => {
          if (valid) {
            let isNullObj = true
            Object.keys(this.model).forEach(key=>{
              if(this.model[key]){
                isNullObj = false
              }
            })
            if(!isNullObj){
              formdata_arr.push(this.model)
            }
          }else{
            this.$emit("validateError","供应商补充信息表单校验未通过");
            return false
          }
        })
        return formdata_arr;
      },
      validate(index){
        return new Promise((resolve, reject) => {
          // 验证主表表单
         this.$refs.form.validate(valid => {
            !valid ? reject({ error: VALIDATE_NO_PASSED ,index}) : resolve()
          })
        }).then(values => {
          return Promise.resolve()
        }).catch(error => {
          return Promise.reject(error)
        })

      },
      clearFormData(){
        this.$refs.form.clearValidate()
      }
    
    }
  }
</script>
