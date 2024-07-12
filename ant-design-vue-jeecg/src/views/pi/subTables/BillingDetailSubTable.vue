<template>
  <a-table
    rowKey="id"
    size="middle"
    bordered
    :loading="loading"
    :columns="columns"
    :dataSource="dataSource"
    :pagination="false"
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

  </a-table>
</template>

<script>
  import { getAction } from '@api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: 'BillingDetailSubTable',
    mixins: [JeecgListMixin],
    props: {
      record: {
        type: Object,
        default: null,
      }
    },
    data() {
      return {
        description: '发票明细内嵌列表',
        disableMixinCreated: true,
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '物料编码',
            align: 'center',
            dataIndex: 'materialCode',
          },
          {
            title: '物料名称',
            align: 'center',
            dataIndex: 'material.materialName',
          },
          {
            title: '规格型号',
            align: 'center',
            dataIndex: 'material.size',
          },
          {
            title: '计量单位',
            align: 'center',
            dataIndex: 'material.unit',
          },
          {
            title: '数量',
            align: 'center',
            dataIndex: 'count',
          },
          {
            title: '含税单价',
            align: 'center',
            dataIndex: 'material.priceIncludedTax',
          },
          {
            title: '含税金额',
            align: 'center',
            dataIndex: 'taxIncludedAmount',
          },
          {
            title: '税率',
            align: 'center',
            dataIndex: 'material.rate',
          },
          {
            title: '无税单价',
            align: 'center',
            dataIndex: 'material.price',
          },
          {
            title: '无税金额',
            align: 'center',
            dataIndex: 'amount',
          },
          {
            title: '税额',
            align: 'center',
            dataIndex: 'tax',
          },
          {
            title: '件装量',
            align: 'center',
            dataIndex: 'material.unitLoad',
          },
          {
            title: '件数',
            align: 'center',
            dataIndex: 'number',
          },
          {
            title: '散件',
            align: 'center',
            dataIndex: 'looseItem',
          },
          {
            title: '来源',
            align: 'center',
            dataIndex: 'source',
          },
          {
            title: '来源单号',
            align: 'center',
            dataIndex: 'sourceNumber',
          },
        ],
        url: {
          listByMainId: '/bill/bill/queryBillingDetailByMainId',
        },
      }
    },
    watch: {
      record: {
        immediate: true,
        handler() {
          if (this.record != null) {
            this.loadData(this.record)
          }
        }
      }
    },
    methods: {

      loadData(record) {
        this.loading = true
        this.dataSource = []
        getAction(this.url.listByMainId, {
          id: record.id
        }).then((res) => {
          if (res.success) {
            this.dataSource = res.result.records
          }
        }).finally(() => {
          this.loading = false
        })
      },

    },
  }
</script>

<style scoped>

</style>
