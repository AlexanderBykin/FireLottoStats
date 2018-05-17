package com.firelotto.stats.extapi

import com.firelotto.stats.utils.JsonHelper

object EtherScanRequest {

  import EtherScanRPC._
  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
  import akka.stream.ActorMaterializer
  import akka.util.ByteString

  import scala.concurrent.{Future, Promise}
  import scala.util.{Failure, Success}

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def doRequest[RPCResponse](action: String, address: String, apiKey: String, startBlock: Long, endBlock: Long): Future[RPCResponse] = {
    val urlRequest = s"http://api.etherscan.io/api?module=account&action=$action&address=$address&startblock=$startBlock&endblock=$endBlock&sort=asc&apikey=$apiKey"
    println(urlRequest)
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = urlRequest))

    val promise = Promise[RPCResponse]()

    responseFuture
      .onComplete {
        case Success(res) ⇒
          action match {
            case "txlist" ⇒ res.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(r ⇒ promise.success(JsonHelper.deserializeFromJson[TxListResponse](r.utf8String).asInstanceOf[RPCResponse]))
            case any ⇒ Future.failed(new Exception(s"Not implemented action '$any'"))
          }
        case Failure(ex) ⇒ promise.failure(ex)
      }

    promise.future
  }

}
