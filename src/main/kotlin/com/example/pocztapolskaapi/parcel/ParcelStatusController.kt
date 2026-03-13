package com.example.pocztapolskaapi.parcel

import com.example.pocztapolska.client.SledzeniePortType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("parcel")
class ParcelStatusController(
    private val sledzeniePort: SledzeniePortType
) {

    @GetMapping
    fun getParcel(@RequestParam parcelId: String): String {
        val response = sledzeniePort.sprawdzPrzesylkePl(parcelId)
        return response.danePrzesylki.value.dataNadania.value.toString()
    }
}