package com.example.Routes.DoctorRoutes

import com.example.data.request.AcceptAppointMent
import com.example.interfaces.DoctorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.AcceptAppointMent(doctorService: DoctorService) {
    authenticate {
        post("doctor/accept") {
            val request = call.receiveOrNull<AcceptAppointMent>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase() == "doctor") {
                try {
                    val res = doctorService.acceptAppointment(request.appointmentid)
                    if (res) {
                        call.respond(HttpStatusCode.OK, "Meeting Accepted Successfully")
                        return@post
                    } else {
                        call.respond(HttpStatusCode.OK, "There seems to be issue ")
                        return@post
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error is${e.message}")
                    return@post
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to access this route")
            return@post
            }

        }
    }

}

fun Route.RejectAppointMent(doctorService: DoctorService) {
    authenticate {
        post("doctor/reject") {
            val request = call.receiveOrNull<AcceptAppointMent>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase()=="doctor") {
                try {
                    val res = doctorService.rejectAppointment(request.appointmentid)
                    if (res) {
                        call.respond(HttpStatusCode.OK, "Meeting Rejected Successfully")
                        return@post
                    } else {
                        call.respond(HttpStatusCode.OK, "There seems to be issue ")
                        return@post
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            } else {
            call.respond(HttpStatusCode.Forbidden,"You are not allowed to access this route")
            }
        }
    }

}