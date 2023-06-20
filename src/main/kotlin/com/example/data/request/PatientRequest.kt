package com.example.data.request

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


@Serializable
data class PatientRequest(
    val username:String?=null,
    val name: String?=null,
    val age: Int?=null,
    val gender: Gender?=null,
    val contactNumber: String?=null,
    val email: String?=null,
    val address: String?=null,
    val profileurl:String?=null,
    val medicalHistory: List<String>?=null,
)
@Serializable
enum class Gender{
    MALE,
    FEMALE
}