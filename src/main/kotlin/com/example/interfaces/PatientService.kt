package com.example.interfaces

import com.example.data.request.DoctorRequest
import com.example.data.request.PatientRequest
import com.example.models.AppointMents
import com.example.models.Doctor
import java.time.LocalDate
import java.time.LocalDateTime
import com.example.models.Review as Review

interface PatientService {
    suspend fun createPatientProfile(id:String,username:String): Boolean
    suspend fun updatePatientProfile(ObjectId: String, request: PatientRequest): Pair<Boolean,String>
    suspend fun getDoctor(doctorId: String): Doctor?
    suspend fun getAllDoctors():List<Doctor>
    suspend fun searchDoctorsByCategory(category: String): List<Doctor>
    suspend fun bookAppointment(
        patientId: String,
        doctorId: String,
        appointmentDateTime: LocalDateTime
    ): AppointMents?

    suspend fun cancelAppointment(appointmentid:String ): Boolean
    suspend fun getPatientAppointments(patientId: String): List<AppointMents>
    suspend fun getDoctorReviews(doctorId: String): List<Review>
    suspend fun addDoctorReview(doctorId: String, review: Review): Boolean
}


