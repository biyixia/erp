<template>
  <a-card class="j-inner-table-wrapper" :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleAdd">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('销售订单')">导出</a-button>
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
          <a-menu-item key="2" v-has="'so:check'" @click="batchSetStatus(1)"><a-icon type="check"/>审核</a-menu-item>
          <a-menu-item key="3" v-has="'so:check'" @click="batchSetStatus(0)"><a-icon type="stop"/>反审核</a-menu-item>
        </a-menu>
        <a-button>
          <span>批量操作</span>
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>
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
        <template slot="expandedRowRender" slot-scope="record">
          <a-tabs tabPosition="top">
            <a-tab-pane tab="标的明细" key="billingDetail" forceRender>
              <billing-detail-sub-table :record="record"/>
            </a-tab-pane>
            <a-tab-pane tab="收款计划" key="collectPayPlan" forceRender>
              <collect-pay-plan-sub-table :record="record"/>
            </a-tab-pane>
          </a-tabs>
        </template>
        <!-- 内嵌table区域 end -->
        <template slot="customRenderStatus" slot-scope="status">
          <a-tag v-if="status === '0'" color="red">未审核</a-tag>
          <a-tag v-if="status === '1'" color="green">已审核</a-tag>
          <a-tag v-if="status === '2'" color="cyan">部分出库</a-tag>
          <a-tag v-if="status === '3'" color="blue">全部出库</a-tag>
        </template>
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
    <s-order-modal ref="modalForm" @ok="modalFormOk"/>
  </a-card>
</template>

<script>

  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import sOrderModal from './modules/sOrderModal'
  import BillingDetailSubTable from './subTables/BillingDetailSubTable'
  import CollectPayPlanSubTable from './subTables/CollectPayPlanSubTable'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import '@/assets/less/TableExpand.less'
  import {getAction} from "@api/manage";

  export default {
    name: 'sOrder',
    mixins: [JeecgListMixin],
    components: {
      sOrderModal,
      BillingDetailSubTable,
      CollectPayPlanSubTable,
    },
    data() {
      return {
        description: '销售订单管理页面',
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
            title: '单据编码',
            align: 'center',
            dataIndex: 'code',
          },
          {
            title: '单据日期',
            align: 'center',
            dataIndex: 'billDate',
          },
          {
            title: '人员名称',
            align: 'center',
            dataIndex: 'personName_dictText'
          },
          {
            title: '部门名称',
            align: 'center',
            dataIndex: 'departmentName_dictText'
          },
          {
            title: '客户名称',
            align: 'center',
            key:'merchant.name',
            dataIndex: ['merchant','name'],
          },
          {
            title: '收款政策',
            align: 'center',
            dataIndex: 'paymentStyle_dictText'
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark',
          },
          {
            title: '状态', dataIndex: 'status', width: 80, align: "center",
            scopedSlots: {customRender: 'customRenderStatus'}
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
          list: '/bill/bill/list/so',
          delete: '/bill/bill/delete',
          deleteBatch: '/bill/bill/deleteBatch',
          exportXlsUrl: '/bill/bill/exportXls/so',
          importExcelUrl: '/bill/bill/importExcel',
          batchSetStatusUrl: "/bill/bill/batchSetStatus"
        },
        superFieldList:[],
      }
    },
    created() {
      this.loading = true;
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
        fieldList.push({type:'string',value:'code',text:'单据编码',dictCode:''})
        fieldList.push({type:'date',value:'billDate',text:'单据日期'})
        fieldList.push({type:'sel_user',value:'personName',text:'人员名称'})
        fieldList.push({type:'sel_depart',value:'departmentName',text:'部门名称'})
        fieldList.push({type:'popup',value:'clientName',text:'客商名称', popup:{code:'byx_merchant',field:'name',orgFields:'name',destFields:'client_name'}})
        fieldList.push({type:'date',value:'estimatedDeliveryDate',text:'预计发货日期'})
        fieldList.push({type:'string',value:'paymentStyle',text:'付款政策',dictCode:'payment_style'})
        fieldList.push({type:'string',value:'remark',text:'备注',dictCode:''})
        fieldList.push({type:'string',value:'billMode',text:'单据模式',dictCode:'bill_mode'})
        fieldList.push({type:'string',value:'createBy',text:'创建人',dictCode:''})
        fieldList.push({type:'datetime',value:'createTime',text:'创建日期'})
        fieldList.push({type:'string',value:'updateBy',text:'更新人',dictCode:''})
        fieldList.push({type:'datetime',value:'updateTime',text:'更新日期'})
        //物料信息
        fieldList.push({type:'string',value:'materialCode',text:'物料编码',dictCode:''})
        fieldList.push({type:'string',value:'materialName',text:'物料名称',dictCode:''})
        fieldList.push({type:'string',value:'size',text:'规格型号',dictCode:''})
        fieldList.push({type:'string',value:'owningName',text:'所属分类名称'})
        fieldList.push({type:'string',value:'unit',text:'计量单位',dictCode:'unit'})
        fieldList.push({type:'double',value:'priceIncludedTax',text:'含税单价',dictCode:''})
        fieldList.push({type:'double',value:'taxIncludedAmount',text:'含税金额',dictCode:''})
        fieldList.push({type:'double',value:'unitLoad',text:'件装量',dictCode:''})
        fieldList.push({type:'double',value:'rate',text:'税率',dictCode:''})
        fieldList.push({type:'double',value:'amount',text:'无税金额',dictCode:''})
        fieldList.push({type:'double',value:'tax',text:'税额',dictCode:''})
        fieldList.push({type:'double',value:'price',text:'无税单价',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style lang="less" scoped>
  @import '~@assets/less/common.less';
</style>