package com.example.Implements

import com.example.data.request.AddCategory
import com.example.interfaces.AdminServices
import com.example.models.User
import com.mongodb.client.model.Filters
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class AdminServicesImp(db:CoroutineDatabase):AdminServices {
    val users=db.getCollection<AddCategory>("category")
    override suspend fun AddCategory(category: AddCategory): Boolean {
        return users.insertOne(category).wasAcknowledged()
    }
}