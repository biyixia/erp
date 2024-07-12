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
    name: 'BomRoutingSubTable',
    mixins: [JeecgListMixin],
    props: {
      record: {
        type: Object,
        default: null,
      }
    },
    data() {
      return {
        description: '工艺线路内嵌列表',
        disableMixinCreated: true,
        loading: false,
        dataSource: [],
        columns: [
          {
            title: '工序编码',
            align: 'center',
            dataIndex: 'craftCode',
          },
          {
            title: '工序名称',
            align: 'center',
            dataIndex: 'craftName',
          },
          {
            title: '加工单位',
            align: 'center',
            dataIndex: 'craftUnit',
          },
          {
            title: '加工工时',
            align: 'center',
            dataIndex: 'craftTime',
          },
          {
            title: '工时单价',
            align: 'center',
            dataIndex: 'craftCost',
          },
          {
            title: '加工单价',
            align: 'center',
            dataIndex: 'processCost',
          },
          {
            title: '料废单价',
            align: 'center',
            dataIndex: 'wastePrice',
          },
          {
            title: '工费单价',
            align: 'center',
            dataIndex: 'wagePrice',
          },
          {
            title: '顺序加工',
            align: 'center',
            dataIndex: 'isOrder',
            customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
          },
          {
            title: '委托加工',
            align: 'center',
            dataIndex: 'isConsign',
            customRender: (text) => (!text ? "" : (text == "Y" ? "是" : "否"))
          },
          {
            title: '序号',
            align: 'center',
            dataIndex: 'serialNumber',
          },
        ],
        url: {
          listByMainId: '/initial/bom/queryBomRoutingByBomId',
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
