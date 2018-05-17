package com.firelotto.stats

import java.io.File

import com.firelotto.{Lottery, Web3jConfig}
import com.firelotto.stats.database._
import com.firelotto.stats.utils.Configs.AppConfig
import com.firelotto.stats.utils.{JsonHelper, TicketUtil}
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j

import scala.concurrent.Future

object MainAppTicket {

  import java.util.concurrent.TimeUnit

  import collection.JavaConverters
  import scala.concurrent.Await
  import scala.concurrent.ExecutionContext.Implicits.global
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

    drawDetails.foreach { case (gameName, gameDetail) ⇒
      val playerWallets = Await.result(db.playerWalletHasTicketDao.getPlayerWalletsByDrawIdHasNewTickets(gameDetail.drawBase.id), Duration(1, TimeUnit.MINUTES))

      println(s"game=$gameName playerWallets=${playerWallets.length}")

      playerWallets.foreach(playerWallet ⇒ {

        gameDetail.wallets.filter(_.id == playerWallet.drawWalletId).foreach(gameWallet ⇒ {

          val credentials = WalletUtils.loadCredentials("test", new File(walletOutDir.getAbsolutePath + s"/${gameWallet.wallet}.json"))
          val lottery = Lottery.load(gameWallet.wallet, web3, credentials, BigInt(2).bigInteger, BigInt(170000).bigInteger)

          val tickets = TicketUtil.parseFromList(
            gameDetail.drawBase.id,
            gameWallet.id,
            playerWallet.wallet,
            gameWallet.startGameIndex,
            gameDetail.drawBase.numbers,
            JavaConverters.asScalaBuffer(TicketUtil.safeLoopLoadPlayerTickets(lottery, playerWallet.wallet, BigInt(0), BigInt(1000))).toList
          )
          var loadedTickets = 0

          tickets.foreach(newTicket ⇒ {
            val foundTicket = Await.result(db.ticketDao.findTicket(newTicket.drawId, newTicket.drawNum, newTicket.ticketNumber, newTicket.wallet), Duration(1, TimeUnit.MINUTES))

            if (foundTicket.isDefined) {
              if (foundTicket.get.purchaseDate == 0L)
                Await.result(db.ticketDao.update(foundTicket.get.copy(purchaseDate = newTicket.purchaseDate)), Duration(1, TimeUnit.MINUTES))
            } else {
              try {
                Await.result(db.ticketDao.insert(newTicket), Duration(1, TimeUnit.MINUTES))
                loadedTickets += 1
              } catch {
                case ex: Exception ⇒
                  println(s"Exception add Ticket: ${ex.getMessage}" +
                    s"\nplayerWallet=$playerWallet" +
                    s"\nnewTicket=$newTicket")
              }
            }
          })
          println(s"Loaded $loadedTickets for PlayerWallet(${playerWallet.wallet})")
          val fut = Future.sequence(Seq(
            db.playerWalletDao.incrementTickets(playerWallet.wallet, loadedTickets),
            db.playerWalletHasTicketDao.remove(playerWallet)
          ))
          Await.result(fut, Duration(1, TimeUnit.MINUTES))
        })

      })
    }
  }
}
