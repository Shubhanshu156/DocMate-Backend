package com.example.Routes.AuthRoutes

import com.example.Security.hasing.HashingService
import com.example.data.request.AuthRequest
import com.example.data.request.DoctorRequest
import com.example.interfaces.DoctorService
import com.example.interfaces.UserDataSource
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    DoctorService:DoctorService
) {
    post("signup") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()||request.type.isBlank()
        val isPwTooShort = request.password.length < 8
        if (request.type.lowercase()!="doctor" && request.type.lowercase()!="patient"){
            call.respond(HttpStatusCode.BadRequest,"This type of user is not allowed")
            return@post
        }
        if(areFieldsBlank ) {
            call.respond(HttpStatusCode.Conflict,"Enter all fields")
            return@post
        }
        if(isPwTooShort ) {
            call.respond(HttpStatusCode.BadRequest,"Passwords too shorts")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            type=request.type,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertUser(user)

        if(!wasAcknowledged)  {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        DoctorService.createDoctorProfile(user.id.toString(),user.username)
        call.respond(HttpStatusCode.OK,"Account created Successfully")
    }
}
