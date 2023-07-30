package com.example.data.responses

import com.example.models.AppointmentStatus
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentResponse(
    val id: String,
    val patientId: String,
    val doctorId: String,
    val date: Int,
    val month: Int,
    val year: Int,
    val time: Int,
    val durationMinutes: Int,
    var status: AppointmentStatus,
    val url: String?,
    val doctorname: String,
    val patientname: String
)
@Serializable
data class ListAppointMent(
    val appointmentlist:List<AppointmentResponse>
)
