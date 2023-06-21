package com.example.Implements

import com.example.data.request.PatientRequest
import com.example.interfaces.Notification
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
import java.time.LocalDateTime

class PatientServiceImpl(private val db: CoroutineDatabase, private val NotificationService: Notification) :
    PatientService {
    val doctorCollection = db.getCollection<Doctor>("doctor")
    val patientCollection = db.getCollection<Patient>("patient")
    val AppointmentCollection = db.getCollection<AppointMents>("appointment")
    override suspend fun createPatientProfile(id: String, username: String): Boolean {
        val filter = Patient::username eq username
        val existingpatient = patientCollection.findOne(filter)
        if (existingpatient != null) {
            throw Exception("already username taken")
        }
        val job = CoroutineScope(Dispatchers.IO).async {
            patientCollection.insertOne(Patient(id = ObjectId(id), username = username))
        }
        val v = job.await()
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
        val v = job.await()
        if (v.wasAcknowledged()) {
            return Pair(true, "updated Successfully!!")
        }
        if (v.matchedCount == 0L) {
            return Pair(false, "No such patient found")
        }
        println("update result is$v")
        return Pair(false, "There seems to issue on our side")
    }

    override suspend fun getDoctor(doctorId: String): Doctor? {
        val idRegex = Regex("^[0-9a-fA-F]{24}$")
        val filter = if (idRegex.matches(doctorId)) {
            Doctor::id eq ObjectId(doctorId)
        } else {
            Doctor::fullname eq doctorId
        }

        val resById = doctorCollection.findOne(filter)

        val res = resById
        return res
    }

    override suspend fun getAllDoctors(): List<Doctor> {
        return doctorCollection.find().toList()

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
            val doctor: Doctor = doctorCollection.findOne(Doctor::id eq ObjectId(doctorId))!!
            val patient: Patient = patientCollection.findOne(Patient::id eq ObjectId(patientId))!!
            bookingConfirmNotification(doctor, patient,newappointment)
            return newappointment
        } else {
            throw Exception("Not able to perform operation on database please check if given data is correct")
        }

    }

    override suspend fun cancelAppointment(appointmentid: String): Boolean {

        val filters = Filters.eq("_id", ObjectId(appointmentid))
        val update = Updates.set("status", AppointmentStatus.CANCELLED.name)
        val Appointment = AppointmentCollection.findOneAndUpdate (filters, update)

        if (Appointment==null) {
            throw Exception("Failed to Cancel the appointment no appointment with such id")
            return false
        }
        else {
            val doctor: Doctor = doctorCollection.findOne(Doctor::id eq ObjectId(Appointment.doctorId))!!
            val patient: Patient = patientCollection.findOne(Patient::id eq ObjectId(Appointment.patientId))!!
            cancelNotification(doctor, patient,Appointment)
            return true
        }
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

    private suspend fun bookingConfirmNotification(doctor: Doctor, patient: Patient, newappointment: AppointMents) {
        NotificationService.GenerateNotification(
            Title = "Appointment Requested",
            message="Hi Doc!! ${patient.name} has requested a Appointment With you",
            imageurl = Avtar.PATIENT.imageUrl,
            tokenid = doctor.token.toString(),
            time = newappointment.durationMinutes.toString(),
            sender = patient.name.toString(),
        )
    }
    private suspend fun cancelNotification(doctor: Doctor, patient: Patient, appointment: AppointMents) {
        NotificationService.GenerateNotification(
            Title = "AppointMent Cancelled ",
            message="Your AppointMent with ${patient.name} has been  Cancelled by him",
            imageurl = Avtar.PATIENT.imageUrl,
            tokenid = doctor.token.toString(),
            time = appointment.durationMinutes.toString(),
            sender = patient.name.toString(),
        )
    }
    enum class Avtar(val imageUrl: String) {
        DOCTOR("https://cdn-icons-png.flaticon.com/512/2869/2869812.png"),
        PATIENT("https://cdn-icons-png.flaticon.com/512/2785/2785482.png")
    }
}
