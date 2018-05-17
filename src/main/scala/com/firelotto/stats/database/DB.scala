package com.firelotto.stats.database

import slick.jdbc.PostgresProfile.api._

trait DBBase {
  def getInstance: Database
}

class DB(url: String,
         user: String,
         password: String,
         driver: String,
         properties: java.util.Properties = null) extends DBBase {

  private val ec = scala.concurrent.ExecutionContext.Implicits.global
  private val dbInstance = Database.forURL(url = url, user = user, password = password, prop = properties, driver = driver, keepAliveConnection = true)

  val drawBaseDao = new DrawBaseDAO(this)(ec)
  val drawDao = new DrawDAO(this)(ec)
  val drawWalletDao = new DrawWalletDAO(this)(ec)
  val playerWalletDao = new PlayerWalletDAO(this)(ec)
  val playerWalletHasTicketDao = new PlayerWalletHasTicketDAO(this)(ec)
  val ticketDao = new TicketDAO(this)(ec)

  def close(): Unit = {
    dbInstance.close
  }

  override def getInstance: Database = dbInstance
}
