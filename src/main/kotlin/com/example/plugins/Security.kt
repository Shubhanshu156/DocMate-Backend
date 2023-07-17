package com.example.plugins

import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.Security.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import java.util.*


fun Application.configureSecurity(config: TokenConfig) {

    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "ktor sample app"
    val jwtSecret = "secret"

    install(Authentication) {

        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .acceptLeeway(config.expiresIn)
                    .build()
            )
            validate { credential ->
                val expirationTime = credential.payload.expiresAt.time
                val currentTime = System.currentTimeMillis()
                println("valueof expiration is $expirationTime, $currentTime")

                if (expirationTime < currentTime) {
                    // Token is expired
                    return@validate null
                }

                if (credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

}
