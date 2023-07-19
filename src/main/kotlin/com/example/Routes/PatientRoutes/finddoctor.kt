package com.example.Routes.PatientRoutes

import com.example.data.request.Search
import com.example.data.request.SearchbyId
import com.example.data.responses.DoctorResponse
import com.example.data.responses.DoctorSearch
import com.example.data.responses.ReviewsResponse
import com.example.interfaces.PatientService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase() == "patient") {
                try {
                    val res = PatientService.searchDoctorsByCategory(request.category,request.name)

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
                            ratingArray = doctor.ratingArray,
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
            } else {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to access this page")
            }
        }
    }
}

fun Route.GetCategories(PatientService: PatientService) {
    authenticate {
        get("patient/categories") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase() == "patient") {
                try {
                    val res = PatientService.getcategory()
                    print("result is $res")
                    if (res != null) {
                        call.respond(HttpStatusCode.OK, res)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Enter valid details")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "${e.localizedMessage}")
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to access this page")
            }
        }
    }
}

fun Route.getDoctor(PatientService: PatientService) {
    authenticate {
        get("patient/getdoctor") {
            val request = call.receiveOrNull<SearchbyId>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)
            if (type!!.lowercase() == "patient") {
                try {
                    val res = PatientService.getDoctor(request.id)

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
            } else {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to access this page")
            }

        }
    }
}

fun Route.getTopDoctors(PatientService: PatientService) {
    authenticate {
        get("patient/topdoctors") {
            try {
                var res2 = PatientService.getTopDoctors()

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
                                id = it.id.toString(),
                                patientId = it.patientId,
                                message = it.message,
                                star = it.star
                            )
                        })
                }
                call.respond(HttpStatusCode.OK, result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.localizedMessage)
            }
        }

    }
}

fun Route.getAllDoctors(PatientService: PatientService) {
    authenticate {
        get("/patient/getall") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val type = principal?.getClaim("TYPE", String::class)

            if (type!!.lowercase() == "patient") {
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
                            gender= res.gender?.name,
                            url = res.url,
                            reviews = res.reviews.map {
                                ReviewsResponse(
                                    id = it.id.toString(),
                                    patientId = it.patientId,
                                    message = it.message,
                                    star = it.star
                                )
                            })
                    }
                    call.respond(HttpStatusCode.OK, result)

                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "${e.localizedMessage}")
                }
            } else {
                call.respond(HttpStatusCode.Forbidden, "You are not allowed to this route")
            }
        }
    }
}