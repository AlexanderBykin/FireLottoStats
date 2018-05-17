package com.firelotto.stats.database

import scala.concurrent.ExecutionContext

class DrawBaseDAO(db: DBBase)(implicit ec: ExecutionContext) {

  import scala.concurrent.Future
  import com.firelotto.stats.models.Tables._
  import slick.jdbc.PostgresProfile.api._

  case class DrawDetails(drawBase: DrawBaseRow, wallets: List[DrawWalletRow])

  def getDrawDetails(): Future[Map[String, DrawDetails]] = {
    val query = DrawWallet join DrawBase on ((a, b) ⇒ a.drawId === b.id)
    //println("getDrawDetails: " + query.result.statements.mkString(" "))
    db.getInstance.run(query.result).flatMap(r ⇒
      Future.successful(
        r.foldLeft(Map.empty[String, DrawDetails])((acc, e) ⇒ {
          val value = acc.getOrElse(e._2.name, DrawDetails(e._2, List.empty))
          val newValue = value.copy(wallets = e._1 :: value.wallets)
          acc.updated(e._2.name, newValue)
        })
      )
    )
  }
}
