package com.example.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime
import java.util.*


data class AppointMents(
    @BsonId val id:ObjectId=ObjectId(),
    val patientId: String,
    val doctorId: String,
    val date: Int,
    val month:Int,
    val year:Int,
    val time:Int,
    val durationMinutes: Int,
    var status: AppointmentStatus,
    val url:String?,
    val doctorname:String="DocMate Doctor",
    val patientname:String="DocMate Patient"
)

enum class AppointmentStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED,
    COMPLETED
}