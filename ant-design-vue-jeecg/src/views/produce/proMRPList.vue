<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">
    <a-table bordered ref="table" size="middle" rowKey="materialCode" :columns="columns"
             :dataSource="dataSource" :pagination="ipagination" :loading="loading"
             :scroll='{x:true}'
             @change="handleTableChange"
             :expandedRowKeys="expandedRowKeys"
             @expand="handleExpand"
    >
      <template slot="action" slot-scope="text, record">
        <a v-if="record.isOutput === 'Y'" @click="handleProduce(record)">下达生产</a>
        <a v-if="record.isOutput === 'N'" @click="handlePurchase()">下达采购</a>
      </template>
    </a-table>
    <pro-order-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import {getAction} from '@/api/manage'
import proOrderModal from './modules/proOrderModal'

import {FormTypes} from "@/utils/JEditableTableUtil";
import {JeecgListMixin} from "@/mixins/JeecgListMixin";

export default {
  name: 'ropSearch',
  components: {proOrderModal},
  mixins: [JeecgListMixin],
  props: ['inWarehouseId', 'outWarehouseId', 'modalWidth', 'multi', 'store', 'text'],
  data() {
    return {
      queryParam: {
        materialParam: "",
      },
      columns: [
        {
          title: '需求日期',
          dataIndex: 'demandedDate',
          defaultSortOrder: 'descend', // 默认上到下为由大到小的顺序
          sorter: (a, b) => {
            return a.time > b.time ? 1 : -1
          },
          width: 100,
        },
        {
          title: '物料编码',
          width: 100,
          dataIndex: 'materialCode'
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
          dataIndex: 'number',
          customRender(text, row, index) {
            return parseInt(row.count / row.material.unitLoad);
          },
        },
        {
          title: '散件',
          width: 100,
          dataIndex: 'looseItem',
          customRender(text, row, index) {
            return (row.count % row.material.unitLoad).toFixed(3);
          },
        },
        {
          title: '数量',
          width: 100,
          dataIndex: 'count',
          customRender(text, row, index) {
            return text.toFixed(3);
          },
        },
        {
          title: '产出品',
          width: 100,
          dataIndex: 'isOutput',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          width: 147,
          scopedSlots: {customRender: 'action'},
        },
      ],
      scrollTrigger: {},
      expandedRowKeys: [],
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
    handleProduce(record) {
      let record1 = {};
      record1.demandedDate = record.demandedDate
      record1.produceNumber = record.count;
      record1.productId = record.material.id;
      record1.product = record.material;
      record1.billCode = null;
      record1.billDate = null;
      this.$refs.modalForm.plan(record1);
      this.$refs.modalForm.title = "计划下达";
      this.$refs.modalForm.disableSubmit = false;
    },
    handlePurchase() {
      this.$router.push('/po/pOrder')
    },
    handleExpand(expanded, record) {
      this.expandedRowKeys = []
      if (expanded === true) {
        this.expandedRowKeys.push(record.id)
      }
    },
    async loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      let params = this.getQueryParams()//查询条件
      this.loading = true
      getAction('/bill/bill/getMRP', params).then(res => {
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
