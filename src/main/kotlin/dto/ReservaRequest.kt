
package com.example.api_de_gymm.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull


data class ReservaRequest(

    @field:NotNull(message = "El id del socio es obligatorio")
    val socioId: Long?,

    @field:NotNull(message = "La duración es obligatoria")
    @field:Min(value = 1, message = "La duración debe estar entre 1 y 12 meses")
    @field:Max(value = 12, message = "La duración debe estar entre 1 y 12 meses")
    val meses: Int?

)
