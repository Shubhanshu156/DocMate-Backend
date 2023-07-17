package com.example.Implements

import com.example.data.request.Category
import com.example.data.request.CategoryResponse
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
import kotlin.math.roundToInt

class PatientServiceImpl(private val db: CoroutineDatabase, private val NotificationService: Notification) :
    PatientService {
    val doctorCollection = db.getCollection<Doctor>("doctor")
    val patientCollection = db.getCollection<Patient>("patient")
    val AppointmentCollection = db.getCollection<AppointMents>("appointment")
    val categorycolleection = db.getCollection<Category>("category")
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

    override suspend fun getTopDoctors(): List<Doctor> {
        return doctorCollection.find()
            .sort(descending(Doctor::rating))
            .limit(5) // Adjust the limit as per your requirement
            .toList()

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
        println("call to bookappointment")
        try {
            val doctor: Doctor? = doctorCollection.findOne(Doctor::id eq ObjectId(doctorId))
            val patient: Patient? = patientCollection.findOne(Patient::id eq ObjectId(patientId))
            if (doctor != null && patient != null) {
                println("Doctor and patient found")
                if (AppointmentCollection.insertOne(newappointment).wasAcknowledged()) {
                    bookingConfirmNotification(doctor, patient, newappointment)
                    return newappointment
                } else {
                    // Handle case when doctor or patient is not found
                    println("There is something wrong")
                    throw Exception("Not Able to perform operation something wrong")
                }
            } else {
                println("Doctor or Patient not Found")
                throw Exception("Doctor or Patient not Found")
            }
        } catch (e: Exception) {
            print("local error message is ${e.localizedMessage}")
            throw Exception(e.message)
        }

    }

    override suspend fun getcategory(): List<CategoryResponse> {
        var ans = categorycolleection.find().toList()
        var catres = ans.map {
            CategoryResponse(it._id.toString(), it.category)
        }
        return catres
    }

    override suspend fun cancelAppointment(appointmentid: String): Boolean {

        val filters = Filters.eq("_id", ObjectId(appointmentid))
        val update = Updates.set("status", AppointmentStatus.CANCELLED.name)
        val Appointment = AppointmentCollection.findOneAndUpdate(filters, update)

        if (Appointment == null) {
            throw Exception("Failed to Cancel the appointment no appointment with such id")
            return false
        } else {
            val doctor: Doctor = doctorCollection.findOne(Doctor::id eq ObjectId(Appointment.doctorId))!!
            val patient: Patient = patientCollection.findOne(Patient::id eq ObjectId(Appointment.patientId))!!
            cancelNotification(doctor, patient, Appointment)
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
        val ratingarr: MutableList<Int> = doctor.ratingArray.toMutableList()
//        count this rating in rating array by 1
        if (review.star in 1..5) {
            ratingarr[review.star - 1]++

            val newrating = getRating(ratingarr)
            val newarr = ratingarr.toList()
            val filter = Doctor::id eq ObjectId(doctorId)
            val update = combine(
                push(Doctor::reviews, review),
                set(Doctor::ratingArray setTo newarr),
                set(Doctor::rating setTo newrating)
            )

            val updateResult = doctorCollection.updateOne(filter, update)


            return updateResult.wasAcknowledged()
        }
        return false

    }

    private suspend fun bookingConfirmNotification(doctor: Doctor, patient: Patient, newappointment: AppointMents) {
        NotificationService.GenerateNotification(
            Title = "Appointment Requested",
            message = "Hi Doc!! ${patient.name} has requested a Appointment With you",
            imageurl = Avtar.PATIENT.imageUrl,
            tokenid = doctor.token.toString(),
            time = newappointment.durationMinutes.toString(),
            sender = patient.name.toString(),
        )
    }

    private suspend fun cancelNotification(doctor: Doctor, patient: Patient, appointment: AppointMents) {
        NotificationService.GenerateNotification(
            Title = "AppointMent Cancelled ",
            message = "Your AppointMent with ${patient.name} has been  Cancelled by him",
            imageurl = Avtar.PATIENT.imageUrl,
            tokenid = doctor.token.toString(),
            time = appointment.durationMinutes.toString(),
            sender = patient.name.toString(),
        )
    }

    fun getRating(ratingCount: MutableList<Int>): Int {
        val totalRatingCount = ratingCount.sum()
        if (totalRatingCount == 0) {
            return 0
        }

        var weightedRatingSum = 0
        for (i in 0..4) {
            weightedRatingSum += (i + 1) * ratingCount[i]
        }

        return (weightedRatingSum.toDouble() / totalRatingCount).roundToInt()
    }
}

enum class Avtar(val imageUrl: String) {
    DOCTOR("https://cdn-icons-png.flaticon.com/512/2869/2869812.png"),
    PATIENT("https://cdn-icons-png.flaticon.com/512/2785/2785482.png")
}


