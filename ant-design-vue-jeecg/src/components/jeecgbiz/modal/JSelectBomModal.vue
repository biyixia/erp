<template>
  <j-modal
    :width="1250"
    :height="600"
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
<!--    <div class="table-page-search-wrapper">-->
      <!-- 搜索区域 -->
<!--      <a-form layout="inline" @keyup.enter.native="searchQuery">-->
<!--        <a-row :gutter="24">-->
<!--          <a-col :md="6" :sm="24">-->
<!--            <a-form-item label="物料编码" :labelCol="labelCol" :wrapperCol="wrapperCol">-->
<!--              <a-input placeholder="请输入物料名称查询" v-model="queryParam.code"></a-input>-->
<!--            </a-form-item>-->
<!--          </a-col>-->
<!--          <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">-->
<!--              <a-col :md="6" :sm="24">-->
<!--                <a-button type="primary" @click="searchQuery">查询</a-button>-->
<!--                <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>-->
<!--              </a-col>-->
<!--            </span>-->
<!--        </a-row>-->
<!--      </a-form>-->
<!--    </div>-->
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

      <!-- 内嵌table区域 begin -->
      <template slot="expandedRowRender" slot-scope="record">
        <a-tabs tabPosition="top">
          <a-tab-pane tab="子件明细" key="bomSubroutine" forceRender>
            <bom-subroutine-sub-table :record="record"/>
          </a-tab-pane>
          <a-tab-pane tab="工艺线路" key="BomRouting" forceRender>
            <bom-routing-sub-table :record="record"/>
          </a-tab-pane>
        </a-tabs>
      </template>
      <!-- 内嵌table区域 end -->
    </a-table>
    <!--用户列表-->
  </j-modal>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import {getAction} from '@/api/manage'
import BomSubroutineSubTable from '../../../views/initial/subTables/BomSubroutineSubTable'
import BomRoutingSubTable from '../../../views/initial/subTables/BomRoutingSubTable'
import {FormTypes} from "@/utils/JEditableTableUtil";
import Area from "@comp/_util/Area";

export default {
  name: 'JSelectBomModal',
  components: {BomSubroutineSubTable,BomRoutingSubTable},
  props: ['modalWidth', 'multi', 'bomId','prodId', 'store', 'text'],
  data() {
    return {
      queryParam: {
      },
      columns: [
        {
          title: '单据编码',
          align: 'center',
          dataIndex: 'code',
        },
        {
          title: '单据日期',
          align: 'center',
          dataIndex: 'billsDate',
        },
        {
          title: '产品名称',
          align: 'center',
          dataIndex: ['material','materialName'],
          key:'material.materialName'
        },
        {
          title: 'BOM名称',
          align: 'center',
          dataIndex: 'bomName',
        },
      ],
      scrollTrigger: {},
      dataSource: [],
      selectionRows: [],
      selectedRowKeys: [],
      selectPoRows: [],
      title: '选择BOM',
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
    bomId(){
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
      getAction('/initial/bom/list', params).then(res => {
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
            if(this.bomId != null && this.bomId.indexOf(item[this.store])>=0){
              this.$emit("initComp", item[this.text].toString())
            }
          }
      })
    },
    getQueryParams() {
      //获取查询条件
      let sqp = {}
      this.queryParam.prodId = this.prodId
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
