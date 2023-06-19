package com.example.interfaces

import com.example.data.request.AddCategory
import com.example.data.request.DoctorRequest
import com.example.models.Doctor
import org.bson.types.ObjectId

interface DoctorService {
    suspend fun createDoctorProfile(id:String,username:String): Boolean
    suspend fun updateDoctorProfile(ObjectId: String, request: DoctorRequest): Pair<Boolean,String>
    suspend fun getDoctorById(id: Int): Doctor?
    suspend fun getAllDoctors(): List<Doctor>
    suspend fun getDoctorsByCategory(category: List<String>): List<Doctor>
}

