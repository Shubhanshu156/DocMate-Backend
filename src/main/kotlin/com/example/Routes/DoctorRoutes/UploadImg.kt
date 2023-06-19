package com.example.Routes.DoctorRoutes

import com.example.FirebaseAdmin
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.StorageClient
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

fun Route.UploadProfile(){
    authenticate {
        post("/upload") {
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val filename = generateUniqueFileName(part.originalFileName!!)
                        val fileBytes = part.streamProvider().readBytes()
                        val url=uploadFileToFirebaseStorage(filename, fileBytes)
                        call.respond(HttpStatusCode.OK, "File uploaded successfully$url.")
                    }
                    else -> {}
                }
            }
        }

    }

}
private fun generateUniqueFileName(originalFileName: String): String {
    val extension = originalFileName.substringAfterLast(".")
    val uniqueId = UUID.randomUUID().toString()
    return "$uniqueId.$extension"
}
private fun uploadFileToFirebaseStorage(filename: String, fileBytes: ByteArray): URL? {

    val app = FirebaseApp.getApps().find { it.name == FirebaseApp.DEFAULT_APP_NAME }
    val firebaseApp = app ?: FirebaseAdmin.init()
    val storage = StorageClient.getInstance(firebaseApp).bucket()
    val bucketName = storage.name
    val blobId = storage.create(filename, fileBytes)
    val expirationMillis = TimeUnit.DAYS.toMillis(1) // URL expiration time (e.g., 1 day)
    val url = storage.storage.signUrl(blobId,100000, TimeUnit.DAYS)
    println("File uploaded to bucket: $bucketName, Blob ID: $blobId")
    return url

}