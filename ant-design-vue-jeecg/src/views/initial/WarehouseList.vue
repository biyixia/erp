<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">
    <!-- 查询区域 begin -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域 end -->
    <!-- 操作按钮区域 begin -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleAdd">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('仓库档案')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
<!--      &lt;!&ndash; 高级查询区域 &ndash;&gt;-->
<!--      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            <span>删除</span>
          </a-menu-item>
        </a-menu>
        <a-button>
          <span>批量操作</span>
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>
    <!-- 操作按钮区域 end -->
    <!-- table区域 begin -->
    <div>
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :loading="loading"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :expandedRowKeys="expandedRowKeys"
        :rowSelection="{selectedRowKeys, onChange: onSelectChange}"
        @expand="handleExpand"
        @change="handleTableChange"
      >
        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text,record">
          <div style="font-size: 12px;font-style: italic;">
            <span v-if="!text">无图片</span>
            <img v-else :src="getImgView(text)" :preview="record.id" alt="" style="max-width:80px;height:25px;"/>
          </div>
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            ghost
            type="primary"
            icon="download"
            size="small"
            @click="downloadFile(text)"
          >
            <span>下载</span>
          </a-button>
        </template>
        <template slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </template>
      </a-table>
    </div>
    <!-- table区域 end -->
    <!-- 表单区域 -->
    <warehouse-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>
<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import WarehouseModal from './modules/WarehouseModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import '@/assets/less/TableExpand.less'

  export default {
    name: 'WarehouseList',
    mixins: [JeecgListMixin],
    components: {
      WarehouseModal,
    },
    data() {
      return {
        description: '仓库档案列表管理页面',
        // 表头
        columns: [
          {
            title: '#',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: (t, r, index) => parseInt(index) + 1
          },
          {
            title: '仓库编码',
            align: 'center',
            dataIndex: 'code',
          },
          {
            title: '仓库名称',
            align: 'center',
            dataIndex: 'warehouseName',
          },
          {
            title: '物权归属',
            align: 'center',
            dataIndex: 'ownership_dictText'
          },
          {
            title: '允许负数',
            align: 'center',
            dataIndex: 'isNegative',
            customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: 'center',
            width:147,
            scopedSlots: { customRender: 'action' },
          },
        ],
        // 字典选项
        dictOptions: {},
        // 展开的行test
        expandedRowKeys: [],
        url: {
          list: '/initial/warehouse/list',
          delete: '/initial/warehouse/delete',
          deleteBatch: '/initial/warehouse/deleteBatch',
          exportXlsUrl: '/initial/warehouse/exportXls',
          importExcelUrl: '/initial/warehouse/importExcel',
        },
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl() {
        return window._CONFIG['domianURL'] + this.url.importExcelUrl
      }
    },
    methods: {
      initDictConfig() {
      },

      handleExpand(expanded, record) {
        this.expandedRowKeys = []
        if (expanded === true) {
          this.expandedRowKeys.push(record.id)
        }
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'code',text:'仓库编码',dictCode:''})
        fieldList.push({type:'string',value:'warehouseName',text:'仓库名称',dictCode:''})
        fieldList.push({type:'string',value:'ownership',text:'物权归属',dictCode:'ownership'})
        fieldList.push({type:'string',value:'priceType',text:'计价方式',dictCode:'price_type'})
        fieldList.push({type:'switch',value:'isNegative',text:'允许负数'})
        fieldList.push({type:'switch',value:'isCost',text:'计算成本'})
        fieldList.push({type:'switch',value:'isRop',text:'参与ROP运算'})
        fieldList.push({type:'switch',value:'isMrp',text:'参与MRP运算'})
        fieldList.push({type:'string',value:'subjectId',text:'科目名称',dictCode:''})
        fieldList.push({type:'string',value:'createBy',text:'制单人',dictCode:''})
        fieldList.push({type:'datetime',value:'createTime',text:'制单日期'})
        fieldList.push({type:'string',value:'closeBy',text:'关闭人',dictCode:''})
        fieldList.push({type:'datetime',value:'closeTime',text:'关闭日期'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';
</style>