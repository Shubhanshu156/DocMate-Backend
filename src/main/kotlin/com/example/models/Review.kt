package com.example.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


data class Review(
   @BsonId val id:ObjectId=ObjectId(),
    val message:String?,
    val patientId:String,
    val star:String,

)
