package com.firelotto.stats.utils

object TicketUtil {

  import com.firelotto.stats.models.Tables._
  import org.web3j.utils.Convert
  import com.firelotto.Lottery
  import scala.annotation.tailrec

  def parseFromList(drawBaseId: Int, drawWalletId: Int, playerWallet: String, drawStartGameIndex: Int, drawMinNumbers: Int, value: Seq[Any]): List[TicketRow] = {
    val rawTickets = value.sliding(5 + drawMinNumbers, 5 + drawMinNumbers).toList
    // println(s"rawTickets=${rawTickets.length}\n${rawTickets}\n$value\n")
    rawTickets.flatMap(rawTicket ⇒ {
      val numbers = rawTicket.slice(5, 10 + drawMinNumbers)
        .map(_.asInstanceOf[java.math.BigInteger].shortValue())
      // loading bad ticket data ArrayBuffer(0, 0, 0, 0, 0, 0, 0, 0, 0)
      if (numbers.count(_ == 0) != numbers.size) {
        val drawNum = rawTicket(0).asInstanceOf[java.math.BigInteger].intValue() + drawStartGameIndex
        val ticketNumber = rawTicket(1).asInstanceOf[java.math.BigInteger].intValue()
        val ticketPurchaseDate = rawTicket(2).asInstanceOf[java.math.BigInteger].longValue()
        val winAmount = Convert.fromWei(rawTicket(3).toString, Convert.Unit.ETHER).toString
        val ticketPrice = Convert.fromWei(rawTicket(4).toString, Convert.Unit.ETHER).toString
        val fiveNumber = if (numbers.length >= 5) Some(numbers(4)) else None
        val sixNumber = if (numbers.length >= 6) Some(numbers(5)) else None

        Some(TicketRow(
          0L,
          drawBaseId,
          drawNum,
          drawWalletId,
          playerWallet,
          ticketNumber,
          ticketPurchaseDate,
          if (winAmount.startsWith("-1E")) "0" else winAmount,
          ticketPrice.toString,
          numbers(0),
          numbers(1),
          numbers(2),
          numbers(3),
          fiveNumber,
          sixNumber))
      } else
        None
    })
  }

  @tailrec
  def safeLoopLoadPlayerTickets(contract: Lottery, playerWallet: String, offset: BigInt, count: BigInt): java.util.List[_] = {
    try {
      contract.getPlayerTickets(playerWallet, offset.bigInteger, count.bigInteger).send()
    } catch {
      case _: Exception ⇒
        safeLoopLoadPlayerTickets(contract, playerWallet, offset, count)
    }
  }

}
