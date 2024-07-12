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
          title: '数量', dataIndex: 'count', width: 100,
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
          title: '累计出库数量',
          width: 120,
          dataIndex: 'sumOutBound'
        },
        {
          title: '累计出库金额',
          width: 120,
          dataIndex: 'sumCInBound',
          customRender(text, row, index) {
            return (row.sumOutBound * row.material.price).toFixed(3);
          }
        },
        {
          title: '未出库数量', dataIndex: 'noOutBound', width: 100,
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
      params.total = true;
      getAction('/bill/bill/referPoList/proo', params).then(res => {
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
