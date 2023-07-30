package com.example.data.request

import com.example.models.Patient
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


@Serializable
data class PatientRequest(
    val username:String?=null,
    val name: String?=null,
    val age: Int?=null,
    val gender: String?=null,
    val contactNumber: String?=null,
    val email: String?=null,
    val address: String?=null,
    val profileurl:String?=null,
    val medicalHistory: List<String>?=null,
)
fun Patient.toPatientRequest(): PatientRequest {
    return PatientRequest(
        username = username,
        name = name,
        age = age,
        gender = gender?.name,
        contactNumber = contactNumber,
        email = email,
        address = address,
        profileurl = profileurl,
        medicalHistory = medicalHistory
    )
}
@Serializable
enum class Gender{
    MALE,
    FEMALE;
}