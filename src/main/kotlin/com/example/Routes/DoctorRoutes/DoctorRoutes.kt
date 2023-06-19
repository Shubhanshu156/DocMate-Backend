package com.example.Routes.DoctorRoutes

import com.example.interfaces.DoctorService
import io.ktor.server.routing.*

fun Route.DoctorRoutes(DoctorService:DoctorService){
    UploadProfile()
    createProfile(DoctorService)
}