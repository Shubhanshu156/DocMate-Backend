package com.example.interfaces

import com.example.data.request.AddCategory

interface AdminServices {
    suspend fun AddCategory(category: AddCategory):Boolean
}