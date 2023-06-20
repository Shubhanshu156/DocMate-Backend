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

fun Route.SearchDoctor(PatientService: PatientService) {
    authenticate {
        get("patient/search") {
            val request = call.receiveOrNull<Search>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            try {
                val res = PatientService.searchDoctorsByCategory(request.name)

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

                call.respond(HttpStatusCode.OK, doctorSearch)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
            }
        }
    }
}

fun Route.getDoctor(PatientService: PatientService) {
    authenticate {
        get("patient/getdoctor") {
            val request = call.receiveOrNull<Search>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            try {
                val res = PatientService.getDoctor(request.name)

                if (res != null) {
                    call.respond(HttpStatusCode.OK, DoctorResponse(
                        username = res.username,
                        id = res.id.toString(),
                        age = res.age,
                        category = res.category,
                        fullname = res.fullname,
                        about = res.about,
                        payment = res.payment,
                        working_hour_start = res.working_hour_start,
                        working_hour_end = res.working_hour_end,
                        PrevSession = res.PrevSession,
                        rating = res.rating,
                        url = res.url,
                        reviews = res.reviews.map {
                            ReviewsResponse(
                                id = it.id.toString(),
                                patientId = it.patientId,
                                message = it.message,
                                star = it.star
                            )
                        }
                    ))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Enter valid details")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "${e.localizedMessage}")
            }

        }
    }
}

fun Route.getAllDoctors(PatientService: PatientService) {
    authenticate {
        get("/patient/getall") {
            try {
                val res2 = PatientService.getAllDoctors()
                val result = res2.map { res ->
                    DoctorResponse(
                        username = res.username,
                        id = res.id.toString(),
                        age = res.age,
                        category = res.category,
                        fullname = res.fullname,
                        about = res.about,
                        payment = res.payment,
                        working_hour_start = res.working_hour_start,
                        working_hour_end = res.working_hour_end,
                        PrevSession = res.PrevSession,
                        rating = res.rating,
                        url = res.url,
                        reviews = res.reviews.map {
                            ReviewsResponse(
                                id = it.id.toString(), patientId = it.patientId, message = it.message, star = it.star
                            )
                        })
                }
                call.respond(HttpStatusCode.OK, result)

            } catch (e: java.lang.Exception) {
                call.respond(HttpStatusCode.InternalServerError, "${e.localizedMessage}")
            }
        }
    }
}