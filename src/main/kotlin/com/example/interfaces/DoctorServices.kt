package com.example.interfaces

import com.example.data.request.AddCategory
import com.example.data.request.DoctorRequest
import com.example.models.AppointMents
import com.example.models.Doctor
import com.example.models.Patient
import org.bson.types.ObjectId

interface DoctorService {
    suspend fun createDoctorProfile(id:String,username:String): Boolean
    suspend fun updateDoctorProfile(ObjectId: String, request: DoctorRequest): Pair<Boolean,String>
    suspend fun getDoctorById(id: String): Doctor?

    suspend fun getDoctorAppointments(doctorId: String): List<AppointMents>
    suspend fun getPatient(patientId: String): Patient?
    suspend fun acceptAppointment(appointmentid:String):Boolean
    suspend fun rejectAppointment(appointmendid:String): Boolean

}

