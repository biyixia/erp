<template>
  <div>
    <a-input
      v-model=this.value
      readOnly
      unselectable="on"
      :disabled="disabled"
      @click="onSearchDepUser">
    </a-input>
    <j-select-material-modal
      ref="selectModal"
      :modal-width="modalWidth"
      :multi="multi"
      @ok="selectOK"
      :user-ids="value"
      :store="storeField"
      :text="textField"
      @initComp="initComp"/>
  </div>
</template>

<script>
import JSelectMaterialModal from './modal/JSelectMaterialModal'
import {underLinetoHump} from '@/components/_util/StringUtil'
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'JSelectMaterial',
  components: {JSelectMaterialModal},
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
      default: true,
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
      required: false
    },
    // 显示字段 [label field]
    text: {
      type: String,
      required: false
    }
  },
  data() {
    return {
      storeVals: '', //[key values]
      textVals: '', //[label values]
    }
  },
  computed: {
    storeField() {
      let field = this.customReturnField
      if (!field) {
        field = this.store;
      }
      return underLinetoHump(field)
    },
    textField() {
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
    backDeparInfo() {
      if (this.backUser === true) {
        if (this.storeVals && this.storeVals.length > 0) {
          let arr1 = this.storeVals.split(',')
          let arr2 = this.textVals.split(',')
          let info = []
          for (let i = 0; i < arr1.length; i++) {
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
      console.log("当前选中物料", rows)
      let temp = []
      for (let item of rows) {
        if (this.storeField === 'materialCode') {
          temp.push(item['code']);
          break;
        }
        temp.push(item[this.storeField]);
      }
      this.storeVals = temp.join(',')
      this.$emit('input', rows[0])
      this.$emit("change", this.storeVals)
    }
  }
}
</script>

<style scoped>

</style>