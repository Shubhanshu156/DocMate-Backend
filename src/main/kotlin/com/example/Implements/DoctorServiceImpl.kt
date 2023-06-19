package com.example.Implements

import com.example.data.request.DoctorRequest
import com.example.interfaces.DoctorService
import com.example.models.Doctor
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.*
import org.bson.types.ObjectId
import org.litote.kmongo.*

import org.litote.kmongo.coroutine.CoroutineDatabase

class DoctorServiceImpl(val db: CoroutineDatabase) : DoctorService {

    override suspend fun createDoctorProfile(id:String,username:String): Boolean {
        val doctor = db.getCollection<Doctor>("doctor")


        val job = CoroutineScope(Dispatchers.IO).async {
            doctor.insertOne(Doctor(id=ObjectId(id), username = username))
        }
        val v=job.await()
        println("update result is$v")
        return v.wasAcknowledged()
    }

    override suspend fun updateDoctorProfile(id: String, request: DoctorRequest): Pair<Boolean,String> {

        val doctor = db.getCollection<Doctor>("doctor")
        val filter = Doctor::id eq ObjectId(id);
        var lst: ArrayList<SetTo<*>> = ArrayList()
        request.fullname?.let { lst.add(Doctor::fullname setTo it) }
        request.age?.let { lst.add(Doctor::age setTo it) }
        request.category?.let { lst.add(Doctor::category setTo it) }
        request.about?.let { lst.add(Doctor::about setTo it) }
        request.payment?.let { lst.add(Doctor::payment setTo it) }
        request.working_hour_start?.let { lst.add(Doctor::working_hour_start setTo it) }
        request.working_hour_end?.let { lst.add(Doctor::working_hour_end setTo it) }
        request.profileurl?.let { lst.add(Doctor::url setTo it) }


        val update = set(*lst.toTypedArray())



        val job = CoroutineScope(Dispatchers.IO).async {

             doctor.updateOne(filter, update)
        }
        val v=job.await()
        if (v.wasAcknowledged()){
            return Pair(true,"updated Successfully")
        }
        if (v.matchedCount==0L){
            return Pair(false,"No such doctor found")
        }
        println("update result is$v")
        return Pair(false,"There seems to issue on our side")
            }
        override suspend fun getDoctorById(id: Int): Doctor? {
            TODO("Not yet implemented")
        }

        override suspend fun getAllDoctors(): List<Doctor> {
            TODO("Not yet implemented")
        }

        override suspend fun getDoctorsByCategory(category: List<String>): List<Doctor> {
            TODO("Not yet implemented")
        }
    }