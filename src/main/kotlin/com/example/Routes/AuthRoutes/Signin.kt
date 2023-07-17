package com.example.Routes.AuthRoutes

import com.example.Security.TokenClaim
import com.example.Security.TokenConfig
import com.example.Security.TokenService
import com.example.Security.hasing.HashingService
import com.example.Security.hasing.SaltedHash
import com.example.data.request.AuthRequest
import com.example.data.responses.AuthResponse
import com.example.interfaces.UserDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils

fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/signin") {
        val request = call.receiveOrNull<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest,  AuthResponse(token = "",msg="Enter Valid Data "))
            return@post
        }

        val user = userDataSource.getUserByUserNameType(request.username, request.type)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound,  AuthResponse(token = "",msg="No Such User"))
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            call.respond(HttpStatusCode.Unauthorized,  AuthResponse(token = "",msg="Please Enter Correct Password"))
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            ),
            TokenClaim("TYPE", user.type.toString())
        )

        call.respond(
            HttpStatusCode.OK,
            AuthResponse(token = token,msg="SignIn Successfully")
        )
    }
}

