package com.firelotto.stats.database

import scala.concurrent.ExecutionContext

class PlayerWalletHasTicketDAO(db: DBBase)(implicit ec: ExecutionContext) {

  import scala.concurrent.Future
  import com.firelotto.stats.models.Tables._
  import slick.jdbc.PostgresProfile.api._

  def getPlayerWalletsByDrawIdHasNewTickets(drawId: Int): Future[Seq[PlayerWalletHasTicketRow]] = {
    val query = (aDrawId: Rep[Int]) ⇒ PlayerWalletHasTicket.filter(r ⇒ r.drawId === aDrawId)
    db.getInstance.run(query(drawId).result)
  }

  def remove(item: PlayerWalletHasTicketRow): Future[Unit] = {
    db.getInstance.run(
      PlayerWalletHasTicket.filter(r ⇒
        r.drawId === valueToConstColumn(item.drawId) &&
          r.drawWalletId === valueToConstColumn(item.drawWalletId) &&
          r.wallet === valueToConstColumn(item.wallet))
        .delete
    ).map(_ ⇒ ())
  }

  def insert(item: PlayerWalletHasTicketRow): Future[Unit] = {
    db.getInstance.run(PlayerWalletHasTicket += item).map(_ ⇒ ())
  }

  def insertMany(items: Iterable[PlayerWalletHasTicketRow]): Future[Unit] = {
    db.getInstance.run(PlayerWalletHasTicket ++= items).map(_ ⇒ ())
  }
}
