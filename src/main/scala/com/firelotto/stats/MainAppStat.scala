package com.firelotto.stats

import com.firelotto.stats.database.DB

object MainAppStat {

  import java.util.concurrent.TimeUnit
  import java.io.PrintWriter
  import com.firelotto.stats.models.Tables.{DrawRow, TicketRow}
  import com.firelotto.stats.utils.Configs.AppConfig
  import com.firelotto.stats.utils.JsonHelper
  import scala.concurrent.Await
  import scala.concurrent.duration.Duration

  val csvSeparator = ";"

  case class DrawResult(draws: String, tickets: String, players: String, nums1: String, nums2: String, nums3: String, nums4: String, nums5: String, nums6: String)

  case class DrawTicketNumFrequency(drawNum: Int, numbers: Seq[Int])

  case class DrawTicketNumFrequencyResult(draws: String, nums0: String, nums1: String, nums2: String, nums3: String, nums4: String, nums5: String, nums6: String)

  case class DrawUniqueTicketNumbers(draw: String, drawNum: Int, numbers: List[Short])

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
    val fpAllTickets = new PrintWriter(appConfig.statsOutDir + s"/all-ticket-result.csv")

    val header = (List("Draw", "Wallet", "TicketNum", "Win", "Price", "WinNumbers") ++
      Range.inclusive(1, 6).map(r ⇒ s"Num$r") ++
      Range.inclusive(1, 6).map(r ⇒ s"DrawNum$r")).mkString(csvSeparator)

    fpAllTickets.println(s"Game$csvSeparator" + header)

