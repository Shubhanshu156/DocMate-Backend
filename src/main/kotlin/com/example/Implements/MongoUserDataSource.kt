package com.example.Implements

import com.example.interfaces.UserDataSource
import com.example.models.Doctor
import com.example.models.Patient
import com.example.models.User
import com.mongodb.client.model.Filters.and
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoUserDataSource(db:CoroutineDatabase):UserDataSource{
    val users=db.getCollection<User>()
    val DoctorCollection=db.getCollection<Doctor>()
    val PatientCollection=db.getCollection<Patient>()
    override suspend fun getUserByUserNameType(username:String,type:String): User? {
        return users.findOne(and(User::username eq username, User::type eq type))


    }

    override suspend fun insertUser(user: User): Boolean {
        val filter = User::username eq user.username
        val existinguser = users.findOne(filter)
        if (existinguser!=null){
            throw Exception("User name Already Taken")
            return false
        }
      return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun UpdateToken(type: String, id: String, token: String): Boolean {
        val filter = and(
            User::id eq ObjectId(id),
            User::type eq type
        )
        val existingUser = users.findOne(filter)
        if (existingUser == null) {
            throw Exception("User not found")
            return false
        }
        return when (type.lowercase()) {
            "patient" -> {
                val update = set(Patient::token setTo token)
                users.updateOne(filter, update).wasAcknowledged()
            }
            "doctor" -> {
                val update = set(Doctor::token setTo token)
                users.updateOne(filter, update).wasAcknowledged()
            }
            else -> false
        }
    }

}