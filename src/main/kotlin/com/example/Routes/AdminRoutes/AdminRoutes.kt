package com.example.Routes.AdminRoutes

import com.example.interfaces.AdminServices
import io.ktor.server.routing.*

fun Route.AdminRoutes(AdminServices: AdminServices){
    addCategory(AdminServices)
}