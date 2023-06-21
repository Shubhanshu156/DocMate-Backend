package com.example.di

import com.example.FirebaseAdmin
import com.example.Implements.*
import com.example.Security.JwtTokenService
import com.example.Security.TokenConfig
import com.example.Security.hasing.SHA256HashingService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val appModule = module {
    val mongopw = System.getenv("MONGO_PW")
    val dbname = "DocMate"

    single {
        KMongo.createClient(
            connectionString = "mongodb+srv://admin-shubhanshu:$mongopw@cluster0.f0g4klw.mongodb.net/$dbname?retryWrites=true&w=majority"
        )
            .coroutine
            .getDatabase(dbname)
    }

    single { MongoUserDataSource(get()) }

    single {
        JwtTokenService()
    }


    single { SHA256HashingService() }

    single { FirebaseAdmin.init() }

    single {
        AdminServicesImp(get())
    }

    single {
        DoctorServiceImpl(get(), FirebaseNotification())
    }

    single {
        PatientServiceImpl(get(), FirebaseNotification())
    }
}
