package com.example.Implements

//import com.example.models.Appointment

import com.example.data.request.DoctorRequest
import com.example.interfaces.DoctorService
import com.example.interfaces.Notification
import com.example.models.AppointMents
import com.example.models.AppointmentStatus
import com.example.models.Doctor
import com.example.models.Patient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class DoctorServiceImpl(private val db: CoroutineDatabase,private val NotificationService: Notification) : DoctorService {
    val doctorCollection = db.getCollection<Doctor>("doctor")
    val patientCollection = db.getCollection<Patient>("patient")
    val AppointmentCollection = db.getCollection<AppointMents>("appointment")

    override suspend fun createDoctorProfile(id: String, username: String): Boolean {
        val filter = Doctor::username eq username
        val existingDoctor = doctorCollection.findOne(filter)
        if (existingDoctor != null) {
            throw Exception("already username taken")
        }
        val job = CoroutineScope(Dispatchers.IO).async {

            doctorCollection.insertOne(Doctor(id = ObjectId(id), username = username))
        }
        val v = job.await()
        println("update result is $v")
        return v.wasAcknowledged()
    }


    override suspend fun updateDoctorProfile(id: String, request: DoctorRequest): Pair<Boolean, String> {


        val filter = Doctor::id eq ObjectId(id)
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
        val v = job.await()
        if (v.wasAcknowledged()) {
            return Pair(true, "updated Successfully")
        }
        if (v.matchedCount == 0L) {
            return Pair(false, "No such doctor found")
        }
        println("update result is$v")
        return Pair(false, "There seems to issue on our side")
    }

    override suspend fun getDoctorById(id: String): Doctor? {
        val doctorId = ObjectId(id)

        return doctorCollection.findOne(Filters.eq("_id", doctorId))
    }


    override suspend fun getDoctorAppointments(doctorId: String): List<AppointMents> {
        val appointmentslst = AppointmentCollection.find(AppointMents::doctorId eq doctorId)
        appointmentslst.let {
            return it.toList()
        }
        return emptyList()
    }

    override suspend fun getPatient(patientId: String): Patient {
        val patient = patientCollection.findOneById(patientId)
        return patient ?: throw IllegalArgumentException("Patient with ID $patientId not found.")
    }


    override suspend fun acceptAppointment(appointmentId: String): Boolean {

        val filters = and(
            AppointMents::id eq ObjectId(appointmentId),
            AppointMents::status ne AppointmentStatus.CANCELLED,
            AppointMents::status ne AppointmentStatus.REJECTED
        )
//        also updated the completed session by one
        val update = Updates.set("status", AppointmentStatus.ACCEPTED.name)
        val updateResult = AppointmentCollection.findOneAndUpdate(filters, update)
        if (updateResult ==null) {
            throw Exception("Failed to accept the appointment or it doesn't exist.")
        return false
        }
        else{
            println("update result is $updateResult")
            val doctor: Doctor? = doctorCollection.findOne(Doctor::id eq ObjectId(updateResult.doctorId))
            val patient: Patient? = patientCollection.findOne(Patient::id eq ObjectId(updateResult.patientId))
            if (doctor==null || patient==null){
                throw Exception("Either Doctor or Patient Does not Exist")
                return false
            }
            acceptAppointMentNotification(doctor,patient,updateResult)
            updateprevsssion(doctor)
            return true
        }
    }
suspend fun updateprevsssion(doctor: Doctor){
    val filter=(Doctor::id eq doctor.id)
    doctorCollection.updateOne(filter, inc(Doctor::PrevSession, 1))

}
    private suspend fun acceptAppointMentNotification(doctor: Doctor, patient: Patient, appointment: AppointMents) {
try{
        NotificationService.GenerateNotification(
            Title = "AppointMent Accepted ",
            message="Congrats!! Your AppointMent with Dr.${doctor.fullname} has been  Accepted by him",
            imageurl = Avtar.DOCTOR.imageUrl,
            tokenid = patient.token.toString(),
            time = appointment.durationMinutes.toString(),
            sender = doctor.fullname.toString(),
        )
    }
    catch (e:Exception){
        throw Exception("Unable to Send Notification ${e.localizedMessage}")
    }
    }


    override suspend fun rejectAppointment(appointmentId: String): Boolean {

        val filters = Filters.and(
            Filters.eq("_id", ObjectId(appointmentId)), Filters.ne("status", AppointmentStatus.CANCELLED.name)
        )
        val update = Updates.set("status", AppointmentStatus.REJECTED.name)
        val updateResult = AppointmentCollection.findOneAndUpdate(filters, update)

        if (updateResult==null) {
            throw Exception("Failed to reject the appointment or it doesn't exist.")
            return false
        }
        else{
            val doctor: Doctor = doctorCollection.findOne(Doctor::id eq ObjectId(updateResult.doctorId))!!
            val patient: Patient = patientCollection.findOne(Patient::id eq ObjectId(updateResult.patientId))!!
            RejectAppointMentNotification(doctor,patient,updateResult)
            return true
        }



    }

    private suspend fun RejectAppointMentNotification(doctor: Doctor, patient: Patient, updateResult: AppointMents) {
        NotificationService.GenerateNotification(
            Title = "AppointMent Rejected ",
            message="Sorry!! Your AppointMent with Dr.${doctor.fullname} has been  Rejected by him",
            imageurl = Avtar.DOCTOR.imageUrl,
            tokenid = patient.token.toString(),
            time = updateResult.durationMinutes.toString(),
            sender = doctor.fullname.toString(),
        )
    }


}