package com.example.pocztapolskaapi.config

import org.springframework.ws.client.support.interceptor.ClientInterceptor
import org.springframework.ws.context.MessageContext
import java.lang.Exception

class SoapLoggingInterceptor : ClientInterceptor {

    override fun handleRequest(messageContext: MessageContext): Boolean {
        println("----- SOAP Request -----")
        messageContext.request.writeTo(System.out)
        println()
        return true
    }

    override fun handleResponse(messageContext: MessageContext): Boolean {
        println("----- SOAP Response -----")
        messageContext.response.writeTo(System.out)
        println()
        return true
    }

    override fun handleFault(messageContext: MessageContext): Boolean {
        println("----- SOAP Fault -----")
        messageContext.response.writeTo(System.out)
        println()
        return true
    }

    override fun afterCompletion(
        messageContext: MessageContext,
        ex: Exception?
    ) {
//        TODO("Not yet implemented")
    }
}