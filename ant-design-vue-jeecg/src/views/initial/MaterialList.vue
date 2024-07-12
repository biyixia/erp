<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('物料管理')">导出</a-button>
<!--      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">-->
<!--        <a-button type="primary" icon="import">导入</a-button>-->
<!--      </a-upload>-->
<!--      &lt;!&ndash; 高级查询区域 &ndash;&gt;-->
<!--      <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
<!--    <div>-->
<!--      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">-->
<!--        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项-->
<!--        <a style="margin-left: 24px" @click="onClearSelected">清空</a>-->
<!--      </div>-->

      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
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
                <a @click="handleDetail(record)">详情</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <material-modal ref="modalForm" @ok="modalFormOk"></material-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import MaterialModal from './modules/MaterialModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'

  export default {
    name: 'MaterialList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      MaterialModal
    },
    data () {
      return {
        description: '物料管理管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
          },
          {
            title:'物料编码',
            align:"center",
            dataIndex: 'code'
          },
          {
            title:'物料名称',
            align:"center",
            dataIndex: 'materialName'
          },
          {
            title:'规格型号',
            align:"center",
            dataIndex: 'size'
          },
          {
            title:'所属分类名称',
            align:"center",
            dataIndex: 'owningCode_dictText'
          },
          {
            title:'计量单位',
            align:"center",
            dataIndex: 'unit_dictText'
          },
          {
            title:'含税单价',
            align:"center",
            dataIndex: 'priceIncludedTax'
          },
          {
            title:'税率',
            align:"center",
            dataIndex: 'rate'
          },
          {
            title:'无税单价',
            align:"center",
            dataIndex: 'price'
          },
          {
            title:'件装量',
            align:"center",
            dataIndex: 'unitLoad'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/initial/material/list",
          delete: "/initial/material/delete",
          deleteBatch: "/initial/material/deleteBatch",
          exportXlsUrl: "/initial/material/exportXls",
          importExcelUrl: "initial/material/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
      this.$set(this.dictOptions, 'isInContract', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isOutContract', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isReceiveOrder', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isBuyorderIn', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isBuyorderOut', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isBuyorderReceive', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'completeSetIn', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isContractProduct', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isProductIn', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isApply', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isApplyIn', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isScattered', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isPlanProduct', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isPlanIn', [{text:'是',value:'Y'},{text:'否',value:'N'}])
      this.$set(this.dictOptions, 'isSpecial', [{text:'是',value:'Y'},{text:'否',value:'N'}])
    this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    methods: {
      initDictConfig(){
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'code',text:'物料编码',dictCode:''})
        fieldList.push({type:'string',value:'materialName',text:'物料名称',dictCode:''})
        fieldList.push({type:'string',value:'size',text:'规格型号',dictCode:''})
        fieldList.push({type:'string',value:'owningCode',text:'所属分类'})
        fieldList.push({type:'string',value:'unit',text:'计量单位',dictCode:'unit'})
        fieldList.push({type:'double',value:'priceIncludedTax',text:'含税单价',dictCode:''})
        fieldList.push({type:'double',value:'taxIncludedAmount',text:'含税金额',dictCode:''})
        fieldList.push({type:'double',value:'unitLoad',text:'件装量',dictCode:''})
        fieldList.push({type:'double',value:'rate',text:'税率',dictCode:''})
        fieldList.push({type:'double',value:'amount',text:'无税金额',dictCode:''})
        fieldList.push({type:'double',value:'tax',text:'税额',dictCode:''})
        fieldList.push({type:'double',value:'price',text:'无税单价',dictCode:''})
        fieldList.push({type:'switch',value:'isInContract',text:'采购必有合同'})
        fieldList.push({type:'switch',value:'isOutContract',text:'销售必有合同'})
        fieldList.push({type:'switch',value:'isReceiveOrder',text:'领用必有订单'})
        fieldList.push({type:'switch',value:'isBuyorderIn',text:'允许超采购合同入库'})
        fieldList.push({type:'switch',value:'isBuyorderOut',text:'允许超采购合同出库'})
        fieldList.push({type:'switch',value:'isBuyorderReceive',text:'允许超生产订单领用'})
        fieldList.push({type:'switch',value:'completeSetIn',text:'齐套入库'})
        fieldList.push({type:'switch',value:'isContractProduct',text:'允许超合同下达生产'})
        fieldList.push({type:'switch',value:'isProductIn',text:'允许超生产订单入库'})
        fieldList.push({type:'switch',value:'isApply',text:'物料必须申请'})
        fieldList.push({type:'switch',value:'isApplyIn',text:'允许超申请入库'})
        fieldList.push({type:'switch',value:'isScattered',text:'允许零星采购'})
        fieldList.push({type:'switch',value:'isPlanProduct',text:'允许超计划生产'})
        fieldList.push({type:'switch',value:'isPlanIn',text:'允许超计划采购'})
        fieldList.push({type:'switch',value:'isSpecial',text:'专用件'})
        fieldList.push({type:'string',value:'planType',text:'计划类型',dictCode:'plan_type'})
        fieldList.push({type:'double',value:'leadDays',text:'前置天数',dictCode:''})
        fieldList.push({type:'string',value:'planLowLeverCode',text:'计划低阶码',dictCode:''})
        fieldList.push({type:'double',value:'maximumDailySupply',text:'日最大供应量',dictCode:''})
        fieldList.push({type:'double',value:'minimumSupply',text:'最小供应量',dictCode:''})
        fieldList.push({type:'string',value:'supplyParameter',text:'供应参数',dictCode:''})
        fieldList.push({type:'double',value:'safeStock',text:'安全库存',dictCode:''})
        fieldList.push({type:'double',value:'maximumStock',text:'最高库存',dictCode:''})
        fieldList.push({type:'double',value:'minimumStock',text:'最低库存',dictCode:''})
        fieldList.push({type:'string',value:'createBy',text:'制单人',dictCode:''})
        fieldList.push({type:'datetime',value:'createTime',text:'制单日期'})
        fieldList.push({type:'string',value:'updateBy',text:'更新人',dictCode:''})
        fieldList.push({type:'datetime',value:'updateTime',text:'更新日期'})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>