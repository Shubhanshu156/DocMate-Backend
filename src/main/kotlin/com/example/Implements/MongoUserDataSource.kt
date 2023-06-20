package com.example.Implements

import com.example.interfaces.UserDataSource
import com.example.models.User
import com.mongodb.client.model.Filters.and
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(db:CoroutineDatabase):UserDataSource{
    val users=db.getCollection<User>()
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
}