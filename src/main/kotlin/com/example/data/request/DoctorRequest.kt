package com.example.data.request

import kotlinx.serialization.Serializable
import java.util.Date
@Serializable
data class DoctorRequest(
    val username:String?=null,
    val category:String?=null,
    val fullname:String?=null,
    val age:String?=null,
    val about:String?=null,
    val payment:Int?=null,
    val working_hour_start:Int?=null,
    val working_hour_end: Int?=null,
    val profileurl:String?=null,
    val gender: String?=null
)
