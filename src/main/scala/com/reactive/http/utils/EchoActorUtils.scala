package com.reactive.http.utils

import com.reactive.http.protocol.Protocols.{EchoCommand, EchoResponse}

/**
  * Created by guifeng on 2017/7/1.
  */
trait EchoActorUtils {

  def handEchoMsg(request: EchoCommand) = {
    EchoResponse(request.id)
  }
}
