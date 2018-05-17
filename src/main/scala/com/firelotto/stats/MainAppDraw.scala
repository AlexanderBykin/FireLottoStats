package com.firelotto.stats

import java.io.File

import com.firelotto.stats.database._
import com.firelotto.stats.utils.JsonHelper
import com.firelotto.{Lottery, Web3jConfig}
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j

import scala.annotation.tailrec

object MainAppDraw {

  import java.util.concurrent.TimeUnit

  import com.firelotto.stats.models.Tables._
  import com.firelotto.stats.utils.Configs.AppConfig

  import collection.JavaConverters
  import scala.concurrent.Await
  import scala.concurrent.duration.Duration

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("No path to config")
      System.exit(1)
    }

    val configPath = args(0)

    val appConfig = JsonHelper.deserializeFromJson[AppConfig](JsonHelper.readFile(new java.io.File(configPath)))

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
    val walletOutDir = new File(appConfig.walletsOutDir)

    if (walletOutDir.isDirectory && walletOutDir.listFiles().isEmpty)
      WalletUtils.generateNewWalletFile("test", walletOutDir, true)

    val web3jConfig = new Web3jConfig
    val web3 = Web3j.build(web3jConfig.buildService(appConfig.web3Provider.url, appConfig.proxy.toJavaProxy()))

    val drawDetails = Await.result(db.drawBaseDao.getDrawDetails(), Duration(1, TimeUnit.MINUTES))

    Await.result(db.drawDao.deleteAllDraw(), Duration(1, TimeUnit.MINUTES))

    // System.exit(0)

    drawDetails.foreach { case (gameName, gameDetail) ⇒
      gameDetail.wallets.foreach(gameWallet ⇒ {

        val credentials = WalletUtils.loadCredentials("test", new File(walletOutDir.getAbsolutePath + s"/${gameWallet.wallet}.json"))
        val lottery = Lottery.load(gameWallet.wallet, web3, credentials, BigInt(2).bigInteger, BigInt(170000).bigInteger)

        val res = JavaConverters.asScalaBuffer(safeLoopLoadGames(lottery, BigInt(0), BigInt(1000)))
          .sliding(10 + gameDetail.drawBase.numbers, 10 + gameDetail.drawBase.numbers)

        res.foreach(rawDraw ⇒ {
          val numbers = rawDraw.slice(10, 10 + gameDetail.drawBase.numbers)
            .map(_.asInstanceOf[java.math.BigInteger].shortValue())
          if (numbers.count(_ == 0) != numbers.length) {
            val drawNum = rawDraw(0).toString.toInt + gameWallet.startGameIndex
            val ticketCount = rawDraw(5).toString.toShort
            val fiveNumber = if (numbers.length >= 5) Some(numbers(4)) else None
            val sixNumber = if (numbers.length >= 6) Some(numbers(5)) else None
            try {
              Await.result(db.drawDao.insertDraw(
                DrawRow(
                  0L,
                  gameDetail.drawBase.id,
                  drawNum,
                  ticketCount,
                  numbers(0),
                  numbers(1),
                  numbers(2),
                  numbers(3),
                  fiveNumber,
                  sixNumber)
              ), Duration(1, TimeUnit.MINUTES))
            } catch {
              case ex: Exception ⇒ println(ex.getMessage)
            }
          }
        })
      })
    }
  }

  @tailrec
  private def safeLoopLoadGames(contract: Lottery, offset: BigInt, count: BigInt): java.util.List[_] = {
    try {
      contract.getGames(offset.bigInteger, count.bigInteger).send()
    } catch {
      case _: Exception ⇒
        safeLoopLoadGames(contract, offset, count)
    }
  }
}
