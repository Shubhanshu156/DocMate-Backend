package com.example.data.responses

import com.example.models.AppointMents
import com.example.models.Review
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class DoctorSearch(
    var doctors:List<DoctorResponse>
)

@Serializable
data class DoctorResponse(
    val username: String? = null,
    val id: String,
    val age: String? = null,
    val category: String? = null,
    val fullname: String? = null,
    val about: String? = null,
    val payment: Int? = null,
    val working_hour_start: Int? = null,
    val working_hour_end: Int? = null,
    val PrevSession: Int? = null,
    val rating: Int? = null,
    val url: String? = null,
    val gender:String?=null,
    val ratingArray: List<Int> = List(5) { 0 },
    val reviews: List<ReviewsResponse> = emptyList()
)
