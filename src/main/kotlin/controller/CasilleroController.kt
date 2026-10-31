
package com.example.api_de_gymm.controller

import com.example.api_de_gymm.dto.ReservaRequest
import com.example.api_de_gymm.service.CasilleroService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/casilleros")
class CasilleroController(
    private val casilleroService: CasilleroService
) {

    @PostMapping("/{numero}/reservas")
    fun reservar(
        @PathVariable numero: Int,
        @Valid @RequestBody datos: ReservaRequest
    ): ResponseEntity<Any> {

        val reserva = casilleroService.reservar(numero, datos)

        return ResponseEntity
            .created(java.net.URI.create("/casilleros/$numero"))
            .body(reserva)
    }
}
