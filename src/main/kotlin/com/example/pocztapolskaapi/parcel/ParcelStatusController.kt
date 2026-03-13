package com.example.pocztapolskaapi.parcel

import com.example.pocztapolska.client.PpApiTrackingWsTt
import com.example.pocztapolska.client.Przesylka
import com.example.pocztapolska.client.SledzeniePortType
import jakarta.xml.ws.BindingProvider
import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor
import org.apache.wss4j.common.ext.WSPasswordCallback
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.security.auth.callback.Callback
import javax.security.auth.callback.CallbackHandler


@RestController
@RequestMapping("parcel")
class ParcelStatusController {

    @GetMapping
    fun getParcel(@RequestParam parcelId: String): String {
        val service = PpApiTrackingWsTt()
        val port: SledzeniePortType = service.getSledzenieHttpSoap11Endpoint()
        val endpointUrl = "https://tt.poczta-polska.pl/Sledzenie/services/Sledzenie"
        val requestContext = (port as BindingProvider).getRequestContext()
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl)

        val client: Client = ClientProxy.getClient(port)
        val outProps: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        outProps.put("action", "UsernameToken")
        outProps.put("user", "sledzeniepp")
        outProps.put("passwordType", "PasswordText") // or PasswordDigest
        outProps.put("passwordCallbackClass", ClientPasswordCallback::class.java.getName())

        val wssOut = WSS4JOutInterceptor(outProps)
        client.getOutInterceptors().add(wssOut)

        try {
            val response: Przesylka = port.sprawdzPrzesylkePl(parcelId)
            return response.danePrzesylki.value.dataNadania.value.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    class ClientPasswordCallback : CallbackHandler {
        override fun handle(callbacks: Array<out Callback?>?) {
            if (callbacks != null) {
                for (callback in callbacks) {
                    if (callback is WSPasswordCallback) {
                        callback.setPassword("PPSA")
                    }
                }
            }
        }
    }
}