package com.firelotto.stats.database

import scala.concurrent.ExecutionContext

class TicketDAO(db: DBBase)(implicit ec: ExecutionContext) {

  import scala.concurrent.Future
  import com.firelotto.stats.models.Tables._
  import slick.jdbc.PostgresProfile.api._

  def checkHasTicket(drawId: Int, drawNum: Int, ticketNumber: Int, wallet: String): Future[Boolean] = {
    db.getInstance.run(
      Ticket.filter(r ⇒
        r.drawId === valueToConstColumn(drawId) &&
          r.drawNum === valueToConstColumn(drawNum) &&
          r.ticketNumber === valueToConstColumn(ticketNumber) &&
          r.wallet === valueToConstColumn(wallet)
      ).length.result
    ).flatMap(r ⇒ Future.successful(r > 0))
  }

  def insert(item: TicketRow): Future[Unit] = {
    db.getInstance.run(Ticket += item).map(_ ⇒ ())
  }

  def update(item: TicketRow): Future[Unit] = {
    db.getInstance.run(Ticket.filter(_.id === item.id).update(item)).map(_ ⇒ ())
  }

  def findTicket(drawId: Int, drawNum: Int, ticketNumber: Int, wallet: String): Future[Option[TicketRow]] = {
    db.getInstance.run(
      Ticket.filter(r ⇒
        r.drawId === valueToConstColumn(drawId) &&
          r.drawNum === valueToConstColumn(drawNum) &&
          r.ticketNumber === valueToConstColumn(ticketNumber) &&
          r.wallet === valueToConstColumn(wallet)
      ).result
    ).flatMap(r ⇒ Future.successful(r.headOption))
  }

  case class DrawUniquePlayers(draw: Long, players: Long)

  def loadUniquePlayersCountByDrawId(drawId: Long): Future[Seq[DrawUniquePlayers]] = {
    val query =
      sql"""select q.draw_num, count(q.draw_num)
        from (
        SELECT draw_num, wallet
        FROM ticket
        WHERE draw_id = $drawId
        group by draw_num, wallet
        order by draw_num) q
        group by q.draw_num""".as[(Long, Long)]

    db.getInstance.run(query).flatMap(r ⇒ Future.successful(r.map(e ⇒ DrawUniquePlayers(e._1, e._2))))
  }

  def loadTicketsByDrawId(drawId: Int): Future[Seq[TicketRow]] = {
    val query = (aDrawId: Rep[Int]) ⇒ Ticket.filter(_.drawId === aDrawId)
    db.getInstance.run(query(drawId).result)
  }
}
