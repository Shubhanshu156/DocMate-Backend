package com.example.Routes.DoctorRoutes

import com.example.data.request.AcceptAppointMent
import com.example.data.request.DoctorRequest
import com.example.interfaces.DoctorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.AcceptAppointMent(doctorService:DoctorService){
    authenticate {
        post("doctor/accept") {
            val request = call.receiveOrNull<AcceptAppointMent>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val res=doctorService.acceptAppointment(request.id)
            if (res){
                call.respond(HttpStatusCode.OK,"Meeting Accepted Successfully")
                return@post
            }
            else{
                call.respond(HttpStatusCode.OK,"There seems to be issue ")
                return@post
            }
        }
    }

}
fun Route.RejectAppointMent(doctorService:DoctorService){
    authenticate {
        post("doctor/reject") {
            val request = call.receiveOrNull<AcceptAppointMent>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val res=doctorService.rejectAppointment(request.id)
            if (res){
                call.respond(HttpStatusCode.OK,"Meeting Accepted Successfully")
                return@post
            }
            else{
                call.respond(HttpStatusCode.OK,"There seems to be issue ")
                return@post
            }
        }
    }

}