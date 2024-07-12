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
            <a-form-item label="科目编码" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入科目编码查询" v-model="queryParam.subCode"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="科目名称" :labelCol="{ span: 5 }" :wrapperCol="{ span: 18, offset: 1 }">
              <a-input placeholder="请输入科目名称查询" v-model="queryParam.subName"/>
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
    <a-table
      ref="table"
      size="middle"
      rowKey="id"
      class="j-table-force-nowrap"
      :scroll="{x:true}"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :expandedRowKeys="expandedRowKeys"
      @change="handleTableChange"
      @expand="handleExpand"
      v-bind="tableProps"
      :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: getType }"
    >

      <template slot="imgSlot" slot-scope="text,record">
        <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
        <img v-else :src="getImgView(text)" :preview="record.id" height="25px" alt="" style="max-width:80px;font-size: 12px;font-style: italic;"/>
      </template>
      <template slot="fileSlot" slot-scope="text">
        <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
        <a-button
          v-else
          :ghost="true"
          type="primary"
          icon="download"
          size="small"
          @click="downloadFile(text)">
          下载
        </a-button>
      </template>

      <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a @click="handleAddChild(record)">添加下级</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDeleteNode(record.id)" placement="topLeft">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

    </a-table>

    <!--用户列表-->
  </j-modal>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import JSelectProd from "@comp/jeecgbiz/JSelectProd";
import {getAction} from '@/api/manage'
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'JSelectAccountSubModal',
  components: {JSelectProd},
  props: ['modalWidth', 'multi', 'materialIds', 'store', 'text'],
  data() {
    return {
      queryParam: {
        productId: ""
      },
      columns: [
        {
          title:'科目编码',
          align:"left",
          dataIndex: 'subCode'
        },
        {
          title:'科目名称',
          align:"left",
          dataIndex: 'subName'
        },
        {
          title:'余额方向',
          align:"left",
          dataIndex: 'subProperty_dictText'
        },
        {
          title:'部门核算',
          align:"left",
          dataIndex: 'isDepcheck',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        {
          title:'人员核算',
          align:"left",
          dataIndex: 'isEmpcheck',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        {
          title:'科目类型',
          align:"left",
          dataIndex: 'subclass_dictText'
        },
        {
          title:'客商核算',
          align:"left",
          dataIndex: 'isCuscheck',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        {
          title:'现金流量',
          align:"left",
          dataIndex: 'isCashflow',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        {
          title:'日记账',
          align:"left",
          dataIndex: 'isDiary',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        // {
        //   title: '数量', dataIndex: 'count', width: 100,
        // customRender(text, row, index) {
        //   if (row.isred === '1') {
        //     return text * (-1);
        //   } else {
        //     return text;
        //   }
        // }
        // },
      ],
      url: {
        list: "/initial/accountingSubject/rootList",
        childList: "/initial/accountingSubject/childList",
        getChildListBatch: "/initial/accountingSubject/getChildListBatch",
      },
      hasChildrenField:"hasChild",
      scrollTrigger: {},
      dataSource: [],
      selectionRows: [],
      selectedRowKeys: [],
      selectPoRows: [],
      expandedRowKeys:[],
      pidField:"pid",
      title: '选择会计科目',
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
    },
    tableProps() {
      let _this = this
      return {
        // 列表项是否可选择
        rowSelection: {
          selectedRowKeys: _this.selectedRowKeys,
          onChange: (selectedRowKeys) => _this.selectedRowKeys = selectedRowKeys
        }
      }
    }
  },
  watch: {},
  created() {
    // 该方法触发屏幕自适应
    this.resetScreenSize();
    this.loadData()
  },
  methods: {
    handleExpand(expanded, record){
      // 判断是否是展开状态
      if (expanded) {
        this.expandedRowKeys.push(record.id)
        if (record.children.length>0 && record.children[0].isLoading === true) {
          let params = this.getQueryParams(1);//查询条件
          params[this.pidField] = record.id
          params.hasQuery = 'false'
          params.superQueryParams=""
          getAction(this.url.childList,params).then((res)=>{
            if(res.success){
              if(res.result.records){
                record.children = this.getDataByResult(res.result.records)
                this.dataSource = [...this.dataSource]
              }else{
                record.children=''
                record.hasChildrenField='0'
              }
            }else{
              this.$message.warning(res.message)
            }
          })
        }
      }else{
        let keyIndex = this.expandedRowKeys.indexOf(record.id)
        if(keyIndex>=0){
          this.expandedRowKeys.splice(keyIndex, 1);
        }
      }
    },
    loadData(arg){
      if(arg===1){
        this.ipagination.current=1
      }
      this.loading = true
      let params = this.getQueryParams()
      params.hasQuery = 'true'
      getAction(this.url.list,params).then(res=>{
        if(res.success){
          let result = res.result
          if(Number(result.total)>0){
            this.ipagination.total = Number(result.total)
            this.dataSource = this.getDataByResult(res.result.records)
            return this.loadDataByExpandedRows(this.dataSource)
          }else{
            this.ipagination.total=0
            this.dataSource=[]
          }
        }else{
          this.$message.warning(res.message)
        }
      }).finally(()=>{
        this.loading = false
      })
    },
    loadDataByExpandedRows(dataList) {
      if (this.expandedRowKeys.length > 0) {
        return getAction(this.url.getChildListBatch,{ parentIds: this.expandedRowKeys.join(',') }).then(res=>{
          if (res.success && res.result.records.length>0) {
            //已展开的数据批量子节点
            let records = res.result.records
            const listMap = new Map();
            for (let item of records) {
              let pid = item[this.pidField];
              if (this.expandedRowKeys.join(',').includes(pid)) {
                let mapList = listMap.get(pid);
                if (mapList == null) {
                  mapList = [];
                }
                mapList.push(item);
                listMap.set(pid, mapList);
              }
            }
            let childrenMap = listMap;
            let fn = (list) => {
              if(list) {
                list.forEach(data => {
                  if (this.expandedRowKeys.includes(data.id)) {
                    data.children = this.getDataByResult(childrenMap.get(data.id))
                    fn(data.children)
                  }
                })
              }
            }
            fn(dataList)
          }
        })
      } else {
        return Promise.resolve()
      }
    },
    getDataByResult(result){
      if(result){
        return result.map(item=>{
          //判断是否标记了带有子节点
          if(item[this.hasChildrenField]==='1'){
            let loadChild = { id: item.id+'_loadChild', name: 'loading...', isLoading: true }
            item.children = [loadChild]
          }
          return item
        })
      }
    },
    getQueryParams(arg) {
      //获取查询条件
      let sqp = {}
      let param = {}
      if(this.superQueryParams){
        sqp['superQueryParams']=encodeURI(this.superQueryParams)
        sqp['superQueryMatchType'] = this.superQueryMatchType
      }
      if(arg){
        param = Object.assign(sqp, this.isorter ,this.filters);
      }else{
        param = Object.assign(sqp, this.queryParam, this.isorter ,this.filters);
      }
      if(JSON.stringify(this.queryParam) === "{}" || arg){
        param.hasQuery = 'false'
      }else{
        param.hasQuery = 'true'
      }
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
      this.expandedRowKeys = []
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
