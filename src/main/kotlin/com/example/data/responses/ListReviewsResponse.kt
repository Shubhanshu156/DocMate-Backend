package com.example.data.responses

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class ListReviewsResponse(
    val reviews:List<ReviewsResponse>
)
@Serializable
data class ReviewsResponse(
    val id: String,
    val message:String?,
    val patientId:String,
    val star:Int=0,
)
