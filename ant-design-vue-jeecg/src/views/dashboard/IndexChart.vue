<!--改了的部分为蓝色注释的下一行，蓝色注释为原本的代码-->
<template>
  <div class="page-header-index-wide">
    <a-row :gutter="24">
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '12px' }">
        <chart-card :loading="loading" title="总销售额" :total="totalSales">
                    <a-tooltip title="统计当前销售收款的总金额" slot="action">
                      <a-icon type="info-circle-o" />
                    </a-tooltip>
<!--          <div>-->
<!--            <trend flag="up" style="margin-right: 16px;">-->
<!--              <span slot="term">周同比</span>-->
<!--              12%-->
<!--            </trend>-->
<!--            <trend flag="down">-->
<!--              <span slot="term">日同比</span>-->
<!--              11%-->
<!--            </trend>-->
<!--          </div>-->
<!--          <template slot="footer">日均销售额<span>￥ 234.56</span></template>-->
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '12px' }">
        <chart-card :loading="loading" title="销售订单量" :total="salesOrder | NumberFormat">
                    <a-tooltip title="统计所有销售订单的数量" slot="action">
                      <a-icon type="info-circle-o" />
                    </a-tooltip>
<!--          <div>-->
<!--            <mini-area/>-->
<!--          </div>-->
          <!--          <template slot="footer">日订单量<span> {{ '1234' | NumberFormat }}</span></template>-->
<!--          <template slot="footer">日销售订单量<span> {{ '1234' | NumberFormat }}</span></template>-->
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '12px' }">
        <chart-card :loading="loading" title="支付笔数" :total="payment | NumberFormat">
                    <a-tooltip title="统计所有付款单的数量" slot="action">
                      <a-icon type="info-circle-o" />
                    </a-tooltip>
<!--          <div>-->
<!--            <mini-bar :height="40"/>-->
<!--          </div>-->
<!--          <template slot="footer">转化率 <span>60%</span></template>-->
        </chart-card>
      </a-col>
      <a-col :sm="24" :md="12" :xl="6" :style="{ marginBottom: '12px' }">
        <chart-card :loading="loading" title="收款笔数" :total="collect | NumberFormat">
                    <a-tooltip title="统计所有收款单的数量" slot="action">
                      <a-icon type="info-circle-o" />
                    </a-tooltip>
<!--          <div>-->
<!--            <mini-bar :height="40"/>-->
<!--          </div>-->
<!--          <template slot="footer">转化率 <span>60%</span></template>-->
        </chart-card>
      </a-col>
    </a-row>

    <a-card :loading="loading" :bordered="false" :body-style="{padding: '0'}">
      <div class="salesCard">
        <a-tabs default-active-key="1" size="large" :tab-bar-style="{marginBottom: '24px', paddingLeft: '16px'}">
          <a-tab-pane loading="true" tab="销售额" key="1">
            <a-row>
              <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
                <bar :dataSource="barData"/>
              </a-col>
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-card>

    <a-row>
      <a-col :span="24">
        <a-card :loading="loading" :bordered="false" title="最近一周访问量统计" :style="{ marginTop: '24px' }">
          <a-row>
            <a-col :span="6">
              <head-info title="今日IP" :content="loginfo.todayIp"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class='circle-cust'>
                <a-icon slot="indicator" type="environment" style="font-size: 24px"/>
              </a-spin>
            </a-col>
            <a-col :span="6">
              <head-info title="今日访问" :content="loginfo.todayVisitCount"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class='circle-cust'>
                <a-icon slot="indicator" type="team" style="font-size: 24px"/>
              </a-spin>
            </a-col>
            <a-col :span="6">
              <head-info title="总访问量" :content="loginfo.totalVisitCount"></head-info>
            </a-col>
            <a-col :span="2">
              <a-spin class='circle-cust'>
                <a-icon slot="indicator" type="rise" style="font-size: 24px"/>
              </a-spin>
            </a-col>
          </a-row>
          <line-chart-multid :fields="visitFields" :dataSource="visitInfo"></line-chart-multid>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import ChartCard from '@/components/ChartCard'
