package com.example.Routes.DoctorRoutes

import com.example.data.request.PatientRequest
import com.example.data.request.toPatientRequest
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
        get("getpatient") { // Change to 'get' instead of 'post'
            val userId = call.principal<JWTPrincipal>()?.getClaim("userId", String::class)
            val type = call.principal<JWTPrincipal>()?.getClaim("TYPE", String::class)

                val request = call.parameters["patientId"] // Assuming the query parameter name is "patientId"
                if (request != null) {
                    try {
                        val res = DoctorService.getPatient(request)
                        if (res != null) {
                            call.respond(HttpStatusCode.OK, res.toPatientRequest())
                        }
                        else{
                            call.respond(HttpStatusCode.BadRequest)
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'patientId' query parameter")
                }
            }


    }
}
