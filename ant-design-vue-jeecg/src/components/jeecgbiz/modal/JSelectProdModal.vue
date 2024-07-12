<template>
  <j-modal
    :width="1250"
    :height="700"
    :visible="visible"
    :title="title"
    switchFullscreen
    wrapClassName="ant-modal-cust-warp"
    @ok="handleSubmit"
    @cancel="close"
    style="top:50px"
    cancelText="关闭"
  >
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <!-- 搜索区域 -->
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="物料编码" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="请输入物料名称查询" v-model="queryParam.code"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="物料名称" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入物料名称查询" v-model="queryParam.materialName"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="规格型号" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入规格型号查询" v-model="queryParam.size"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="计量单位" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入计量单位查询" v-model="queryParam.unit"></a-input>
            </a-form-item>
          </a-col>
          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-col :md="6" :sm="24">
                <a-button type="primary" @click="searchQuery">查询</a-button>
                <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
              </a-col>
            </span>
        </a-row>
      </a-form>
    </div>
    <!-- table区域-begin -->
    <a-table bordered ref="table" size="middle" rowKey="id" :columns="columns"
             :dataSource="dataSource" :pagination="ipagination" :loading="loading"
             :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: getType }"
             :scroll='{x:true}'
             @change="handleTableChange"
    >
      <!--      :customRow="rowAction"-->
      <template slot="pcaSlot" slot-scope="text">
        <div>{{ getPcaText(text) }}</div>
      </template>
    </a-table>
    <!--用户列表-->
  </j-modal>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import {getAction} from '@/api/manage'
import {FormTypes} from "@/utils/JEditableTableUtil";
import Area from "@comp/_util/Area";

export default {
  name: 'JSelectProdModal',
  components: {},
  props: ['modalWidth', 'multi', 'prodId', 'store', 'text'],
  data() {
    return {
      queryParam: {
        contactParam: "",
      },
      columns: [
        {
          title: '物料编码',
          align: 'left',
          dataIndex: 'code'
        },
        {
          title: '物料名称',
          align: 'center',
          dataIndex: 'materialName'
        },
        {
          title: '规格型号',
          align: 'center',
          dataIndex: 'size'
        },
        {
          title: '计量单位',
          align: 'center',
          dataIndex: 'unit',
        },
        {
          title: '件装量',
          align: 'center',
          dataIndex: 'unitLoad',
        },
        {
          title: '含税单价',
          align: 'center',
          dataIndex: 'priceIncludedTax',
        },
        {
          title: '税率',
          align: 'center',
          dataIndex: 'rate',
        },
        {
          title: '无税单价',
          align: 'center',
          dataIndex: 'price',
        },
      ],
      scrollTrigger: {},
      dataSource: [],
      selectionRows: [],
      selectedRowKeys: [],
      selectPoRows: [],
      title: '选择产品',
      ipagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '30'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      isorter: {
        column: 'createTime',
        order: 'desc'
      },
      visible: false,
      form: this.$form.createForm(this),
      loading: false,
      expandedKeys: [],
      labelCol: {
        xs: {span: 24},
        sm: {span: 4},
      },
      wrapperCol: {
        xs: {span: 24},
        sm: {span: 10},
      },
      pcaData: '',
    }
  },
  computed: {
    // 计算属性的 getter
    getType: function () {
      return 'radio';
    }
  },
  watch: {
    prodId(val){
      this.loadData()
    },
  },
  created() {
    this.pcaData = new Area()
    // 该方法触发屏幕自适应
    this.resetScreenSize();
    this.loadData()
  },
  methods: {
    getPcaText(code) {
      return this.pcaData.getText(code);
    },
    loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      let params = this.getQueryParams()//查询条件
      this.loading = true
      getAction('/initial/material/list', params).then(res => {
        if (res.success) {
          this.dataSource = res.result.records;
          this.ipagination.total = res.result.total;
        } else {
          this.$message.error(res.message);
        }
      }).finally(() => {
        this.loading = false
        // //该方法两个地方用 1.visible改变事件重新设置选中项 2.组件编辑页面回显
          for(let item of this.dataSource){
            if(this.prodId != null && this.prodId.indexOf(item[this.store])>=0){
              this.$emit("initComp", item[this.text].toString())
            }
          }
      })
    },
    getQueryParams() {
      //获取查询条件
      let sqp = {}
      var param = Object.assign(sqp, this.queryParam, this.isorter, this.filters);
      param.field = this.getQueryField();
      param.pageNo = this.ipagination.current;
      param.pageSize = this.ipagination.pageSize;
      return filterObj(param);
    },
    searchQuery() {
      this.loadData(1);
      this.selectedRowKeys = []
      this.selectionRows = []
    },
    // 触发屏幕自适应
    resetScreenSize() {
      let screenWidth = document.body.clientWidth;
      if (screenWidth < 500) {
        this.scrollTrigger = {x: 800};
      } else {
        this.scrollTrigger = {};
      }
    },
    showModal() {
      this.visible = true;
      this.loadData(1);
      this.form.resetFields();
    },
    getQueryField() {
      let str = 'id,';
      for (let a = 0; a < this.columns.length; a++) {
        str += ',' + this.columns[a].dataIndex;
      }
      return str;
    },
    searchReset() {
      this.selectedRowKeys = [];
      this.queryParam = {};
      this.loadData(1);
    },
    close() {
      this.searchReset();
      this.visible = false;
    },
    handleTableChange(pagination, filters, sorter) {
      // 筛选
      if (Object.keys(sorter).length > 0) {
        this.isorter.column = sorter.field;
        this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc';
      }
      this.ipagination = pagination;
      this.loadData();
    },
    handleSubmit() {
      this.getSelectPoRows();
      this.$emit('ok', this.selectPoRows);
      this.searchReset()
      this.close();
    },
    //获取选择信息
    getSelectPoRows() {
      this.selectPoRows = []
      for (let row of this.selectionRows) {
        if (this.selectedRowKeys.includes(row.id)) {
          this.selectPoRows.push(row)
        }
      }
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys;
      selectionRows.forEach(row => pushIfNotExist(this.selectionRows, row, 'id'))
    },
    onSearch() {
      this.loadData(1);
    },
    modalFormOk() {
      this.loadData();
    }
  }
}
</script>

<style scoped>
.ant-table-tbody .ant-table-row td {
  padding-top: 10px;
  padding-bottom: 10px;
}

#components-layout-demo-custom-trigger .trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 24px;
  cursor: pointer;
  transition: color .3s;
}
</style>
