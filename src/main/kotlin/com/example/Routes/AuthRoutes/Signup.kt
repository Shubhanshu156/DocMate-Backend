package com.example.Routes.AuthRoutes

import com.example.Security.TokenClaim
import com.example.Security.hasing.HashingService
import com.example.data.request.AuthRequest
import com.example.data.request.SignUpResponse
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
    doctorService: DoctorService,
    patientService: PatientService
) {
    post("/signup") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, SignUpResponse("Invalid request"))
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank() || request.type.isBlank()
        val isPwTooShort = request.password.length < 8
        if (request.type.lowercase() != "doctor" && request.type.lowercase() != "patient") {
            call.respond(HttpStatusCode.BadRequest, SignUpResponse("This type of user is not allowed"))
            return@post
        }
        if (areFieldsBlank) {
            call.respond(HttpStatusCode.BadRequest, SignUpResponse("Enter all fields"))
            return@post
        }
        if (isPwTooShort) {
            call.respond(HttpStatusCode.BadRequest, SignUpResponse("Password too short"))
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
                call.respond(HttpStatusCode.Conflict, SignUpResponse("User creation failed"))
                return@post
            }
            if (user.type.lowercase() == "doctor") {
                doctorService.createDoctorProfile(user.id.toString(), user.username)
            }
            if (user.type.lowercase() == "patient") {
                patientService.createPatientProfile(user.id.toString(), user.username)
            }
            call.respond(HttpStatusCode.OK, SignUpResponse("Account created successfully"))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, SignUpResponse(e.localizedMessage))
        }
    }
}


fun Route.UpdateToken(userDataSource: UserDataSource) {
    authenticate {
        post("/token") {
            val request = call.receiveOrNull<Token>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, SignUpResponse("Not a Valid Request"))
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            val res = userDataSource.UpdateToken(type!!, userId!!, request.token)
            try {
                if (res) {
                    call.respond(HttpStatusCode.OK, SignUpResponse("Token has been updated successfully"))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, SignUpResponse("Failed to update token"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, SignUpResponse(e.localizedMessage))
            }
        }
    }
}

