package com.example.models

import com.example.data.request.Gender
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Patient(
    @BsonId val id:ObjectId,
    val username:String,
    val name: String?=null,
    val age: Int?=null,
    val gender: Gender?=null,
    val token:String?=null,
    val contactNumber: String?=null,
    val email: String?=null,
    val address: String?=null,
    val profileurl:String?=null,
    val medicalHistory: List<String>?=null,
)


