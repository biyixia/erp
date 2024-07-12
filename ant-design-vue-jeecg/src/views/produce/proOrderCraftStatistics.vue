<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">
    <div class="table-page-search-wrapper">
      <!-- 搜索区域 -->
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="物料编码" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入物料编码查询" v-model="queryParam.materialCode"></a-input>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
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
    <a-table bordered ref="table" size="middle" rowKey="inWarehouseId,materialCode" :columns="columns"
             :dataSource="dataSource" :pagination="ipagination" :loading="loading"
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
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'ropSearch',
  components: {},
  props: ['inWarehouseId', 'outWarehouseId', 'modalWidth', 'multi', 'store', 'text'],
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
          width: 180,
        },
        {
          title: '单据日期',
          key: 'bill.billDate',
          dataIndex: ['bill', 'billDate'],
          width: 100
        },
        {
          title: '产品编号',
          key: 'bill.product.code',
          dataIndex: ['bill','product', 'code'],
          width: 100
        },
        {
          title: '产品名称',
          key: 'bill.product.materialName',
          dataIndex: ['bill','product', 'materialName'],
          width: 100
        },
        {
          title: '规格型号',
          key: 'bill.product.size',
          dataIndex: ['bill','product', 'size'],
          width: 100
        },
        {
          title: '工艺编码',
          dataIndex: 'craftCode',
          width: 100
        },
        {
          title: '工艺名称',
          dataIndex: 'craftName',
          width: 100
        },
        {
          title: '加工单价',
          dataIndex: 'processCost',
          width: 100
        },
        {
          title: '料废单价',
          dataIndex: 'wastePrice',
          width: 100
        },
        {
          title: '工费单价',
          dataIndex: 'wagePrice',
          width: 100
        },
        {
          title: '委托加工',
          dataIndex: 'isConsign',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否")),
          width: 100
        },
        {
          title: '序号',
          dataIndex: 'serialNumber',
          width: 100
        },
        {
          title: '完工数量',
          dataIndex: 'completeCount',
          width: 100
        },
        {
          title: '委托数量',
          dataIndex: 'consignCount',
          width: 100
        },
      ],
      scrollTrigger: {},
      dataSource: [],
      selectionRows: [],
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
      getAction('/bill/bill/referBomRoutingList/proo', params).then(res => {
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

    getQueryField() {
      let str = 'id,';
      for (let a = 0; a < this.columns.length; a++) {
        str += ',' + this.columns[a].dataIndex;
      }
      return str;
    },
    searchReset() {
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
      this.getSelectPoRows();
      this.$emit('ok', this.selectPoRows);
      this.searchReset()
      this.close();
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
