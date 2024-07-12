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
import {getAction} from '@api/manage'
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import {FormTypes} from "@/utils/JEditableTableUtil";

export default {
  name: 'CraftDetailSubTable',
  mixins: [JeecgListMixin],
  props: {
    record: {
      type: Object,
      default: null,
    }
  },
  data() {
    return {
      description: '工艺明细内嵌列表',
      disableMixinCreated: true,
      loading: false,
      dataSource: [],
      columns: [
        {
          title: '工艺编码',
          align: 'center',
          dataIndex: 'craftCode',
        },
        {
          title: '工艺名称',
          align: 'center',
          key: 'bomRouting.craftName',
          dataIndex: ['bomRouting', 'craftName'],
        },
        {
          title: '序号',
          align: 'center',
          key: 'bomRouting.serialNumber',
          dataIndex: ['bomRouting', 'serialNumber'],
        },
        {
          title: '加工单价',
          align: 'center',
          key: 'bomRouting.processCost',
          dataIndex: ['bomRouting', 'processCost'],
        }, {
          title: '料废单价',
          align: 'center',
          key: 'bomRouting.wastePrice',
          dataIndex: ['bomRouting', 'wastePrice'],
        },
        {
          title: '工费单价',
          align: 'center',
          key: 'bomRouting.wagePrice',
          dataIndex: ['bomRouting', 'wagePrice'],
        },
        {
          title: '委托数量',
          dataIndex: 'consignQuantity',
          align: 'center',
        },
        {
          title: '累计加工入库数量',
          dataIndex: 'processInQuantity',
          align: 'center',
        },
        {
          title: '累计废品入库数量',
          dataIndex: 'wasteInQuantity',
          align: 'center',
        },
        {
          title: '累计加工结算数量',
          dataIndex: 'processSettleQuantity',
          align: 'center',
        },
        {
          title: '累计废品结算数量',
          dataIndex: 'wasteSettleQuantity',
          align: 'center',
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
        listByMainId: '/bill/bill/queryCraftDetailByMainId',
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
