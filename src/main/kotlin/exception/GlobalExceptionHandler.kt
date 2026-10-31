
package com.example.api_de_gymm.exception

import com.example.api_de_gymm.service.CasilleroNoExisteException
import com.example.api_de_gymm.service.CasilleroOcupadoException
import com.example.api_de_gymm.service.SocioNoExisteException
import com.example.api_de_gymm.service.SocioYaTieneCasilleroException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.OffsetDateTime

data class DetalleError(
    val campo: String,
    val mensaje: String
)

data class ErrorResponse(
    val estado: Int,
    val error: String,
    val mensaje: String,
    val ruta: String,
    val momento: String,
    val detalles: List<DetalleError>
)

@RestControllerAdvice
class GlobalExceptionHandler(
    @Value("\${gimnasio.validacion.estado:422}")
    private val estadoValidacion: Int
) {

    private fun crearError(
        estado: Int,
        nombre: String,
        mensaje: String,
        ruta: String,
        detalles: List<DetalleError> = emptyList()
    ): ErrorResponse {
        return ErrorResponse(
            estado = estado,
            error = nombre,
            mensaje = mensaje,
            ruta = ruta,
            momento = OffsetDateTime.now().toString(),
            detalles = detalles
        )
    }

    private fun responder(
        estado: Int,
        nombre: String,
        mensaje: String,
        request: HttpServletRequest,
        detalles: List<DetalleError> = emptyList()
    ): ResponseEntity<ErrorResponse> {

        return ResponseEntity.status(estado).body(
            crearError(
                estado,
                nombre,
                mensaje,
                request.requestURI,
                detalles
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun manejarValidacion(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        val detalles = ex.bindingResult.fieldErrors.map { error: FieldError ->
            DetalleError(
                campo = error.field,
                mensaje = error.defaultMessage ?: "Dato no válido"
            )
        }

        return responder(
            estadoValidacion,
            "Datos no válidos",
            "Los datos enviados no son válidos",
            request,
            detalles
        )
    }

    @ExceptionHandler(CasilleroNoExisteException::class)
    fun manejarCasillero(
        ex: CasilleroNoExisteException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        return responder(
            404,
            "Not Found",
            ex.message ?: "El casillero no existe",
            request
        )
    }

    @ExceptionHandler(SocioNoExisteException::class)
    fun manejarSocio(
        ex: SocioNoExisteException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        return responder(
            estadoValidacion,
            "Datos no válidos",
            "El socio indicado no está registrado",
            request,
            listOf(
                DetalleError("socioId", "El id del socio no existe")
            )
        )
    }

    @ExceptionHandler(CasilleroOcupadoException::class)
    fun manejarCasilleroOcupado(
        ex: CasilleroOcupadoException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        return responder(
            409,
            "Conflict",
            ex.message ?: "El casillero ya está reservado",
            request
        )
    }

    @ExceptionHandler(SocioYaTieneCasilleroException::class)
    fun manejarSocioOcupado(
        ex: SocioYaTieneCasilleroException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {

        return responder(
            409,
            "Conflict",
            ex.message ?: "El socio ya tiene un casillero",
            request
        )
    }
}
