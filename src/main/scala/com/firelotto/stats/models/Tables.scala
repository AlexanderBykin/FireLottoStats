package com.firelotto.stats.models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Draw.schema, DrawBase.schema, DrawWallet.schema, PlayerWallet.schema, PlayerWalletHasTicket.schema, Ticket.schema, TicketN.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Draw
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param drawId Database column draw_id SqlType(int4)
   *  @param drawNum Database column draw_num SqlType(int4)
   *  @param ticketCount Database column ticket_count SqlType(int2)
   *  @param num1 Database column num1 SqlType(int2)
   *  @param num2 Database column num2 SqlType(int2)
   *  @param num3 Database column num3 SqlType(int2)
   *  @param num4 Database column num4 SqlType(int2)
   *  @param num5 Database column num5 SqlType(int2), Default(None)
   *  @param num6 Database column num6 SqlType(int2), Default(None) */
  case class DrawRow(id: Long, drawId: Int, drawNum: Int, ticketCount: Short, num1: Short, num2: Short, num3: Short, num4: Short, num5: Option[Short] = None, num6: Option[Short] = None)
  /** GetResult implicit for fetching DrawRow objects using plain SQL queries */
  implicit def GetResultDrawRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[Short], e3: GR[Option[Short]]): GR[DrawRow] = GR{
    prs => import prs._
    DrawRow.tupled((<<[Long], <<[Int], <<[Int], <<[Short], <<[Short], <<[Short], <<[Short], <<[Short], <<?[Short], <<?[Short]))
  }
  /** Table description of table draw. Objects of this class serve as prototypes for rows in queries. */
  class Draw(_tableTag: Tag) extends profile.api.Table[DrawRow](_tableTag, "draw") {
    def * = (id, drawId, drawNum, ticketCount, num1, num2, num3, num4, num5, num6) <> (DrawRow.tupled, DrawRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(drawId), Rep.Some(drawNum), Rep.Some(ticketCount), Rep.Some(num1), Rep.Some(num2), Rep.Some(num3), Rep.Some(num4), num5, num6).shaped.<>({r=>import r._; _1.map(_=> DrawRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column draw_id SqlType(int4) */
    val drawId: Rep[Int] = column[Int]("draw_id")
    /** Database column draw_num SqlType(int4) */
    val drawNum: Rep[Int] = column[Int]("draw_num")
    /** Database column ticket_count SqlType(int2) */
    val ticketCount: Rep[Short] = column[Short]("ticket_count")
    /** Database column num1 SqlType(int2) */
    val num1: Rep[Short] = column[Short]("num1")
    /** Database column num2 SqlType(int2) */
    val num2: Rep[Short] = column[Short]("num2")
    /** Database column num3 SqlType(int2) */
    val num3: Rep[Short] = column[Short]("num3")
    /** Database column num4 SqlType(int2) */
    val num4: Rep[Short] = column[Short]("num4")
    /** Database column num5 SqlType(int2), Default(None) */
    val num5: Rep[Option[Short]] = column[Option[Short]]("num5", O.Default(None))
    /** Database column num6 SqlType(int2), Default(None) */
    val num6: Rep[Option[Short]] = column[Option[Short]]("num6", O.Default(None))

    /** Foreign key referencing DrawBase (database name draw_draw_base_fk) */
    lazy val drawBaseFk = foreignKey("draw_draw_base_fk", drawId, DrawBase)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (drawId,drawNum) (database name draw_uniq) */
    val index1 = index("draw_uniq", (drawId, drawNum), unique=true)
  }
  /** Collection-like TableQuery object for table Draw */
  lazy val Draw = new TableQuery(tag => new Draw(tag))

  /** Entity class storing rows of table DrawBase
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(15,true)
   *  @param numbers Database column numbers SqlType(int2)
   *  @param maxNumbers Database column max_numbers SqlType(int2) */
  case class DrawBaseRow(id: Int, name: String, numbers: Short, maxNumbers: Short)
  /** GetResult implicit for fetching DrawBaseRow objects using plain SQL queries */
  implicit def GetResultDrawBaseRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Short]): GR[DrawBaseRow] = GR{
    prs => import prs._
    DrawBaseRow.tupled((<<[Int], <<[String], <<[Short], <<[Short]))
  }
  /** Table description of table draw_base. Objects of this class serve as prototypes for rows in queries. */
  class DrawBase(_tableTag: Tag) extends profile.api.Table[DrawBaseRow](_tableTag, "draw_base") {
    def * = (id, name, numbers, maxNumbers) <> (DrawBaseRow.tupled, DrawBaseRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(numbers), Rep.Some(maxNumbers)).shaped.<>({r=>import r._; _1.map(_=> DrawBaseRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(15,true) */
    val name: Rep[String] = column[String]("name", O.Length(15,varying=true))
    /** Database column numbers SqlType(int2) */
    val numbers: Rep[Short] = column[Short]("numbers")
    /** Database column max_numbers SqlType(int2) */
    val maxNumbers: Rep[Short] = column[Short]("max_numbers")
  }
  /** Collection-like TableQuery object for table DrawBase */
  lazy val DrawBase = new TableQuery(tag => new DrawBase(tag))

  /** Entity class storing rows of table DrawWallet
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param drawId Database column draw_id SqlType(int4)
   *  @param wallet Database column wallet SqlType(varchar), Length(50,true)
   *  @param startGameIndex Database column start_game_index SqlType(int2)
   *  @param lastBlockNum Database column last_block_num SqlType(int8), Default(0) */
  case class DrawWalletRow(id: Int, drawId: Int, wallet: String, startGameIndex: Short, lastBlockNum: Long = 0L)
  /** GetResult implicit for fetching DrawWalletRow objects using plain SQL queries */
  implicit def GetResultDrawWalletRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Short], e3: GR[Long]): GR[DrawWalletRow] = GR{
    prs => import prs._
    DrawWalletRow.tupled((<<[Int], <<[Int], <<[String], <<[Short], <<[Long]))
  }
  /** Table description of table draw_wallet. Objects of this class serve as prototypes for rows in queries. */
  class DrawWallet(_tableTag: Tag) extends profile.api.Table[DrawWalletRow](_tableTag, "draw_wallet") {
    def * = (id, drawId, wallet, startGameIndex, lastBlockNum) <> (DrawWalletRow.tupled, DrawWalletRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(drawId), Rep.Some(wallet), Rep.Some(startGameIndex), Rep.Some(lastBlockNum)).shaped.<>({r=>import r._; _1.map(_=> DrawWalletRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column draw_id SqlType(int4) */
    val drawId: Rep[Int] = column[Int]("draw_id")
    /** Database column wallet SqlType(varchar), Length(50,true) */
    val wallet: Rep[String] = column[String]("wallet", O.Length(50,varying=true))
    /** Database column start_game_index SqlType(int2) */
    val startGameIndex: Rep[Short] = column[Short]("start_game_index")
    /** Database column last_block_num SqlType(int8), Default(0) */
    val lastBlockNum: Rep[Long] = column[Long]("last_block_num", O.Default(0L))

    /** Uniqueness Index over (drawId,wallet) (database name draw_wallet_uniq) */
    val index1 = index("draw_wallet_uniq", (drawId, wallet), unique=true)
  }
  /** Collection-like TableQuery object for table DrawWallet */
  lazy val DrawWallet = new TableQuery(tag => new DrawWallet(tag))

  /** Entity class storing rows of table PlayerWallet
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param drawId Database column draw_id SqlType(int4)
   *  @param wallet Database column wallet SqlType(varchar), Length(50,true)
   *  @param lastLoadedTicket Database column last_loaded_ticket SqlType(int4), Default(0) */
  case class PlayerWalletRow(id: Long, drawId: Int, wallet: String, lastLoadedTicket: Int = 0)
  /** GetResult implicit for fetching PlayerWalletRow objects using plain SQL queries */
  implicit def GetResultPlayerWalletRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[PlayerWalletRow] = GR{
    prs => import prs._
    PlayerWalletRow.tupled((<<[Long], <<[Int], <<[String], <<[Int]))
  }
  /** Table description of table player_wallet. Objects of this class serve as prototypes for rows in queries. */
  class PlayerWallet(_tableTag: Tag) extends profile.api.Table[PlayerWalletRow](_tableTag, "player_wallet") {
    def * = (id, drawId, wallet, lastLoadedTicket) <> (PlayerWalletRow.tupled, PlayerWalletRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(drawId), Rep.Some(wallet), Rep.Some(lastLoadedTicket)).shaped.<>({r=>import r._; _1.map(_=> PlayerWalletRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column draw_id SqlType(int4) */
    val drawId: Rep[Int] = column[Int]("draw_id")
    /** Database column wallet SqlType(varchar), Length(50,true) */
    val wallet: Rep[String] = column[String]("wallet", O.Length(50,varying=true))
    /** Database column last_loaded_ticket SqlType(int4), Default(0) */
    val lastLoadedTicket: Rep[Int] = column[Int]("last_loaded_ticket", O.Default(0))

    /** Foreign key referencing DrawBase (database name player_wallet_draw_base_fk) */
    lazy val drawBaseFk = foreignKey("player_wallet_draw_base_fk", drawId, DrawBase)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (drawId,wallet) (database name player_wallet_uniq) */
    val index1 = index("player_wallet_uniq", (drawId, wallet), unique=true)
  }
  /** Collection-like TableQuery object for table PlayerWallet */
  lazy val PlayerWallet = new TableQuery(tag => new PlayerWallet(tag))

  /** Entity class storing rows of table PlayerWalletHasTicket
   *  @param drawId Database column draw_id SqlType(int4)
   *  @param drawWalletId Database column draw_wallet_id SqlType(int4)
   *  @param wallet Database column wallet SqlType(varchar), Length(50,true) */
  case class PlayerWalletHasTicketRow(drawId: Int, drawWalletId: Int, wallet: String)
  /** GetResult implicit for fetching PlayerWalletHasTicketRow objects using plain SQL queries */
  implicit def GetResultPlayerWalletHasTicketRow(implicit e0: GR[Int], e1: GR[String]): GR[PlayerWalletHasTicketRow] = GR{
    prs => import prs._
    PlayerWalletHasTicketRow.tupled((<<[Int], <<[Int], <<[String]))
  }
  /** Table description of table player_wallet_has_ticket. Objects of this class serve as prototypes for rows in queries. */
  class PlayerWalletHasTicket(_tableTag: Tag) extends profile.api.Table[PlayerWalletHasTicketRow](_tableTag, "player_wallet_has_ticket") {
    def * = (drawId, drawWalletId, wallet) <> (PlayerWalletHasTicketRow.tupled, PlayerWalletHasTicketRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(drawId), Rep.Some(drawWalletId), Rep.Some(wallet)).shaped.<>({r=>import r._; _1.map(_=> PlayerWalletHasTicketRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column draw_id SqlType(int4) */
    val drawId: Rep[Int] = column[Int]("draw_id")
    /** Database column draw_wallet_id SqlType(int4) */
    val drawWalletId: Rep[Int] = column[Int]("draw_wallet_id")
    /** Database column wallet SqlType(varchar), Length(50,true) */
    val wallet: Rep[String] = column[String]("wallet", O.Length(50,varying=true))
  }
  /** Collection-like TableQuery object for table PlayerWalletHasTicket */
  lazy val PlayerWalletHasTicket = new TableQuery(tag => new PlayerWalletHasTicket(tag))

  /** Entity class storing rows of table Ticket
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param drawId Database column draw_id SqlType(int4)
   *  @param drawNum Database column draw_num SqlType(int4)
   *  @param drawWalletId Database column draw_wallet_id SqlType(int4)
   *  @param wallet Database column wallet SqlType(varchar), Length(50,true)
   *  @param ticketNumber Database column ticket_number SqlType(int4)
   *  @param purchaseDate Database column purchase_date SqlType(int8)
   *  @param winAmount Database column win_amount SqlType(varchar), Length(128,true)
   *  @param ticketPrice Database column ticket_price SqlType(varchar), Length(128,true)
   *  @param num1 Database column num1 SqlType(int2)
   *  @param num2 Database column num2 SqlType(int2)
   *  @param num3 Database column num3 SqlType(int2)
   *  @param num4 Database column num4 SqlType(int2)
   *  @param num5 Database column num5 SqlType(int2), Default(None)
   *  @param num6 Database column num6 SqlType(int2), Default(None) */
  case class TicketRow(id: Long, drawId: Int, drawNum: Int, drawWalletId: Int, wallet: String, ticketNumber: Int, purchaseDate: Long, winAmount: String, ticketPrice: String, num1: Short, num2: Short, num3: Short, num4: Short, num5: Option[Short] = None, num6: Option[Short] = None)
  /** GetResult implicit for fetching TicketRow objects using plain SQL queries */
  implicit def GetResultTicketRow(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Short], e4: GR[Option[Short]]): GR[TicketRow] = GR{
    prs => import prs._
    TicketRow.tupled((<<[Long], <<[Int], <<[Int], <<[Int], <<[String], <<[Int], <<[Long], <<[String], <<[String], <<[Short], <<[Short], <<[Short], <<[Short], <<?[Short], <<?[Short]))
  }
  /** Table description of table ticket. Objects of this class serve as prototypes for rows in queries. */
  class Ticket(_tableTag: Tag) extends profile.api.Table[TicketRow](_tableTag, "ticket") {
    def * = (id, drawId, drawNum, drawWalletId, wallet, ticketNumber, purchaseDate, winAmount, ticketPrice, num1, num2, num3, num4, num5, num6) <> (TicketRow.tupled, TicketRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(drawId), Rep.Some(drawNum), Rep.Some(drawWalletId), Rep.Some(wallet), Rep.Some(ticketNumber), Rep.Some(purchaseDate), Rep.Some(winAmount), Rep.Some(ticketPrice), Rep.Some(num1), Rep.Some(num2), Rep.Some(num3), Rep.Some(num4), num5, num6).shaped.<>({r=>import r._; _1.map(_=> TicketRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14, _15)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column draw_id SqlType(int4) */
    val drawId: Rep[Int] = column[Int]("draw_id")
    /** Database column draw_num SqlType(int4) */
    val drawNum: Rep[Int] = column[Int]("draw_num")
    /** Database column draw_wallet_id SqlType(int4) */
    val drawWalletId: Rep[Int] = column[Int]("draw_wallet_id")
    /** Database column wallet SqlType(varchar), Length(50,true) */
    val wallet: Rep[String] = column[String]("wallet", O.Length(50,varying=true))
    /** Database column ticket_number SqlType(int4) */
    val ticketNumber: Rep[Int] = column[Int]("ticket_number")
    /** Database column purchase_date SqlType(int8) */
    val purchaseDate: Rep[Long] = column[Long]("purchase_date")
    /** Database column win_amount SqlType(varchar), Length(128,true) */
    val winAmount: Rep[String] = column[String]("win_amount", O.Length(128,varying=true))
    /** Database column ticket_price SqlType(varchar), Length(128,true) */
    val ticketPrice: Rep[String] = column[String]("ticket_price", O.Length(128,varying=true))
    /** Database column num1 SqlType(int2) */
    val num1: Rep[Short] = column[Short]("num1")
    /** Database column num2 SqlType(int2) */
    val num2: Rep[Short] = column[Short]("num2")
    /** Database column num3 SqlType(int2) */
    val num3: Rep[Short] = column[Short]("num3")
    /** Database column num4 SqlType(int2) */
    val num4: Rep[Short] = column[Short]("num4")
    /** Database column num5 SqlType(int2), Default(None) */
    val num5: Rep[Option[Short]] = column[Option[Short]]("num5", O.Default(None))
    /** Database column num6 SqlType(int2), Default(None) */
    val num6: Rep[Option[Short]] = column[Option[Short]]("num6", O.Default(None))

    /** Foreign key referencing DrawBase (database name ticket_draw_base_fk) */
    lazy val drawBaseFk = foreignKey("ticket_draw_base_fk", drawId, DrawBase)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing DrawWallet (database name ticket_draw_wallet_fk) */
    lazy val drawWalletFk = foreignKey("ticket_draw_wallet_fk", drawWalletId, DrawWallet)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (drawId,drawNum,wallet,ticketNumber) (database name ticket_uniq) */
    val index1 = index("ticket_uniq", (drawId, drawNum, wallet, ticketNumber), unique=true)
  }
  /** Collection-like TableQuery object for table Ticket */
  lazy val Ticket = new TableQuery(tag => new Ticket(tag))

  /** Entity class storing rows of table TicketN
   *  @param id Database column id SqlType(int8), Default(None)
   *  @param drawId Database column draw_id SqlType(int4), Default(None)
   *  @param drawNum Database column draw_num SqlType(int4), Default(None)
   *  @param drawWalletId Database column draw_wallet_id SqlType(int4), Default(None)
   *  @param wallet Database column wallet SqlType(varchar), Length(50,true), Default(None)
   *  @param ticketNumber Database column ticket_number SqlType(int4), Default(None)
   *  @param purchaseDate Database column purchase_date SqlType(int8), Default(None)
   *  @param winAmount Database column win_amount SqlType(varchar), Length(128,true), Default(None)
   *  @param ticketPrice Database column ticket_price SqlType(varchar), Length(128,true), Default(None)
   *  @param num1 Database column num1 SqlType(int2), Default(None)
   *  @param num2 Database column num2 SqlType(int2), Default(None)
   *  @param num3 Database column num3 SqlType(int2), Default(None)
   *  @param num4 Database column num4 SqlType(int2), Default(None)
   *  @param num5 Database column num5 SqlType(int2), Default(None)
   *  @param num6 Database column num6 SqlType(int2), Default(None) */
  case class TicketNRow(id: Option[Long] = None, drawId: Option[Int] = None, drawNum: Option[Int] = None, drawWalletId: Option[Int] = None, wallet: Option[String] = None, ticketNumber: Option[Int] = None, purchaseDate: Option[Long] = None, winAmount: Option[String] = None, ticketPrice: Option[String] = None, num1: Option[Short] = None, num2: Option[Short] = None, num3: Option[Short] = None, num4: Option[Short] = None, num5: Option[Short] = None, num6: Option[Short] = None)
  /** GetResult implicit for fetching TicketNRow objects using plain SQL queries */
  implicit def GetResultTicketNRow(implicit e0: GR[Option[Long]], e1: GR[Option[Int]], e2: GR[Option[String]], e3: GR[Option[Short]]): GR[TicketNRow] = GR{
    prs => import prs._
    TicketNRow.tupled((<<?[Long], <<?[Int], <<?[Int], <<?[Int], <<?[String], <<?[Int], <<?[Long], <<?[String], <<?[String], <<?[Short], <<?[Short], <<?[Short], <<?[Short], <<?[Short], <<?[Short]))
  }
  /** Table description of table ticket_n. Objects of this class serve as prototypes for rows in queries. */
  class TicketN(_tableTag: Tag) extends profile.api.Table[TicketNRow](_tableTag, "ticket_n") {
    def * = (id, drawId, drawNum, drawWalletId, wallet, ticketNumber, purchaseDate, winAmount, ticketPrice, num1, num2, num3, num4, num5, num6) <> (TicketNRow.tupled, TicketNRow.unapply)

    /** Database column id SqlType(int8), Default(None) */
    val id: Rep[Option[Long]] = column[Option[Long]]("id", O.Default(None))
    /** Database column draw_id SqlType(int4), Default(None) */
    val drawId: Rep[Option[Int]] = column[Option[Int]]("draw_id", O.Default(None))
    /** Database column draw_num SqlType(int4), Default(None) */
    val drawNum: Rep[Option[Int]] = column[Option[Int]]("draw_num", O.Default(None))
    /** Database column draw_wallet_id SqlType(int4), Default(None) */
    val drawWalletId: Rep[Option[Int]] = column[Option[Int]]("draw_wallet_id", O.Default(None))
    /** Database column wallet SqlType(varchar), Length(50,true), Default(None) */
    val wallet: Rep[Option[String]] = column[Option[String]]("wallet", O.Length(50,varying=true), O.Default(None))
    /** Database column ticket_number SqlType(int4), Default(None) */
    val ticketNumber: Rep[Option[Int]] = column[Option[Int]]("ticket_number", O.Default(None))
    /** Database column purchase_date SqlType(int8), Default(None) */
    val purchaseDate: Rep[Option[Long]] = column[Option[Long]]("purchase_date", O.Default(None))
    /** Database column win_amount SqlType(varchar), Length(128,true), Default(None) */
    val winAmount: Rep[Option[String]] = column[Option[String]]("win_amount", O.Length(128,varying=true), O.Default(None))
    /** Database column ticket_price SqlType(varchar), Length(128,true), Default(None) */
    val ticketPrice: Rep[Option[String]] = column[Option[String]]("ticket_price", O.Length(128,varying=true), O.Default(None))
    /** Database column num1 SqlType(int2), Default(None) */
    val num1: Rep[Option[Short]] = column[Option[Short]]("num1", O.Default(None))
    /** Database column num2 SqlType(int2), Default(None) */
    val num2: Rep[Option[Short]] = column[Option[Short]]("num2", O.Default(None))
    /** Database column num3 SqlType(int2), Default(None) */
    val num3: Rep[Option[Short]] = column[Option[Short]]("num3", O.Default(None))
    /** Database column num4 SqlType(int2), Default(None) */
    val num4: Rep[Option[Short]] = column[Option[Short]]("num4", O.Default(None))
    /** Database column num5 SqlType(int2), Default(None) */
    val num5: Rep[Option[Short]] = column[Option[Short]]("num5", O.Default(None))
    /** Database column num6 SqlType(int2), Default(None) */
    val num6: Rep[Option[Short]] = column[Option[Short]]("num6", O.Default(None))
  }
  /** Collection-like TableQuery object for table TicketN */
  lazy val TicketN = new TableQuery(tag => new TicketN(tag))
}
