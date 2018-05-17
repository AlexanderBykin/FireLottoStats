package com.firelotto.stats.database

import scala.concurrent.ExecutionContext

class PlayerWalletDAO(db: DBBase)(implicit ec: ExecutionContext) {

  import scala.concurrent.Future
  import com.firelotto.stats.models.Tables._
  import slick.jdbc.PostgresProfile.api._

  def getPlayerWalletsByDrawId(drawId: Int): Future[Seq[PlayerWalletRow]] = {
    val query = (aDrawId: Rep[Int]) ⇒ PlayerWallet.filter(r ⇒ r.drawId === aDrawId)
    db.getInstance.run(query(drawId).result)
  }

  def update(item: PlayerWallet#TableElementType): Future[Unit] = {
    db.getInstance.run(PlayerWallet.filter(_.id === item.id).update(item)).map(_ ⇒ ())
  }

  def insert(item: PlayerWallet#TableElementType): Future[Unit] = {
    db.getInstance.run(PlayerWallet += item).map(_ ⇒ ())
  }

  def incrementTickets(wallet: String, value: Int): Future[Unit] = {
    db.getInstance.run(
      sqlu"""update player_wallet
             set last_loaded_ticket = last_loaded_ticket + $value
             where wallet=$wallet"""
    ).map(_ ⇒ ())
  }
}
