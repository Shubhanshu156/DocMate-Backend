package com.example.Routes.PatientRoutes

import com.example.data.request.DoctorRequest
import com.example.data.request.PatientRequest
import com.example.interfaces.PatientService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.Profile(PatientService:PatientService){
    authenticate {
        patch("patient/profile") {
            val request = call.receiveOrNull<PatientRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            try{
                val res=PatientService.updatePatientProfile(userId!!,request)
                call.respond(HttpStatusCode.OK,res.second)
            }
            catch (e:Exception){

                call.respond(HttpStatusCode.InternalServerError,"$e.localizedMessage\n$userId\n   $request")
            }



        }
    }
}