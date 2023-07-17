package com.example.Routes.PatientRoutes

import com.example.data.request.AcceptAppointMent
import com.example.data.request.bookappointment
import com.example.data.responses.AppointmentResponse
import com.example.data.responses.ListAppointMent
import com.example.interfaces.PatientService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime

fun Route.bookappointment(PatientService: PatientService) {
    authenticate {
        post("patient/book") {
            val request = call.receiveOrNull<bookappointment>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest,"There is issue in your request")
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)

            if (type!!.lowercase()=="patient") {
                try {
                    val res = PatientService.bookAppointment(
                        patientId = userId!!,
                        doctorId = request.doctorid,
                        appointmentDateTime = LocalDateTime.parse(request.time)
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        AppointmentResponse(
                            res!!.id.toString(),
                            res.patientId,
                            res.doctorId,
                            res.appointmentDateTime.toString(),
                            res.durationMinutes,
                            res.status,
                            res.url
                        )
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "This is error messsage ${e.message}")
                }
            }
            else {
                call.respond(HttpStatusCode.Forbidden,"You are not allowed to this route")
            }
        }
    }

}

fun Route.getappointment(PatientService: PatientService) {
    authenticate {
        get("patient/getappointments") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase()=="patient") {
                try {
                    val res = PatientService.getPatientAppointments(userId!!)
                    var ans = ListAppointMent(res.map {
                        AppointmentResponse(
                            doctorId = it.doctorId,
                            patientId = it.patientId,
                            appointmentDateTime = it.appointmentDateTime.toString(),
                            durationMinutes = it.durationMinutes,
                            id = it.id.toString(),
                            url = it.url,
                            status = it.status
                        )
                    })
                    call.respond(HttpStatusCode.OK, ans)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, e.localizedMessage)
                }
            }
            else {
            call.respond(HttpStatusCode.Forbidden,"You can not access this route")
            }
        }
    }
}

fun Route.cancelAppointMent(PatientService: PatientService) {
    authenticate {
        post("patient/cancel") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase()=="patient") {
                val request = call.receiveOrNull<AcceptAppointMent>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                try {
                    val res = PatientService.cancelAppointment(request.appointmentid)
                    if (res) {
                        call.respond(HttpStatusCode.OK, "Successfully cancelled appointment")
                    } else {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            "can not cancel this appointment check there is no such appointment"
                        )
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "${e.localizedMessage}")

                }
            }
            else {
                call.respond(HttpStatusCode.Forbidden,"You can not access this route")
            }
        }
    }
}