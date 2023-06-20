package com.example.Routes.AuthRoutes
import PatientRoutes
import com.example.Security.TokenConfig
import com.example.Security.TokenService
import com.example.Security.hasing.HashingService
import com.example.interfaces.DoctorService
import com.example.interfaces.PatientService
import com.example.interfaces.UserDataSource
//import com.example.models.
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Route.AuthRoutes(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    DoctorService:DoctorService,
    PatientService:PatientService
) {
    signIn(userDataSource,hashingService,tokenService,tokenConfig)
    signUp(hashingService,userDataSource,DoctorService,PatientService)
}
fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)

            println("url is $type")
            call.respond(HttpStatusCode.OK, "Your userId is $userId and your category/type is $type and url is ")
        }
    }
    }


