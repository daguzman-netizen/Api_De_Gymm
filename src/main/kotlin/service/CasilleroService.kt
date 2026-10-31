
package com.example.api_de_gymm.service

import com.example.api_de_gymm.dto.ReservaRequest
import org.springframework.stereotype.Service

data class ReservaResponse(
    val casillero: Int,
    val socioId: Long,
    val meses: Int,
    val mensaje: String
)

data class Reserva(
    val casillero: Int,
    val socioId: Long,
    val meses: Int
)

class CasilleroNoExisteException :
    RuntimeException("El casillero solicitado no existe")

class SocioNoExisteException :
    RuntimeException("El socio indicado no está registrado")

class CasilleroOcupadoException :
    RuntimeException("El casillero ya está reservado")

class SocioYaTieneCasilleroException :
    RuntimeException("El socio ya tiene un casillero reservado")

@Service
class CasilleroService {

    // Socios registrados para las pruebas del ejercicio
    private val sociosRegistrados = (1L..30L).toSet()

    // Reservas almacenadas temporalmente en memoria
    private val reservas = mutableMapOf<Int, Reserva>()

    fun reservar(numero: Int, datos: ReservaRequest): ReservaResponse {

        val socioId = datos.socioId!!
        val meses = datos.meses!!

        // Comprobar que el casillero exista
        if (numero !in 1..30) {
            throw CasilleroNoExisteException()
        }

        // Comprobar que el socio exista
        if (socioId !in sociosRegistrados) {
            throw SocioNoExisteException()
        }

        // Comprobar que el casillero esté libre
        if (reservas.containsKey(numero)) {
            throw CasilleroOcupadoException()
        }

        // Comprobar que el socio no tenga otro casillero
        if (reservas.values.any { it.socioId == socioId }) {
            throw SocioYaTieneCasilleroException()
        }

        // Guardar la reserva
        val reserva = Reserva(
            casillero = numero,
            socioId = socioId,
            meses = meses
        )

        reservas[numero] = reserva

        return ReservaResponse(
            casillero = numero,
            socioId = socioId,
            meses = meses,
            mensaje = "Reserva realizada correctamente"
        )
    }
}
