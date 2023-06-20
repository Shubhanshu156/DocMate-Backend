package com.example.Implements

import com.example.data.request.PatientRequest
import com.example.interfaces.PatientService
import com.example.models.*
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.beans.ExceptionListener

import java.time.LocalDateTime

class PatientServiceImpl(db: CoroutineDatabase) : PatientService {
    val doctorCollection = db.getCollection<Doctor>("doctor")
    val patientCollection = db.getCollection<Patient>("patient")
    val AppointmentCollection = db.getCollection<AppointMents>("appointment")
    override suspend fun createPatientProfile(id: String, username: String): Boolean {
        val filter = Patient::username eq username
        val existingpatient = patientCollection.findOne(filter)
        if (existingpatient!=null){
            throw Exception("already username taken")
        }
        val job = CoroutineScope(Dispatchers.IO).async {
            patientCollection.insertOne(Patient(id=ObjectId(id), username = username))
        }
        val v=job.await()
        println("update result is$v")
        return v.wasAcknowledged()
    }

    override suspend fun updatePatientProfile(id: String, request: PatientRequest): Pair<Boolean, String> {

        val filter = Patient::id eq ObjectId(id)
        val lst: ArrayList<SetTo<*>> = ArrayList()

        request.name?.let { lst.add(Patient::name setTo it) }
        request.age?.let { lst.add(Patient::age setTo it) }
        request.gender?.let { lst.add(Patient::gender setTo it) }
        request.contactNumber?.let { lst.add(Patient::contactNumber setTo it) }
        request.email?.let { lst.add(Patient::email setTo it) }
        request.address?.let { lst.add(Patient::address setTo it) }
        request.medicalHistory?.let { lst.add(Patient::medicalHistory setTo it) }
        request.profileurl?.let { lst.add(Patient::profileurl setTo it) }
        val update = set(*lst.toTypedArray())
        val job = CoroutineScope(Dispatchers.IO).async {
            patientCollection.updateOne(filter, update)
        }
        val v=job.await()
        if (v.wasAcknowledged()){
            return Pair(true,"updated Successfully!!")
        }
        if (v.matchedCount==0L){
            return Pair(false,"No such patient found")
        }
        println("update result is$v")
        return Pair(false,"There seems to issue on our side")
    }

    override suspend fun getDoctor(doctorId: String): Doctor? {
        val res = doctorCollection.findOne(Doctor::id eq ObjectId(doctorId))
        return res
    }

    override suspend fun searchDoctorsByCategory(category: String): List<Doctor> {
        val filter = Filters.`in`("category", category)
        return doctorCollection.find(filter).toList()
    }

    override suspend fun bookAppointment(
        patientId: String,
        doctorId: String,
        appointmentDateTime: LocalDateTime
    ): AppointMents? {

        val newappointment = AppointMents(
            patientId = patientId,
            doctorId = doctorId,
            appointmentDateTime = appointmentDateTime,
            durationMinutes = 60,
            status = AppointmentStatus.PENDING,
            url = null
        )
        if (AppointmentCollection.insertOne(newappointment).wasAcknowledged()) {
            return newappointment
        } else {
            throw Exception("Not able to perform operation on database please check if given data is correct")
        }

    }

    override suspend fun cancelAppointment(appointmentid:String): Boolean {

        val filters = Filters.eq("id", appointmentid)
        val update = Updates.set("status", AppointmentStatus.CANCELLED.name)
        val updateResult = AppointmentCollection.updateOne(filters, update)

        if (updateResult.modifiedCount == 0L) {
            throw Exception("Failed to accept the appointment.")
        }
        return updateResult.wasAcknowledged()
    }

    override suspend fun getPatientAppointments(patientId: String): List<AppointMents> {
        return AppointmentCollection.find(AppointMents::patientId eq patientId).toList()
    }

    override suspend fun getDoctorReviews(doctorId: String): List<Review> {
        val doctor = doctorCollection.findOneById(ObjectId(doctorId))
            ?: throw Exception("Doctor not found with ID: $doctorId")
        val reviews = arrayListOf<Review>()
        doctorCollection.findOne(Doctor::id eq ObjectId(doctorId))?.reviews?.let { reviews.addAll(it) }
        return reviews
    }

    override suspend fun addDoctorReview(doctorId: String, review: Review): Boolean {
        val doctor = doctorCollection.findOneById(ObjectId(doctorId))
            ?: throw Exception("Doctor not found with ID: $doctorId")
        return doctorCollection.updateOne(
            Doctor::id eq ObjectId(doctorId),
            push(Doctor::reviews, review)
        ).wasAcknowledged()

    }
}