import ACol from "ant-design-vue/es/grid/Col"
import ATooltip from "ant-design-vue/es/tooltip/Tooltip"
import MiniArea from '@/components/chart/MiniArea'
import MiniBar from '@/components/chart/MiniBar'
import MiniProgress from '@/components/chart/MiniProgress'
// import RankList from '@/components/chart/RankList'
import Bar from '@/components/chart/Bar'
import LineChartMultid from '@/components/chart/LineChartMultid'
import HeadInfo from '@/components/tools/HeadInfo.vue'

import Trend from '@/components/Trend'
import {getLoginfo, getVisitInfo} from '@/api/api'
import {getAction} from "@api/manage";

// const rankList = []
// for (let i = 0; i < 7; i++) {
//   rankList.push({
//     name: '白鹭岛 ' + (i + 1) + ' 号店',
//     total: 1234.56 - i * 100
//   })
// }
// const barData = []
// for (let i = 0; i < 12; i += 1) {
//   barData.push({
//     x: `${i + 1}月`,
//     y: Math.floor(Math.random() * 1000) + 200
//   })
// }
export default {
  name: "IndexChart",
  components: {
    ATooltip,
    ACol,
    ChartCard,
    MiniArea,
    MiniBar,
    MiniProgress,
    // RankList,
    Bar,
    Trend,
    LineChartMultid,
    HeadInfo
  },
  data() {
    return {
      loading: true,
      center: null,
      // rankList,
      barData: [],
      loginfo: {},
      totalSales: {},
      salesOrder: {},
      collect: {},
      payment: {},
      visitFields: ['ip', 'visit'],
      visitInfo: [],
      indicator: <a-icon type="loading" style="font-size: 24px" spin/>
    }
  },
  created() {
    setTimeout(() => {
      this.loading = !this.loading
    }, 1000)
    this.initSellInfo();
    this.initLogInfo();
  },
  methods: {
    initSellInfo() {
      getAction('/bill/bill/sellInfo').then(res => {
        if (res.success) {
          this.barData = res.result
        }
      })
      getAction('/bill/bill/getTotalSales').then(res => {
        if (res.success) {
          this.totalSales = '￥' + res.result
        }
      })
      getAction('/bill/bill/getSalesOrder').then(res => {
        if (res.success) {
          this.salesOrder = res.result
        }
      })
      getAction('/bill/bill/getCollect').then(res => {
        if (res.success) {
          this.collect = res.result
        }
      })
      getAction('/bill/bill/getPayment').then(res => {
        if (res.success) {
          this.payment = res.result
        }
      })
    },
    initLogInfo() {
      getLoginfo(null).then((res) => {
        if (res.success) {
          Object.keys(res.result).forEach(key => {
            res.result[key] = res.result[key] + ""
          })
          this.loginfo = res.result;
        }
      })
      getVisitInfo().then(res => {
        if (res.success) {
          this.visitInfo = res.result;
        }
      })
    },
  }
}
</script>

<style lang="less" scoped>
.circle-cust {
  position: relative;
  top: 28px;
  left: -100%;
}

.extra-wrapper {
  line-height: 55px;
  padding-right: 24px;

  .extra-item {
    display: inline-block;
    margin-right: 24px;

    a {
      margin-left: 24px;
    }
  }
}

/* 首页访问量统计 */
.head-info {
  position: relative;
  text-align: left;
  padding: 0 32px 0 0;
  min-width: 125px;

  &.center {
    text-align: center;
    padding: 0 32px;
  }

  span {
    color: rgba(0, 0, 0, .45);
    display: inline-block;
    font-size: .95rem;
    line-height: 42px;
    margin-bottom: 4px;
  }

  p {
    line-height: 42px;
    margin: 0;

    a {
      font-weight: 600;
      font-size: 1rem;
    }
  }
}
</style>