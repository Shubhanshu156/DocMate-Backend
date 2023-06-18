package com.example.Security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService : TokenService {
    override fun generate(config: TokenConfig, vararg claim: TokenClaim): String {
        var token=JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(Date(System.currentTimeMillis()+config.expiresIn))
        claim.forEach {
            token.withClaim(it.name,it.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }
}