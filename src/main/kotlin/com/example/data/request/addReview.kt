package com.example.data.request

import com.example.models.Review
import kotlinx.serialization.Serializable

@Serializable
data class addReview(
    val doctorid:String,
    val message: String?,
    val star:String,
)
@Serializable
data class getReview(
    val doctorid: String
)
