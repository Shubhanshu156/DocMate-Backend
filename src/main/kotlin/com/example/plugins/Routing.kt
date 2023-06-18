package com.example.plugins


import com.example.Routes.addCategory
import com.example.Security.Routes.authenticate
import com.example.Security.Routes.getSecretInfo
import com.example.Security.Routes.signIn
import com.example.Security.Routes.signUp
import com.example.Security.TokenConfig
import com.example.Security.TokenService
import com.example.Security.hasing.HashingService
import com.example.data.request.AddCategory
import com.example.interfaces.AdminServices
import com.example.interfaces.UserDataSource
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    AdminServices:AdminServices
) {
    routing {
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        authenticate()
        getSecretInfo()
        addCategory(AdminServices)

    }
}