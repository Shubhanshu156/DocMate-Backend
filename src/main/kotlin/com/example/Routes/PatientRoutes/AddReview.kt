package com.example.Routes.PatientRoutes

import com.example.data.request.DoctorRequest
import com.example.data.request.addReview
import com.example.data.request.getReview
import com.example.data.responses.ListReviewsResponse
import com.example.data.responses.ReviewsResponse
import com.example.interfaces.PatientService
import com.example.models.Review
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Route.addReview(PatientSerivce: PatientService) {
    authenticate {
        post("patient/addreview") {
            val request = call.receiveOrNull<addReview>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase()=="patient") {
                val res = PatientSerivce.addDoctorReview(
                    request.doctorid,
                    Review(patientId = userId!!, message = request.message.toString(), star = request.star)
                )
                try {
                    if (res) {
                        call.respond(HttpStatusCode.OK, "Review Added Successfullly")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "${e.localizedMessage}")
                }
            }
            else{
               call.respond(HttpStatusCode.BadRequest,"You can not access this route")
            }
        }
    }
}
fun Route.getReviews(PatientSerivce: PatientService){
    authenticate {
        get("patient/reviews") {
            val request = call.receiveOrNull<getReview>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase()=="patient") {
                try {
                    val res: List<Review> = PatientSerivce.getDoctorReviews(request.doctorid)

                    val reviewsResponseList: List<ReviewsResponse> = res.map { review ->
                        ReviewsResponse(
                            id = review.id.toString(),
                            message = review.message,
                            patientId = review.patientId,
                            star = review.star
                        )
                    }

                    val listReviewsResponse = ListReviewsResponse(reviews = reviewsResponseList)
                    val json = Json.encodeToString(ListReviewsResponse.serializer(), listReviewsResponse)
                    call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)

                }
                catch (e:Exception){
                    call.respond(HttpStatusCode.BadRequest,"${e.localizedMessage}")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest,"You can not access this route")
            }
        }
    }
}