package com.example.Security

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claim: TokenClaim,
    ):String
}