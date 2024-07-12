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
    <a-table bordered ref="table" size="middle" rowKey="materialCode" :columns="columns"
             :dataSource="dataSource" :pagination="ipagination" :loading="loading"
             :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: getType }"
             :scroll='{x:true}'
             @change="handleTableChange"
    >
    </a-table>
  </j-modal>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import {getAction} from '@/api/manage'
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'JSelectPoModal',
  components: {},
  props: ['modalWidth', 'multi', 'clientId', 'store', 'text'],
  data() {
    return {
      queryParam: {
        materialParam: "",
      },
      columns: [
        {
          title: '需求日期',
          align: 'center',
          width: 100,
          dataIndex: 'demandedDate',
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
          title: '数量', dataIndex: 'count', width: 100,
        },
        {
          title: '累计下单数量',
          width: 120,
          dataIndex: 'invoiceNumber'
        },
      ],
      scrollTrigger: {},
      dataSource: [],
      selectionRows: [],
      selectedRowKeys: [],
      selectPoRows: [],
      title: '选择MRP计划',
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
      getAction('/bill/bill/purchaseReferMRP', params).then(res => {
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
      this.getSelectPoRows();
      this.$emit('ok', this.selectPoRows);
      this.searchReset()
      this.close();
    },
    //获取选择信息
    getSelectPoRows() {
      this.selectPoRows = []
      for (let row of this.selectionRows) {
        if (this.selectedRowKeys.includes(row.materialCode)) {
          row.source = 'MRP计划'
          row.number = parseInt(row.count / row.material.unitLoad);
          row.looseItem = (row.count % row.material.unitLoad).toFixed(3);
          row.expectedDelivery = row.demandedDate
          //自动更新含税金额
          row.taxIncludedAmount = row.material.priceIncludedTax * row.count;
          row.amount = (row.material.price * row.count).toFixed(3);
          row.tax = (row.taxIncludedAmount - row.amount).toFixed(3)
          row.standardDosage = row.count
          this.selectPoRows.push(row)
        }
      }
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys;
      selectionRows.forEach(row => pushIfNotExist(this.selectionRows, row, 'materialCode'))
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
