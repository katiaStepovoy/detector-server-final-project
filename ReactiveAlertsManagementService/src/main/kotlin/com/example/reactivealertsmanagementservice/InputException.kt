package com.example.reactivealertsmanagementservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class InputException : RuntimeException {
    constructor():super()

    constructor(info:String):super(info)

    constructor(info:String, cause:Throwable): super(info, cause)
}