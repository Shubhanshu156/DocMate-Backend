package com.example.Routes.AdminRoutes
import com.example.FirebaseAdmin
import com.example.Security.TokenConfig
import com.example.data.request.AddCategory
import com.example.data.request.AuthRequest
import com.example.interfaces.AdminServices
import com.example.models.User
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.StorageClient
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.millisecond
import java.io.FileInputStream
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

fun Route.addCategory(AdminServices: AdminServices) {
    authenticate {
        post("addcategory") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type == "admin") {
                val request = call.receiveOrNull<AddCategory>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if (request.category.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "This type of user is not allowed")
                    return@post
                }
                try {
                    val success = AdminServices.AddCategory(request)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "Category Added Successfully")
                        return@post
                    } else {
                        call.respond(HttpStatusCode.Conflict, "There Seems to be error")
                        return@post
                    }
                } catch (ex: Exception) {
                    call.respond(ex.localizedMessage)
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, "You Are not Allowed to access this page")
            }
        }
    }
}
