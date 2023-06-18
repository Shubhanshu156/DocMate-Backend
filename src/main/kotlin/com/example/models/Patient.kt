package com.example.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Patient(
    val username:String,
    val password:String,
    val salt: String,
    @BsonId val id: ObjectId = ObjectId()
)
