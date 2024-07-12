<template>
  <j-modal
    :width="modalWidth"
    :visible="visible"
    :title="title"
    switchFullscreen
    wrapClassName="j-user-select-modal"
    @ok="handleSubmit"
    @cancel="close"
    style="top:50px"
    cancelText="关闭"
  >
    <a-row :gutter="10" style="background-color: #ececec; padding: 10px; margin: -10px">
      <a-col :md="6" :sm="24">
        <a-card :bordered="false">
          <!--组织机构-->
          <a-tree
            selectable
            :selectedKeys="selectedTypeIds"
            :checkStrictly="true"
            :dropdownStyle="{maxHeight:'200px',overflow:'auto'}"
            :treeData="materialTypeTree"
            :expandAction="false"
            @select="onTypeSelect"
            :load-data="onLoadMaterialType"
          />
        </a-card>
      </a-col>
      <a-col :md="18" :sm="24">
        <a-card :bordered="false">
          <a-form-model>
            <a-form-model-item label="物料名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-row type="flex" :gutter="8">
                <a-col :span="18">
                  <a-input-search
                    :style="{width:'100%'}"
                    placeholder="请输入物料名称"
                    v-model="queryParam.materialName"
                    @search="onSearch"
                  ></a-input-search>
                </a-col>
                <a-col :span="6">
                  <a-button @click="searchReset(1)" icon="redo">重置</a-button>
                </a-col>
              </a-row>
            </a-form-model-item>
          </a-form-model>
          <!--用户列表-->
          <a-table
            ref="table"
            :scroll="scrollTrigger"
            size="middle"
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: getType}"
            :loading="loading"
            @change="handleTableChange">
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </j-modal>
</template>

<script>
import {pushIfNotExist, filterObj} from '@/utils/util'
import {queryMaterialTypeTreeSync,queryMaterialByTypeId} from '@/api/api'
import {getAction} from '@/api/manage'
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'JSelectMaterialModal',
  components: {},
  props: ['modalWidth', 'multi', 'materialIds', 'store', 'text'],
  data() {
    return {
      queryParam: {
        materialName: "",
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
      selectUserRows: [],
      selectmaterialIds: [],
      title: '选择物料',
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
      selectedTypeIds: [],
      materialTypeTree: [],
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
      return 'radio';
    }
  },
  watch: {
    materialIds: {
    //   immediate: true,
    //   handler() {
    //     this.initUserNames()
    //   }
    },
  },
  created() {
    // 该方法触发屏幕自适应
    this.resetScreenSize();
    this.loadData()
  },
  methods: {
    // initUserNames() {
    //   if (this.materialIds) {
    //     // 这里最后加一个 , 的原因是因为无论如何都要使用 in 查询，防止后台进行了模糊匹配，导致查询结果不准确
    //     let values = this.materialIds.split(',') + ','
    //     let param = {[this.store]: values}
    //     getAction('/sys/user/getMultiUser', param).then((list) => {
    //       this.selectionRows = []
    //       let selectedRowKeys = []
    //       let textArray = []
    //       if (list && list.length > 0) {
    //         for (let user of list) {
    //           textArray.push(user[this.text])
    //           selectedRowKeys.push(user['id'])
    //           this.selectionRows.push(user)
    //         }
    //       }
    //       this.selectedRowKeys = selectedRowKeys
    //       this.$emit('initComp', textArray.join(','))
    //     })
    //
    //   } else {
    //     // JSelectUserByDep组件bug issues/I16634
    //     this.$emit('initComp', '')
    //     // 前端用户选择单选无法置空的问题 #2610
    //     this.selectedRowKeys = []
    //   }
    // },
    async loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1;
      }
      let params = this.getQueryParams()//查询条件
      this.loading = true
      getAction('/initial/material/queryMaterialComponentData', params).then(res => {
        if (res.success) {
          this.dataSource = res.result.records
          this.ipagination.total = res.result.total
        }
      }).finally(() => {
        this.loading = false
      })
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
      this.querymaterialTypeTree();
      // this.initUserNames()
      this.loadData();
      this.form.resetFields();
    },
    getQueryParams() {
      let param = Object.assign({}, this.queryParam, this.isorter);
      param.field = this.getQueryField();
      param.pageNo = this.ipagination.current;
      param.pageSize = this.ipagination.pageSize;
      param.owningCode = this.selectedTypeIds.join(',')
      return filterObj(param);
    },
    getQueryField() {
      let str = 'id,';
      for (let a = 0; a < this.columns.length; a++) {
        str += ',' + this.columns[a].dataIndex;
      }
      return str;
    },
    searchReset(num) {
      let that = this;
      that.selectedRowKeys = [];
      that.selectmaterialIds = [];
      that.selectedTypeIds = [];
      if (num !== 0) {
        that.queryParam = {};
        that.loadData(1);
      }
    },
    close() {
      this.searchReset(0);
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
      let that = this;
      this.getSelectUserRows();
      if (that.selectUserRows.length > 1){
        this.$message.warning("请选择一行数据！")
        return
      }
      that.$emit('ok', that.selectUserRows);
      that.searchReset(0)
      that.close();
    },
    //获取选择用户信息
    getSelectUserRows() {
      this.selectUserRows = []
      for (let row of this.selectionRows) {
        if (this.selectedRowKeys.includes(row.id)) {
          this.selectUserRows.push(row)
        }
      }
      this.selectmaterialIds = this.selectUserRows.map(row => row.username).join(',')
    },
    // 点击树节点,筛选出对应的用户
    onTypeSelect(selectedTypeIds) {
      if (selectedTypeIds[0] != null) {
        if (this.selectedTypeIds[0] !== selectedTypeIds[0]) {
          this.selectedTypeIds = [selectedTypeIds[0]];
        }
        this.loadData(1);
      }
    },
    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys;
      selectionRows.forEach(row => pushIfNotExist(this.selectionRows, row, 'id'))
    },
    onSearch() {
      this.loadData(1);
    },
    // 根据选择的id来查询用户信息
    initQueryUserByTypeId(selectedTypeIds) {
      this.loading = true
      return queryMaterialByTypeId({id: selectedTypeIds.toString()}).then((res) => {
        if (res.success) {
          this.dataSource = res.result;
          this.ipagination.total = res.result.length;
        }
      }).finally(() => {
        this.loading = false
      })
    },
    querymaterialTypeTree() {
      //update-begin-author:taoyan date:20211202 for: 异步加载部门树 https://github.com/jeecgboot/jeecg-boot/issues/3196
      this.expandedKeys = []
      this.materialTypeTree = []
      queryMaterialTypeTreeSync().then((res) => {
        if (res.success) {
          for (let i = 0; i < res.result.length; i++) {
            let temp = res.result[i]
            this.materialTypeTree.push(temp)
          }
        }
      })
    },
    onLoadMaterialType(treeNode) {
      return new Promise(resolve => {
        queryMaterialTypeTreeSync({pid: treeNode.dataRef.id}).then((res) => {
          if (res.success) {
            //判断chidlren是否为空，并修改isLeaf属性值
            if (res.result.length === 0) {
              treeNode.dataRef['isLeaf'] = true
              return;
            } else {
              treeNode.dataRef['isLeaf'] = false
              treeNode.dataRef['children'] = res.result;
            }
          }
        })
        resolve();
      });
    },
    //update-end-author:taoyan date:20211202 for: 异步加载部门树 https://github.com/jeecgboot/jeecg-boot/issues/3196
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
