package com.example.Routes.PatientRoutes

import com.example.data.request.Search
import com.example.data.request.bookappointment
import com.example.data.responses.DoctorResponse
import com.example.data.responses.DoctorSearch
import com.example.data.responses.ReviewsResponse
import com.example.interfaces.PatientService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.SearchDoctor(PatientService:PatientService){
    authenticate {
        get("patient/search"){
            val request = call.receiveOrNull<Search>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            try{
                val res=PatientService.searchDoctorsByCategory(request.name)

                val doctorResponseList: List<DoctorResponse> = res.map { doctor ->
                    DoctorResponse(
                        username = doctor.username,
                        id = doctor.id.toString(),
                        age = doctor.age,
                        category = doctor.category,
                        fullname = doctor.fullname,
                        about = doctor.about,
                        payment = doctor.payment,
                        working_hour_start = doctor.working_hour_start,
                        working_hour_end = doctor.working_hour_end,
                        PrevSession = doctor.PrevSession,
                        rating = doctor.rating,
                        url = doctor.url,
                        reviews = doctor.reviews.map { review ->
                            ReviewsResponse(
                                id = review.id.toString(),
                                message = review.message,
                                patientId = review.patientId,
                                star = review.star
                            )
                        }
                    )
                }

                val doctorSearch = DoctorSearch(doctors = doctorResponseList)

                call.respond(HttpStatusCode.OK,doctorSearch)
            }
            catch (e:Exception){
                call.respond(HttpStatusCode.BadRequest,e.localizedMessage)
            }
        }
    }
}
fun Route.getDoctor(PatientService: PatientService){
    authenticate {
        get("patient/getdoctor") {  }
    }
}