package com.example.Routes.DoctorRoutes

import com.example.interfaces.DoctorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.GetPatient(DoctorService: DoctorService) {
    authenticate {
        get("doctor/patient") {
            val request = call.receiveOrNull<String>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase() == "doctor") {
                try {
                    val res = DoctorService.getPatient(request)
                    call.respond(HttpStatusCode.OK, "$res")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Give Valid id")
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to access this route")
            }


        }
    }
}