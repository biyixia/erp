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
    name: 'PayDetailSubTable',
    mixins: [JeecgListMixin],
    props: {
      record: {
        type: Object,
        default: null,
      }
    },
    data() {
      return {
        description: '付款明细内嵌列表',
        disableMixinCreated: true,
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '客商名称',
            align: 'center',
            dataIndex: 'clientName',
          },
          {
            title: '付款类型',
            align: 'center',
            dataIndex: 'payType_dictText'
          },
          {
            title: '申请金额',
            align: 'center',
            dataIndex: 'applyAmount',
          },
          {
            title: '预计付款日期',
            align: 'center',
            dataIndex: 'expectedPayDate',
          },
          {
            title: '单据应付金额',
            align: 'center',
            dataIndex: 'payAmount',
          },
          {
            title: '备注',
            align: 'center',
            dataIndex: 'remark',
          },
          {
            title: '来源',
            align: 'center',
            dataIndex: 'sourse',
          },
          {
            title: '来源单号',
            align: 'center',
            dataIndex: 'sourseNum',
          },
        ],
        url: {
          listByMainId: '/bill/bill/queryPayDetailByMainId',
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
