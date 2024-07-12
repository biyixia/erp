<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <!-- 搜索区域 -->
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="单据编号" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入单据编号查询" v-model="queryParam.billCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="客户选择" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <j-select-customer v-model="queryParam.clientId" :multi="false"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="单据日期" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-range-picker style="width: 100%" v-model="queryParam.createTimeRange" format="YYYY-MM-DD"
                              :placeholder="['开始时间', '结束时间']" @change="onDateChange"/>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="未完成发票" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <j-dict-select-tag type="list" v-model="queryParam.noInvoice" dictCode="is_open"
                                 placeholder="是否未完成发票"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="物料编码" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入物料编码查询" v-model="queryParam.materialParam"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="物料名称" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入物料名称查询" v-model="queryParam.materialParam"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="物料规格" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入物料规格查询" v-model="queryParam.materialParam"></a-input>
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
    </a-table>
    <!--用户列表-->
  </a-card>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import {getAction} from '@/api/manage'
import JSelectCustomer from '@/components/jeecgbiz/JSelectCustomer'
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'soOutBoundStatistics',
  components: {JSelectCustomer},
  props: ['modalWidth', 'multi', 'materialIds', 'store', 'text'],
  data() {
    return {
      queryParam: {
        materialParam: "",
      },
      columns: [
        {
          title: '单据编号',
          key: 'bill.code',
          dataIndex: ['bill', 'code'],
          width: 170,
        },
        {
          title: '单据日期',
          key: 'bill.billDate',
          dataIndex: ['bill', 'billDate'],
          width: 100
        },
        {
          title: '客户名称',
          key: 'bill.merchant.name',
          dataIndex: ['bill', 'merchant', 'name'],
          width: 160,
        },
        {
          title: '调出仓库',
          key: 'bill.outWarehouse.warehouseName',
          dataIndex: ['bill', 'outWarehouse', 'warehouseName'],
          width: 160,
        },
        {
          title: '物料编码',
          key: 'material.code',
          width: 100,
          dataIndex: ['material', 'code']
        },
        {
          title: '物料名称',
          key: 'material.materialName',
          width: 100,
          dataIndex: ['material', 'materialName']
        },
        {
          title: '规格型号',
          key: 'material.size',
          width: 100,
          dataIndex: ['material', 'size']
        },
        {
          title: '计量单位',
          key: 'material.unit',
          width: 100,
          dataIndex: ['material', 'unit']
        },
        {
          title: '件装量',
          key: 'material.unitLoad',
          width: 100,
          dataIndex: ['material', 'unitLoad']
        },
        {
          title: '件数',
          width: 100,
          dataIndex: 'number'
        },
        {
          title: '散件',
          width: 100,
          dataIndex: 'looseItem'
        },
        {
          title: '含税单价',
          key: 'material.priceIncludedTax',
          width: 100,
          dataIndex: ['material', 'priceIncludedTax']
        },
        {
          title: '含税金额',
          width: 100,
          dataIndex: 'taxIncludedAmount'
        },
        {
          title: '税率',
          key: 'material.rate',
          width: 100,
          dataIndex: ['material', 'rate']
        },
        {
          title: '无税单价',
          key: 'material.price',
          width: 100,
          dataIndex: ['material', 'price']
        },
        {
          title: '无税金额',
          width: 100,
          dataIndex: 'amount'
        },
        {
          title: '税额',
          width: 100,
          dataIndex: 'tax'
        },
        {
          title: '数量', dataIndex: 'count', width: 100,
        },
        {
          title: '累计发票数量',
          width: 120,
          dataIndex: 'sumOutBound'
        },
        {
          title: '累计发票金额',
          width: 120,
          dataIndex: 'sumCOutBound',
          customRender(text, row, index) {
            return row.sumOutBound * row.material.priceIncludedTax;
          }
        },
        {
          title: '剩余未收发票数量',
          width: 130,
          dataIndex: 'sumNoOutBound',
          customRender(text, row, index) {
            return row.count - row.sumOutBound;
          }
        },
        {
          title: '创建人',
          width: 100,
          key: 'bill.createBy',
          dataIndex: ['bill', 'createBy']
        },
        {
          title: '创建日期',
          width: 150,
          key: 'bill.createTime',
          dataIndex: ['bill', 'createTime']
        },
      ],
      scrollTrigger: {},
      dataSource: [],
      selectionRows: [],
      selectedRowKeys: [],
      selectPoRows: [],
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
    }
  },
  computed: {
    // 计算属性的 getter
    getType: function () {
      return 'checkbox';
    }
  },
  watch: {},
  created() {
    // 该方法触发屏幕自适应
    this.resetScreenSize();
    this.loadData()
  },
  methods: {
    async loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      let params = this.getQueryParams()//查询条件
      this.loading = true
      params.total = true
      getAction('/bill/bill/referPoList/sos', params).then(res => {
        if (res.success) {
          this.dataSource = res.result.records
          this.ipagination.total = res.result.total
        }
      }).finally(() => {
        this.loading = false
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
    onDateChange: function (value, dateString) {
      this.queryParam.beginTime = dateString[0].substring(0, 10)
      this.queryParam.endTime = dateString[1].substring(0, 10)
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
      this.$emit('ok', this.selectPoRows);
      this.searchReset()
      this.close();
    },
    //获取选择信息
    getSelectPoRows() {
      this.selectPoRows = []
      for (let row of this.selectionRows) {
        // if (this.selectedRowKeys.includes(row.id)) {
        //   row.source = '采购入库单'
        //   row.sourceNumber = row.bill.code
        //   row.referId = row.id
        //   this.selectPoRows.push(row)
        // }
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
