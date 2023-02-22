package com.example.reactivealertsmanagementservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class AlertNotFoundException: RuntimeException {
    constructor():super()

    constructor(info:String):super(info)

    constructor(info:String, cause:Throwable): super(info, cause)
}
