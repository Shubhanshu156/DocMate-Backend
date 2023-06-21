package com.example.data.request

import kotlinx.serialization.Serializable


@Serializable
data class AcceptAppointMent(
    val appointmentid:String
)
@Serializable
data class bookappointment(
    val doctorid:String,
    val time:String
)
