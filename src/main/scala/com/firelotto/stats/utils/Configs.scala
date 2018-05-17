package com.firelotto.stats.utils

import java.net.InetSocketAddress

object Configs {

  import java.net.Proxy

  case class AppConfig(statsOutDir: String,
                       walletsOutDir: String,
                       web3Provider: Web3ProviderConfig,
                       db: DbConfiguration,
                       proxy: ProxyConfig)

  case class Web3ProviderConfig(url: String,
                                apiKey: String)

  /**
    *
    * @param enabled   Proxy usage enable/disable
    * @param proxyType Possible values [HTTP, SOCKS]
    * @param host      Proxy host
    * @param port      Proxy port
    */
  case class ProxyConfig(enabled: Boolean,
                         proxyType: String,
                         host: String,
                         port: Int) {
    def isValid(): Boolean = {
      Array("HTTP", "SOCKS").contains(proxyType.toUpperCase) &&
        enabled &&
        host.nonEmpty &&
        port > 0
    }

    def toJavaProxy(): Proxy = {
      val prxyType = proxyType.toUpperCase match {
        case "HTTP" ⇒ Proxy.Type.HTTP
        case "SOCKS" ⇒ Proxy.Type.SOCKS
        case _ ⇒ Proxy.Type.DIRECT
      }
      if (isValid())
        new Proxy(prxyType, new InetSocketAddress(host, port))
      else
        null.asInstanceOf[Proxy]
    }
  }

  case class DbConfiguration(url: String,
                             username: String,
                             password: String,
                             driver: String,
                             logSQL: Boolean)

}
