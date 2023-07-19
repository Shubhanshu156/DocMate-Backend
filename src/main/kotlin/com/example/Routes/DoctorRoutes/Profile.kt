package com.example.Routes.DoctorRoutes

import com.example.FirebaseAdmin
import com.example.data.request.AuthRequest
import com.example.data.request.DoctorRequest
import com.example.data.request.Gender

import com.example.interfaces.DoctorService
import com.example.models.Doctor
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.StorageClient
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit


fun Route.createProfile(DoctorServices:DoctorService) {
    authenticate {
        patch("doctor/profile") {
            val request = call.receiveOrNull<DoctorRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@patch
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase()=="doctor") {
                try{
                    val gender = when {
                        request.gender.isNullOrBlank() -> null
                        request.gender.lowercase() == "male" -> Gender.MALE
                        request.gender.lowercase() == "female" -> Gender.FEMALE
                        else -> null // Handle cases where the gender value is neither "male" nor "female"
                    }

                    val res=DoctorServices.updateDoctorProfile(userId!!, Doctor(
                        username = request.username,
                        age = request.age ,
                        category = request.category ,
                        fullname = request.fullname ,
                        about = request.about,
                        payment = request.payment,
                        gender = gender,
                        working_hour_start = request.working_hour_start ,
                        working_hour_end = request.working_hour_end ,
                        url = request.profileurl ,
                        id = ObjectId()
                    )
                    )
                    call.respond(HttpStatusCode.OK,res.second)
                }
                catch (e:Exception){

                    call.respond(HttpStatusCode.InternalServerError,"$e.localizedMessage\n$userId\n   $request")
                }
            } else {
            call.respond(HttpStatusCode.Forbidden,"You can not access this route")
            }


        }
    }

}




//private fun generateUniqueFileName(originalFileName: String): String {
//    val extension = originalFileName.substringAfterLast(".")
//    val uniqueId = UUID.randomUUID().toString()
//    return "$uniqueId.$extension"
//}
//private fun uploadFileToFirebaseStorage(filename: String, fileBytes: ByteArray): URL? {
//
//    val app = FirebaseApp.getApps().find { it.name == FirebaseApp.DEFAULT_APP_NAME }
//    val firebaseApp = app ?: FirebaseAdmin.init()
//    val storage = StorageClient.getInstance(firebaseApp).bucket()
//    val bucketName = storage.name
//    val blobId = storage.create(filename, fileBytes)
//    val expirationMillis = TimeUnit.DAYS.toMillis(1) // URL expiration time (e.g., 1 day)
//    val url = storage.storage.signUrl(blobId,100000, TimeUnit.DAYS)
//    println("File uploaded to bucket: $bucketName, Blob ID: $blobId")
//    return url
//
//}