    drawDetails.foreach { case (drawName, drawDetail) ⇒
      val drawResults = Await.result(db.drawDao.loadByDrawId(drawDetail.drawBase.id), Duration(1, TimeUnit.MINUTES)).sortWith((a, b) ⇒ a.drawNum > b.drawNum)
      val drawUniquePlayers = Await.result(db.ticketDao.loadUniquePlayersCountByDrawId(drawDetail.drawBase.id), Duration(1, TimeUnit.MINUTES))

      var fp = new PrintWriter(appConfig.statsOutDir + s"/$drawName-draw-result.csv")
      val drawCsvResult = drawResults.foldLeft(DrawResult("", "", "", "", "", "", "", "", ""))((acc, e) ⇒ {
        val playersCount = drawUniquePlayers.find(_.draw == e.drawNum).map(_.players).getOrElse(0L)
        val (draw, tickets, players, num1, num2, num3, num4, num5, num6) =
          if (drawResults.head == e)
            (s"Draw${csvSeparator}${e.drawNum}",
              s"Tickets${csvSeparator}${e.ticketCount}",
              s"Players${csvSeparator}$playersCount",
              s"Num1${csvSeparator}${e.num1}",
              s"Num2${csvSeparator}${e.num2}",
              s"Num3${csvSeparator}${e.num3}",
              s"Num4${csvSeparator}${e.num4}",
              e.num5.map(r ⇒ s"Num5${csvSeparator}$r").getOrElse(""),
              e.num6.map(r ⇒ s"Num6${csvSeparator}$r").getOrElse("")
            )
          else
            (s"${acc.draws}${csvSeparator}${e.drawNum}",
              s"${acc.tickets}${csvSeparator}${e.ticketCount}",
              s"${acc.players}${csvSeparator}$playersCount",
              s"${acc.nums1}${csvSeparator}${e.num1}",
              s"${acc.nums2}${csvSeparator}${e.num2}",
              s"${acc.nums3}${csvSeparator}${e.num3}",
              s"${acc.nums4}${csvSeparator}${e.num4}",
              e.num5.map(r ⇒ s"${acc.nums5}${csvSeparator}$r").getOrElse(""),
              e.num6.map(r ⇒ s"${acc.nums6}${csvSeparator}$r").getOrElse("")
            )
        DrawResult(draw, tickets, players, num1, num2, num3, num4, num5, num6)
      })
      fp.println(drawCsvResult.draws)
      fp.println(drawCsvResult.tickets)
      fp.println(drawCsvResult.players)
      fp.println(drawCsvResult.nums1)
      fp.println(drawCsvResult.nums2)
      fp.println(drawCsvResult.nums3)
      fp.println(drawCsvResult.nums4)
      fp.println(drawCsvResult.nums5)
      fp.println(drawCsvResult.nums6)
      fp.flush()
      fp.close()

      fp = new PrintWriter(appConfig.statsOutDir + s"/$drawName-ticket-result.csv")
      val drawTickets = Await.result(db.ticketDao.loadTicketsByDrawId(drawDetail.drawBase.id), Duration(1, TimeUnit.MINUTES))
        .sortWith((a, b) ⇒ a.drawNum > b.drawNum)

      val header = (List("Draw", "Wallet", "TicketNum", "Win", "Price", "WinNumbers") ++ Range.inclusive(1, drawDetail.drawBase.numbers).map(r ⇒ s"Num$r")).mkString(csvSeparator)
      fp.println(header)

      drawTickets.foreach(ticket ⇒ {
        val drawNumbers = drawResults.find(_.drawNum == ticket.drawNum)
          .map(getDrawNumbers)
          .getOrElse(List.empty)
        val numbers = getTicketNumbers(ticket)
        val value = List(
          ticket.drawNum,
          ticket.wallet,
          ticket.ticketNumber,
          ticket.winAmount,
          ticket.ticketPrice,
          numbers.count(r ⇒ drawNumbers.contains(Math.abs(r)))
        ) ++ numbers
        fp.println(value.mkString(csvSeparator))
        val drawNumbersValue = Range.inclusive(0, 6 - drawDetail.drawBase.numbers)
          .map(_ ⇒ csvSeparator).mkString("") +
          drawNumbers.mkString(csvSeparator)
        fpAllTickets.println(s"$drawName$csvSeparator" + value.mkString(csvSeparator) + drawNumbersValue)
      })
      fp.flush()
      fp.close()

      fp = new PrintWriter(appConfig.statsOutDir + s"/$drawName-draw-ticket-num-freq.csv")

      var numFrequency = drawTickets.foldLeft(Map.empty[Int, DrawTicketNumFrequency])((acc, e) ⇒ {
        val drawNumbers = drawResults.find(_.drawNum == e.drawNum)
          .map(getDrawNumbers)
          .getOrElse(List.empty)
        val numbers = getTicketNumbers(e)
        val winNumbers = numbers.count(r ⇒ drawNumbers.contains(Math.abs(r)))
        val value = acc.getOrElse(e.drawNum, DrawTicketNumFrequency(e.drawNum, Range.inclusive(0, drawDetail.drawBase.numbers).map(_ ⇒ 0)))
        acc.updated(e.drawNum, value.copy(numbers = value.numbers.updated(winNumbers, value.numbers(winNumbers) + 1)))
      }).values.toList.sortWith((a, b) ⇒ a.drawNum > b.drawNum)

      var numFreqResult = numFrequency.foldLeft(DrawTicketNumFrequencyResult("", "", "", "", "", "", "", ""))((acc, e) ⇒ {
        val (draws, nums0, nums1, nums2, nums3, nums4, nums5, nums6) =
          if (numFrequency.head == e)
            (s"Draw$csvSeparator${e.drawNum}",
              s"Num0$csvSeparator${e.numbers(0)}",
              s"Num1$csvSeparator${e.numbers(1)}",
              s"Num2$csvSeparator${e.numbers(2)}",
              s"Num3$csvSeparator${e.numbers(3)}",
              s"Num4$csvSeparator${e.numbers(4)}",
              if (e.numbers.length >= 6) s"Num5$csvSeparator${e.numbers(5)}" else "",
              if (e.numbers.length >= 7) s"Num6$csvSeparator${e.numbers(6)}" else ""
            )
          else
            (s"${acc.draws}$csvSeparator${e.drawNum}",
              s"${acc.nums0}$csvSeparator${e.numbers(0)}",
              s"${acc.nums1}$csvSeparator${e.numbers(1)}",
              s"${acc.nums2}$csvSeparator${e.numbers(2)}",
              s"${acc.nums3}$csvSeparator${e.numbers(3)}",
              s"${acc.nums4}$csvSeparator${e.numbers(4)}",
              if (e.numbers.length >= 6) s"${acc.nums5}$csvSeparator${e.numbers(5)}" else "",
              if (e.numbers.length >= 7) s"${acc.nums6}$csvSeparator${e.numbers(6)}" else ""
            )
        DrawTicketNumFrequencyResult(draws, nums0, nums1, nums2, nums3, nums4, nums5, nums6)
      })
      fp.println(numFreqResult.draws)
      fp.println(numFreqResult.nums0)
      fp.println(numFreqResult.nums1)
      fp.println(numFreqResult.nums2)
      fp.println(numFreqResult.nums3)
      fp.println(numFreqResult.nums4)
      fp.println(numFreqResult.nums5)
      fp.println(numFreqResult.nums6)
      fp.flush()
      fp.close()

      fp = new PrintWriter(appConfig.statsOutDir + s"/$drawName-draw-unique-numbers.csv")
      val drawUniqueTicketNumbers =
        drawTickets.foldLeft(List.empty[DrawUniqueTicketNumbers])((acc, e) ⇒ {
          val numbers = getTicketNumbers(e).map(_.abs).sortWith((a, b) ⇒ a < b)
          acc.find(r ⇒ r.drawNum == e.drawNum && r.numbers.forall(n ⇒ numbers.contains(n))) match {
            case None ⇒ DrawUniqueTicketNumbers(drawName, e.drawNum, numbers) :: acc
            case Some(_) ⇒ acc
          }
        })
      drawUniqueTicketNumbers.foreach(drawUniqNumbers ⇒ {
        fp.println((List(drawUniqNumbers.draw, drawUniqNumbers.drawNum) ++ drawUniqNumbers.numbers).mkString(csvSeparator))
      })
      fp.flush()
      fp.close()

      numFrequency = drawUniqueTicketNumbers.foldLeft(Map.empty[Int, DrawTicketNumFrequency])((acc, e) ⇒ {
        val drawNumbers = drawResults.find(_.drawNum == e.drawNum)
          .map(getDrawNumbers)
          .getOrElse(List.empty)
        val winNumbers = e.numbers.count(r ⇒ drawNumbers.contains(Math.abs(r)))
        val value = acc.getOrElse(e.drawNum, DrawTicketNumFrequency(e.drawNum, Range.inclusive(0, drawDetail.drawBase.numbers).map(_ ⇒ 0)))
        acc.updated(e.drawNum, value.copy(numbers = value.numbers.updated(winNumbers, value.numbers(winNumbers) + 1)))
      }).values.toList.sortWith((a, b) ⇒ a.drawNum > b.drawNum)

      fp = new PrintWriter(appConfig.statsOutDir + s"/$drawName-draw-unique-numbers-freq.csv")
      numFreqResult = numFrequency.foldLeft(DrawTicketNumFrequencyResult("", "", "", "", "", "", "", ""))((acc, e) ⇒ {
        val (draws, nums0, nums1, nums2, nums3, nums4, nums5, nums6) =
          if (numFrequency.head == e)
            (s"Draw$csvSeparator${e.drawNum}",
              s"Num0$csvSeparator${e.numbers(0)}",
              s"Num1$csvSeparator${e.numbers(1)}",
              s"Num2$csvSeparator${e.numbers(2)}",
              s"Num3$csvSeparator${e.numbers(3)}",
              s"Num4$csvSeparator${e.numbers(4)}",
              if (e.numbers.length >= 6) s"Num5$csvSeparator${e.numbers(5)}" else "",
              if (e.numbers.length >= 7) s"Num6$csvSeparator${e.numbers(6)}" else ""
            )
          else
            (s"${acc.draws}$csvSeparator${e.drawNum}",
              s"${acc.nums0}$csvSeparator${e.numbers(0)}",
              s"${acc.nums1}$csvSeparator${e.numbers(1)}",
              s"${acc.nums2}$csvSeparator${e.numbers(2)}",
              s"${acc.nums3}$csvSeparator${e.numbers(3)}",
              s"${acc.nums4}$csvSeparator${e.numbers(4)}",
              if (e.numbers.length >= 6) s"${acc.nums5}$csvSeparator${e.numbers(5)}" else "",
              if (e.numbers.length >= 7) s"${acc.nums6}$csvSeparator${e.numbers(6)}" else ""
            )
        DrawTicketNumFrequencyResult(draws, nums0, nums1, nums2, nums3, nums4, nums5, nums6)
      })
      fp.println(numFreqResult.draws)
      fp.println(numFreqResult.nums0)
      fp.println(numFreqResult.nums1)
      fp.println(numFreqResult.nums2)
      fp.println(numFreqResult.nums3)
      fp.println(numFreqResult.nums4)
      fp.println(numFreqResult.nums5)
      fp.println(numFreqResult.nums6)
      fp.flush()
      fp.close()
    }
    fpAllTickets.flush()
    fpAllTickets.close()
  }

  def getTicketNumbers(ticket: TicketRow): List[Short] = {
    List(ticket.num1, ticket.num2, ticket.num3, ticket.num4) ++
      ticket.num5.map(r ⇒ List(r)).getOrElse(List.empty[Short]) ++
      ticket.num6.map(r ⇒ List(r)).getOrElse(List.empty[Short])
  }

  def getDrawNumbers(draw: DrawRow): List[Short] = {
    List(draw.num1, draw.num2, draw.num3, draw.num4) ++
      draw.num5.map(rr ⇒ List(rr)).getOrElse(List.empty) ++
      draw.num6.map(rr ⇒ List(rr)).getOrElse(List.empty)
  }
}
