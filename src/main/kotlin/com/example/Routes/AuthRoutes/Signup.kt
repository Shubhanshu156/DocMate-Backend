package com.example.Routes.AuthRoutes

import com.example.Security.hasing.HashingService
import com.example.data.request.AuthRequest
import com.example.data.request.Token
import com.example.interfaces.DoctorService
import com.example.interfaces.PatientService
import com.example.interfaces.UserDataSource
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    DoctorService: DoctorService,
    PatientService: PatientService
) {
    post("signup") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank() || request.type.isBlank()
        val isPwTooShort = request.password.length < 8
        if (request.type.lowercase() != "doctor" && request.type.lowercase() != "patient") {
            call.respond(HttpStatusCode.BadRequest, "This type of user is not allowed")
            return@post
        }
        if (areFieldsBlank) {
            call.respond(HttpStatusCode.Conflict, "Enter all fields")
            return@post
        }
        if (isPwTooShort) {
            call.respond(HttpStatusCode.BadRequest, "Passwords too shorts")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            type = request.type,
            salt = saltedHash.salt
        )
        try {
            val wasAcknowledged = userDataSource.insertUser(user)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
            if (user.type.lowercase() == "doctor") {
                DoctorService.createDoctorProfile(user.id.toString(), user.username)
            }
            if (user.type.lowercase() == "patient") {
                PatientService.createPatientProfile(user.id.toString(), user.username)
            }
            call.respond(HttpStatusCode.OK, "Account created Successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, e.localizedMessage)
        }

    }
}

fun Route.UpdateToken(userDataSource: UserDataSource) {
    authenticate {
        post("token") {
            val request = call.receiveOrNull<Token>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            val res = userDataSource.UpdateToken(type!!,userId!!, request.token)
            try {
                if (res) {
                    call.respond(HttpStatusCode.OK, "Token has been updated Successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Token can not be updated")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
            }

        }
    }
}
