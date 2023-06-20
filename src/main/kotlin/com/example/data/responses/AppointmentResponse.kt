package com.example.data.responses

import com.example.models.AppointMents
import com.example.models.AppointmentStatus
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.OffsetDateTime

@Serializable
data class AppointmentResponse(
    val id:String,
    val patientId: String,
    val doctorId: String,
    val appointmentDateTime: String,
    val durationMinutes: Int,
    var status: AppointmentStatus,
    val url: String?
)
@Serializable
data class ListAppointMent(
    val appointmentlist:List<AppointmentResponse>
)
