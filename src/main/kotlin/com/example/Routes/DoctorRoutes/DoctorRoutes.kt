package com.example.Routes.DoctorRoutes

import com.example.Routes.AuthRoutes.UpdateToken
import com.example.interfaces.DoctorService
import io.ktor.server.routing.*

fun Route.DoctorRoutes(DoctorService:DoctorService){
    AcceptAppointMent(DoctorService)
    RejectAppointMent(DoctorService)
    GetAppointment(DoctorService)
    GetPatient(DoctorService)
    UploadProfile()

    createProfile(DoctorService)
}