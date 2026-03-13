package com.example.pocztapolskaapi.parcel

import com.example.pocztapolskaapi.config.SoapLoggingInterceptor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.ws.client.core.WebServiceMessageCallback
import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.soap.SoapMessage
import org.springframework.xml.transform.StringResult
import java.io.StringReader
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamSource

@RestController
@RequestMapping("parcel")
class ParcelStatusController {
    private val webServiceTemplate = WebServiceTemplate().apply {
        setInterceptors(arrayOf(SoapLoggingInterceptor()))
    }

    @GetMapping
    fun getParcel(@RequestParam parcelId: String): String {
        webServiceTemplate.setInterceptors(arrayOf(SoapLoggingInterceptor()))

        val soapBody = """
    <sled:sprawdzPrzesylkePl xmlns:sled="http://sledzenie.pocztapolska.pl">
        <sled:numer>$parcelId</sled:numer>
    </sled:sprawdzPrzesylkePl>
""".trimIndent()

        val source = StreamSource(StringReader(soapBody))
        val result = StringResult()

        webServiceTemplate.sendSourceAndReceiveToResult(
            "https://tt.poczta-polska.pl/Sledzenie/services/Sledzenie",
            source,
            WebServiceMessageCallback { message ->
                val soapMessage = message as SoapMessage
                soapMessage.soapAction = "urn:sprawdzPrzesylkePl"

                // Add WS-Security header
                val securityHeader = """
            <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
                <wsse:UsernameToken wsu:Id="UsernameToken-2" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
                    <wsse:Username>sledzeniepp</wsse:Username>
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">PPSA</wsse:Password>
                    <wsu:Created>2026-03-13T11:41:31.746Z</wsu:Created>
                </wsse:UsernameToken>
            </wsse:Security>
        """.trimIndent()

                val header = soapMessage.soapHeader
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.transform(StreamSource(StringReader(securityHeader)), header?.result)
            },
            result
        )

        return result.toString()
    }
}