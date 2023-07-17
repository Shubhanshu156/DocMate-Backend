package com.example.data.request

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class AddCategory(
    val category:String
)

data class Category(
    val category:String,
    @BsonId val _id:ObjectId
)
@Serializable
data class CategoryResponse(
    val _id:String,
    val category:String,
)
@Serializable
data class Token(
    val token:String
)
