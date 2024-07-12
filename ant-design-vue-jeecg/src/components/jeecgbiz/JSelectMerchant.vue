<template>
  <div>
    <a-input
      v-model="textVals"
      readOnly
      unselectable="on"
      :disabled="disabled"
      @click="onSearchDepUser">
    </a-input>
    <j-select-merchant-modal
      ref="selectModal"
      :modal-width="modalWidth"
      :multi="multi"
      @ok="selectOK"
      :merchant-id="value"
      :store="storeField"
      :text="textField"
      @initComp="initComp"/>
  </div>
</template>

<script>
import JSelectMerchantModal from './modal/JSelectMerchantModal'
import { underLinetoHump } from '@/components/_util/StringUtil'

export default {
  name: 'JSelectMerchant',
  components: {JSelectMerchantModal},
  props: {
    modalWidth: {
      type: Number,
      default: 1250,
      required: false
    },
    value: {
      type: String,
      required: false
    },
    disabled: {
      type: Boolean,
      required: false,
      default: false
    },
    multi: {
      type: Boolean,
      default: false,
      required: false
    },
    backUser: {
      type: Boolean,
      default: false,
      required: false
    },
    // 存储字段 [key field]
    store: {
      type: String,
      default: 'id',
      required: false
    },
    // 显示字段 [label field]
    text: {
      type: String,
      default: 'name',
      required: false
    }
  },
  data() {
    return {
      storeVals: '', //[key values]
      textVals: '' //[label values]
    }
  },
  computed:{
    storeField(){
      let field = this.customReturnField
      if(!field){
        field = this.store;
      }
      return underLinetoHump(field)
    },
    textField(){
      return underLinetoHump(this.text)
    }
  },
  mounted() {
    this.storeVals = this.value
  },
  watch: {
    value(val) {
      this.storeVals = val
    }
  },
  model: {
    prop: 'value',
    event: 'change'
  },
  methods: {
    initComp(textVals) {
      this.textVals = textVals
    },
    //返回选中的用户信息
    backDeparInfo(){
      if(this.backUser===true){
        if(this.storeVals && this.storeVals.length>0){
          let arr1 = this.storeVals.split(',')
          let arr2 = this.textVals.split(',')
          let info = []
          for(let i=0;i<arr1.length;i++){
            info.push({
              value: arr1[i],
              text: arr2[i]
            })
          }
          this.$emit('back', info)
        }
      }
    },
    onSearchDepUser() {
      this.$refs.selectModal.showModal()
    },
    selectOK(rows) {
      console.log("当前选中供应商", rows)
      if (!rows) {
        this.storeVals = ''
        this.textVals = ''
      } else {
        let temp1 = []
        let temp2 = []
        for (let item of rows) {
          temp1.push(item[this.storeField])
          temp2.push(item[this.textField])
        }
        this.storeVals = temp1.join(',')
        this.textVals = temp2.join(',')
      }
      this.$emit("change", this.storeVals)
    }
  }
}
</script>

<style scoped>

</style>