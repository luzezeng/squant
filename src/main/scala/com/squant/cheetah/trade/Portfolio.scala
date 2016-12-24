package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.{FAILED, OrderState, OrderStyle, SUCCESS, UNKNOW}

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(startingCash: Double) {

  private val porfolioMetric: PortfolioMetric = new PortfolioMetric(startingCash)

  private val records = mutable.Map[LocalDateTime, Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  //key是股票代码code
  var positions: Map[String, Position] = mutable.Map[String, Position]() //记录账户当前持仓情况

  def longOrder(code: String, amount: Int, style: OrderStyle, ts: LocalDateTime): OrderState = {
    if (porfolioMetric.availableCash > amount * style.price) {
      Position.add(positions.get(code).get, Position.mk(code, amount, style.price(), ts))
      //TODO update porfolio
    }
    UNKNOW
  }

  def shortOrder(code: String, amount: Int, style: OrderStyle, ts: LocalDateTime): OrderState = {
    positions.contains(code) match {
      case true => {
        val position = positions.get(code).get
        if (position.totalAmount < amount) {
          return FAILED
        } else if (position.totalAmount == amount) {
          positions - code
          //TODO update porfolio
        } else {
          Position.sub(positions.get(code).get, positions.get(code).get)
          //TODO update porfolio
        }
        SUCCESS
      }
      case false => FAILED
    }
  }
}