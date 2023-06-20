package com.example.Routes.DoctorRoutes

import com.example.data.responses.AppointmentResponse
import com.example.interfaces.DoctorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.GetAppointment(DoctorService: DoctorService) {
    authenticate {
        get("doctor/appointment") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            val res = DoctorService.getDoctorAppointments(userId!!)
            val ans = res.map {
                AppointmentResponse(
                    it.id.toString(),
                    it.patientId,
                    it.doctorId,
                    it.appointmentDateTime.toString(),
                    it.durationMinutes,
                    it.status,
                    it.url,

                    )
            }
            call.respond(HttpStatusCode.OK, ans)
        }
    }
}