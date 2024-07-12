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
      <a-button type="primary" icon="download" @click="handleExportXls('客商管理')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"-->
<!--                @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
<!--      &lt;!&ndash; 高级查询区域 &ndash;&gt;-->
<!--      <j-super-query :fieldList="superFieldList" ref="superQueryModal"-->
<!--                     @handleSuperQuery="handleSuperQuery"></j-super-query>-->
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

<!--      <a-alert type="info" showIcon style="margin-bottom: 16px;">-->
<!--        <template slot="message">-->
<!--          <span>已选择</span>-->
<!--          <a style="font-weight: 600;padding: 0 4px;">{{ selectedRowKeys.length }}</a>-->
<!--          <span>项</span>-->
<!--          <a style="margin-left: 24px" @click="onClearSelected">清空</a>-->
<!--        </template>-->
<!--      </a-alert>-->

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

        <!-- 内嵌table区域 begin -->
        <template slot="expandedRowRender" slot-scope="record">
          <a-tabs tabPosition="top">
            <a-tab-pane tab="客户补充信息" v-if="record.isCustomer==='Y'" key="customer" forceRender>
              <customer-sub-table :record="record"/>
            </a-tab-pane>
            <a-tab-pane tab="供应商补充信息" v-if="record.isSupplier==='Y'" key="supplier" forceRender>
              <supplier-sub-table :record="record"/>
            </a-tab-pane>
          </a-tabs>
        </template>
        <!-- 内嵌table区域 end -->

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>

        <template slot="imgSlot" slot-scope="text,record">
          <div style="font-size: 12px;font-style: italic;">
            <span v-if="!text">无图片</span>
            <img v-else :src="getImgView(text)" :preview="record.id" alt="" style="max-width:80px;height:25px;"/>
          </div>
        </template>

        <template slot="pcaSlot" slot-scope="text">
          <div>{{ getPcaText(text) }}</div>
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
            <a class="ant-dropdown-link">更多
              <a-icon type="down"/>
            </a>
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
    <merchant-modal ref="modalForm" @ok="modalFormOk"/>

  </a-card>
</template>

<script>

import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import MerchantModal from './modules/MerchantModal'
import CustomerSubTable from './subTables/CustomerSubTable'
import SupplierSubTable from './subTables/SupplierSubTable'
import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
import Area from '@/components/_util/Area'
import '@/assets/less/TableExpand.less'

export default {
  name: 'MerchantList',
  mixins: [JeecgListMixin],
  components: {
    MerchantModal,
    CustomerSubTable,
    SupplierSubTable,
  },
  data() {
    return {
      isorter:{
        column: 'sorter',
        order: 'desc',
      },
      description: '客商管理列表管理页面',
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
          title: '客商编号',
          align: 'center',
          dataIndex: 'code',
        },
        {
          title: '客商名称',
          align: 'center',
          dataIndex: 'name',
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'contacts',
        },
        {
          title: '联系电话',
          align: 'center',
          dataIndex: 'phoneNum',
        },
        {
          title: '所属地区',
          align: 'center',
          dataIndex: 'area',
          scopedSlots: {customRender: 'pcaSlot'}
        },
        {
          title: '详细地址',
          align: 'center',
          dataIndex: 'address',
        },
        {
          title: '是否客户',
          align: 'center',
          dataIndex: 'isCustomer',
          customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
        },
        {
          title: '是否供应商',
          align: 'center',
          dataIndex: 'isSupplier',
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
      // 字典选项
      dictOptions: {},
      // 展开的行test
      expandedRowKeys: [],
      url: {
        list: '/initial/merchant/list',
        delete: '/initial/merchant/delete',
        deleteBatch: '/initial/merchant/deleteBatch',
        exportXlsUrl: '/initial/merchant/exportXls',
        importExcelUrl: '/initial/merchant/importExcel',
      },
      pcaData: '',
      superFieldList: [],
    }
  },
  created() {
    this.pcaData = new Area()
    this.getSuperFieldList();
  },
  computed: {
    importExcelUrl() {
      return window._CONFIG['domianURL'] + this.url.importExcelUrl
    }
  },
  methods: {
    getPcaText(code) {
      return this.pcaData.getText(code);
    },
    initDictConfig() {
    },

    handleExpand(expanded, record) {
      this.expandedRowKeys = []
      if (expanded === true) {
        this.expandedRowKeys.push(record.id)
      }
    },
    getSuperFieldList() {
      let fieldList = [];
      fieldList.push({type: 'string', value: 'code', text: '客商编号', dictCode: ''})
      fieldList.push({type: 'string', value: 'name', text: '客商名称', dictCode: ''})
      fieldList.push({type: 'string', value: 'contacts', text: '联系人', dictCode: ''})
      fieldList.push({type: 'string', value: 'phoneNum', text: '联系电话', dictCode: ''})
      fieldList.push({type: 'pca', value: 'area', text: '所属地区'})
      fieldList.push({type: 'string', value: 'address', text: '详细地址', dictCode: ''})
      fieldList.push({type: 'string', value: 'creditNum', text: '统一信用证号', dictCode: ''})
      fieldList.push({type: 'string', value: 'currency', text: '币别', dictCode: 'currency'})
      fieldList.push({type: 'string', value: 'qq', text: 'QQ', dictCode: ''})
      fieldList.push({type: 'string', value: 'wechat', text: '微信', dictCode: ''})
      fieldList.push({type: 'string', value: 'email', text: '电子邮箱', dictCode: ''})
      fieldList.push({type: 'string', value: 'fax', text: '传真', dictCode: ''})
      fieldList.push({type: 'string', value: 'telephone', text: '手机号码', dictCode: ''})
      fieldList.push({type: 'string', value: 'bankType', text: '所属银行', dictCode: 'bank_type'})
      fieldList.push({type: 'string', value: 'bankName', text: '开户行', dictCode: ''})
      fieldList.push({type: 'string', value: 'bankNum', text: '联行号', dictCode: ''})
      fieldList.push({type: 'string', value: 'bankAccount', text: '银行账号', dictCode: ''})
      fieldList.push({type: 'string', value: 'taxNum', text: '纳税人识别号', dictCode: ''})
      fieldList.push({type: 'double', value: 'taxRate', text: '税率', dictCode: ''})
      fieldList.push({type: 'string', value: 'sort', text: '排序', dictCode: ''})
      fieldList.push({type: 'switch', value: 'isCustomer', text: '是否客户'})
      fieldList.push({type: 'switch', value: 'isSupplier', text: '是否供应商'})
      fieldList.push({type: 'Text', value: 'description', text: '备注', dictCode: ''})
      fieldList.push({type: 'string', value: 'createBy', text: '创建人', dictCode: ''})
      fieldList.push({type: 'datetime', value: 'createTime', text: '创建日期'})
      fieldList.push({type: 'string', value: 'updateBy', text: '更新人', dictCode: ''})
      fieldList.push({type: 'datetime', value: 'updateTime', text: '更新日期'})
      this.superFieldList = fieldList
    }
  }
}
</script>
<style lang="less" scoped>
@import '~@assets/less/common.less';
</style>