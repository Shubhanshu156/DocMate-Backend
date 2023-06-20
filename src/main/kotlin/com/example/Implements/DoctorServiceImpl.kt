package com.example.Implements

import com.example.data.request.DoctorRequest
import com.example.interfaces.DoctorService
import com.example.models.AppointMents
import com.example.models.AppointmentStatus
//import com.example.models.Appointment
import com.example.models.Doctor
import com.example.models.Patient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import kotlinx.coroutines.*
import org.bson.types.ObjectId
import org.litote.kmongo.*

import org.litote.kmongo.coroutine.CoroutineDatabase

class DoctorServiceImpl(val db: CoroutineDatabase) : DoctorService {
    val doctorCollection = db.getCollection<Doctor>("doctor")
    val patientCollection = db.getCollection<Patient>("patient")
    val AppointmentCollection = db.getCollection<AppointMents>("appointment")

    override suspend fun createDoctorProfile(id: String, username: String): Boolean {
        val filter = Doctor::username eq username
        val existingDoctor = doctorCollection.findOne(filter)
        if (existingDoctor!=null){
            throw Exception("already username taken")
        }
        val job = CoroutineScope(Dispatchers.IO).async {

                doctorCollection.insertOne(Doctor(id = ObjectId(id), username = username))
        }
        val v = job.await()
        println("update result is $v")
        return v.wasAcknowledged()
    }


    override suspend fun updateDoctorProfile(id: String, request: DoctorRequest): Pair<Boolean,String> {


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

             doctorCollection.updateOne(filter, update)
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
    override suspend fun getDoctorById(id: String): Doctor? {
        val doctorId = ObjectId(id)

        return doctorCollection.findOne(Filters.eq("_id", doctorId))
    }




    override suspend fun getDoctorAppointments(doctorId: String): List<AppointMents> {
        val appointmentslst = AppointmentCollection.find(AppointMents::doctorId eq doctorId)
        appointmentslst?.let {
            return it!!.toList()
        }
        return emptyList()
    }

    override suspend fun getPatient(patientId: String): Patient? {
        val patient = patientCollection.findOneById(patientId)
        return patient ?: throw IllegalArgumentException("Patient with ID $patientId not found.")
    }


    override suspend fun acceptAppointment(appointmentid: String):Boolean {

        val filters = Filters.eq("_id", ObjectId(appointmentid))
        val update = Updates.set("status", AppointmentStatus.ACCEPTED.name)
        val updateResult = AppointmentCollection.updateOne(filters, update)

        if (updateResult.modifiedCount == 0L) {
            throw Exception("Failed to accept the appointment.")
        }
        else{
            return updateResult.wasAcknowledged()
        }
    }


    override suspend fun rejectAppointment(appointmentid: String):Boolean {
        val filters = Filters.eq("_id", ObjectId(appointmentid))
        val update = Updates.set("status", AppointmentStatus.REJECTED.name)
        val updateResult = AppointmentCollection.updateOne(filters, update)

        if (updateResult.modifiedCount == 0L) {
            throw Exception("Failed to accept the appointment.")
        }
        return updateResult.wasAcknowledged()
    }


}