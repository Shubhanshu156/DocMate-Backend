package com.example.plugins


import PatientRoutes
import com.example.Routes.AdminRoutes.AdminRoutes
import com.example.Routes.AdminRoutes.addCategory
import com.example.Routes.AuthRoutes.*

import com.example.Routes.DoctorRoutes.DoctorRoutes
import com.example.Routes.DoctorRoutes.UploadProfile
import com.example.Security.TokenConfig
import com.example.Security.TokenService
import com.example.Security.hasing.HashingService
import com.example.interfaces.AdminServices
import com.example.interfaces.DoctorService
import com.example.interfaces.PatientService
import com.example.interfaces.UserDataSource
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    AdminServices: AdminServices,
    DoctorService: DoctorService,
    PatientService: PatientService
) {
    routing {
        AuthRoutes(userDataSource, hashingService, tokenService, tokenConfig, DoctorService, PatientService)
        getSecretInfo()
        DoctorRoutes(DoctorService)
        AdminRoutes(AdminServices)
        PatientRoutes(PatientService)


    }
}

