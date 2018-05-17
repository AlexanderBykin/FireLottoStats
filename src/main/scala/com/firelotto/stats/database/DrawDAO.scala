package com.firelotto.stats.database

import scala.concurrent.ExecutionContext

class DrawDAO(db: DBBase)(implicit ec: ExecutionContext) {

  import scala.concurrent.Future
  import com.firelotto.stats.models.Tables._
  import slick.jdbc.PostgresProfile.api._

  def loadByDrawId(drawId: Int): Future[Seq[DrawRow]] = {
    val query = (aDrawId: Rep[Int]) ⇒ Draw.filter(r ⇒ r.drawId === aDrawId)
    db.getInstance.run(query(drawId).result)
  }

  def insertDraw(item: Draw#TableElementType): Future[Unit] = {
    db.getInstance.run(Draw += item).map(_ ⇒ ())
  }

  def deleteAllDraw(): Future[Unit] = {
    db.getInstance.run(Draw.delete).map(_ ⇒ ())
  }

}
