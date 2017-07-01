package com.reactive.http.protocol

/**
  * Created by guifeng on 2017/6/30.
  */

/**
  * Define some interactive messages between server and client.
  */
object Protocols {

  // client => server
  case class EchoCommand(id: String)

  // server => client
  case class EchoResponse(id: String)

}
