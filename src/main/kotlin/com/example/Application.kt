package com.example

import com.example.Implements.AdminServicesImp
import com.example.Implements.DoctorServiceImpl
import com.example.Implements.MongoUserDataSource
import com.example.Security.JwtTokenService
import com.example.Security.TokenConfig
import com.example.Security.hasing.SHA256HashingService
import com.example.interfaces.DoctorService
import com.example.plugins.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.io.FileInputStream


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongopw = System.getenv("MONGO_PW")
    val dbname = "DocMate"

    val db =
        KMongo.createClient(connectionString = "mongodb+srv://admin-shubhanshu:$mongopw@cluster0.f0g4klw.mongodb.net/$dbname?retryWrites=true&w=majority")
            .coroutine
            .getDatabase(dbname)

    val userDataSource = MongoUserDataSource(db)

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    FirebaseAdmin.init()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig,AdminServicesImp(db),DoctorServiceImpl(db))
    configureSerialization()
    configureMonitoring()



}
fun initializeFirebase() {
    val serviceAccount = FileInputStream("src/main/resources/docmate.json")

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)
}