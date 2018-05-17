package com.firelotto.stats

import com.firelotto.stats.database.DB
import com.firelotto.stats.utils.Configs.AppConfig
import com.firelotto.stats.utils.JsonHelper

object MainApp {
  def main(args: Array[String]): Unit = {
    /**
      * 1. Run AppDraw
      * 2. Run AppWallet
      * 3. Run AppTicket
      * 4. Run AppStat
    */

    if (args.isEmpty) {
      println("No path to config")
      System.exit(1)
    }

    val appConfig = JsonHelper.deserializeFromJson[AppConfig](JsonHelper.readFile(new java.io.File(args(0))))

    val db = new DB(
      url = appConfig.db.url,
      user = appConfig.db.username,
      password = appConfig.db.password,
      driver = appConfig.db.driver
    )


    println("stage 1 Loading draw results")
    MainAppDraw.doAction(appConfig, db)
    println("stage 1 complete")

    println("stage 2 Loading new player wallets")
    MainAppWallet.doAction(appConfig, db)
    println("stage 2 complete")

    println("stage 3 Loading new player tickets")
    MainAppTicket.doAction(appConfig, db)
    println("stage 3 complete")

    println("stage 4 Creating statistics")
    MainAppStat.doAction(appConfig, db)
    println("stage 4 complete")

    db.close()

    System.exit(0)
  }
}
