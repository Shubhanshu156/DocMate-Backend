package com.example.Routes.AuthRoutes

import com.example.Security.TokenClaim
import com.example.Security.TokenConfig
import com.example.Security.TokenService
import com.example.Security.hasing.HashingService
import com.example.Security.hasing.SaltedHash
import com.example.data.request.AuthRequest
import com.example.data.responses.AuthResponse
import com.example.interfaces.DoctorService
import com.example.interfaces.UserDataSource
//import com.example.models.
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils


fun Route.AuthRoutes(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    DoctorService:DoctorService
) {
    signIn(userDataSource,hashingService,tokenService,tokenConfig)
    signUp(hashingService,userDataSource,DoctorService)
}

//fun Route.authenticate() {
//    authenticate {
//        get("authenticate") {
//            call.respond(HttpStatusCode.OK)
//        }
//    }
//}
//
fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId and your type is $type")
        }
    }
    }
//}