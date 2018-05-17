package com.firelotto.stats

import java.io.File
import java.util.concurrent.TimeUnit

import com.firelotto.{Lottery, Web3jConfig}
import com.firelotto.stats.database.DB
import com.firelotto.stats.utils.Configs.AppConfig
import com.firelotto.stats.utils.{JsonHelper, TicketUtil}
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j

import scala.collection.JavaConverters
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object MainAppTest {
  def main(args: Array[String]): Unit = {
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

    doAction(appConfig, db)

    db.close()
  }

  def doAction(appConfig: AppConfig, db: DB): Unit = {
    val web3jConfig = new Web3jConfig
    val web3 = Web3j.build(web3jConfig.buildService(appConfig.web3Provider.url, appConfig.proxy.toJavaProxy()))
    val drawDetails = Await.result(db.drawBaseDao.getDrawDetails(), Duration(1, TimeUnit.MINUTES))
    val gameDetail = drawDetails("4x20")
    val gameWallet = gameDetail.wallets.filter(_.wallet.startsWith("0x202")).head
    val credentials = WalletUtils.loadCredentials("test", new File(appConfig.walletsOutDir + s"/${gameWallet.wallet}.json"))
    val lottery = Lottery.load(gameWallet.wallet, web3, credentials, BigInt(2).bigInteger, BigInt(170000).bigInteger)
    val playerWallet = "0x4b86ff37f4482c2381d8fc1fefc9f42f0a2d0e5a"
    //    val playerWallet = "0xd970208248f10f1ab40a4aee0746ea77e032814b"
    val tickets = TicketUtil.parseFromList(
      gameDetail.drawBase.id,
      gameWallet.id,
      playerWallet,
      gameWallet.startGameIndex,
      gameDetail.drawBase.numbers,
      JavaConverters.asScalaBuffer(TicketUtil.safeLoopLoadPlayerTickets(lottery, playerWallet, BigInt(0), BigInt(1000)))
    )
    tickets.filter(t ⇒ {
      t.num1 > gameDetail.drawBase.maxNumbers ||
        t.num2 > gameDetail.drawBase.maxNumbers ||
        t.num3 > gameDetail.drawBase.maxNumbers ||
        t.num4 > gameDetail.drawBase.maxNumbers ||
        t.num5.exists(_ > gameDetail.drawBase.maxNumbers) ||
        t.num6.exists(_ > gameDetail.drawBase.maxNumbers)
    }).foreach(println)
  }
}
