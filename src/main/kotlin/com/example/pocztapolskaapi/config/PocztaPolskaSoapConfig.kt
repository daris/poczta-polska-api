package com.example.pocztapolskaapi.config

import com.example.pocztapolska.client.PpApiTrackingWsTt
import com.example.pocztapolska.client.SledzeniePortType
import jakarta.xml.ws.BindingProvider
import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor
import org.apache.wss4j.common.ext.WSPasswordCallback
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.security.auth.callback.Callback
import javax.security.auth.callback.CallbackHandler

@Configuration
class PocztaPolskaSoapConfig {

    @Bean
    fun sledzeniePort(): SledzeniePortType {

        val service = PpApiTrackingWsTt()
        val port = service.sledzenieHttpSoap11Endpoint

        val endpointUrl = "https://tt.poczta-polska.pl/Sledzenie/services/Sledzenie"
        val requestContext = (port as BindingProvider).requestContext
        requestContext[BindingProvider.ENDPOINT_ADDRESS_PROPERTY] = endpointUrl

        val client: Client = ClientProxy.getClient(port)

        val outProps = HashMap<String, Any>()
        outProps["action"] = "UsernameToken"
        outProps["user"] = "sledzeniepp"
        outProps["passwordType"] = "PasswordText"
        outProps["passwordCallbackClass"] = ClientPasswordCallback::class.java.name

        val wssOut = WSS4JOutInterceptor(outProps)
        client.outInterceptors.add(wssOut)

        return port
    }

    class ClientPasswordCallback : CallbackHandler {
        override fun handle(callbacks: Array<out Callback?>?) {
            callbacks?.forEach {
                if (it is WSPasswordCallback) {
                    it.password = "PPSA"
                }
            }
        }
    }
}