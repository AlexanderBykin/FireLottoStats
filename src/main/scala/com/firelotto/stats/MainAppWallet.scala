package com.firelotto.stats

object MainAppWallet {

  import com.firelotto.stats.utils.Configs.AppConfig
  import scala.concurrent.Await
  import scala.concurrent.duration.Duration
  import java.util.concurrent.TimeUnit
  import com.firelotto.stats.extapi.EtherScanRPC._
  import com.firelotto.stats.database._
  import com.firelotto.stats.extapi.EtherScanRequest
  import com.firelotto.stats.utils.JsonHelper
  import com.firelotto.stats.models.Tables._

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
    val drawDetails = Await.result(db.drawBaseDao.getDrawDetails(), Duration(1, TimeUnit.MINUTES))
    println(drawDetails)

    val amountRequest = 10000000L
    val notErrorTrx = "0"
    var startBlock = 0L
    var endBlock = 0L
    var sourceWallets = Set.empty[String]
    var processing = false
    var lastBlock = 0L

    drawDetails.foreach { case (drawName, drawDetail) ⇒
      startBlock = 0
      endBlock = 0
      drawDetail.wallets.foreach(drawWallet ⇒ {
        sourceWallets = Set.empty
        lastBlock = 0L
        startBlock = drawWallet.lastBlockNum + 1
        endBlock = startBlock + amountRequest
        processing = true
        while (processing) {
          val fut = EtherScanRequest.doRequest[TxListResponse]("txlist", drawWallet.wallet, appConfig.web3Provider.apiKey, startBlock, endBlock)
          val result = Await.result[TxListResponse](fut, Duration(1, TimeUnit.MINUTES))
          result.result.foreach(r ⇒ {
            if (r.isError == notErrorTrx) {
              sourceWallets += r.from
            }
            startBlock = startBlock.max(r.blockNumber.toLong)
            lastBlock = lastBlock.max(r.blockNumber.toLong)
          })
          startBlock += 1
          endBlock = startBlock + amountRequest
          processing = result.result.nonEmpty
          println(s"processing: $processing result: ${result.result.length}")
        }
        if (lastBlock > drawWallet.lastBlockNum) {
          val walletsHasTickets = sourceWallets.map(r ⇒ {
            PlayerWalletHasTicketRow(drawDetail.drawBase.id, drawWallet.id, r)
          })
          Await.result(db.playerWalletHasTicketDao.insertMany(walletsHasTickets), Duration(1, TimeUnit.MINUTES))

          val newDrawWallet = drawWallet.copy(lastBlockNum = lastBlock)
          Await.result(db.drawWalletDao.update(newDrawWallet), Duration(1, TimeUnit.MINUTES))
        }
      })
    }
  }
}