package com.firelotto.stats.database

import scala.concurrent.ExecutionContext

class DrawWalletDAO(db: DBBase)(implicit ec: ExecutionContext) {

  import scala.concurrent.Future
  import com.firelotto.stats.models.Tables._
  import slick.jdbc.PostgresProfile.api._

  def update(item: DrawWallet#TableElementType): Future[Unit] = {
    db.getInstance.run(DrawWallet.filter(_.id === item.id).update(item)).map(_ ⇒ ())
  }
}
