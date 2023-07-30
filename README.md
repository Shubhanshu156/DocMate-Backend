# Doctor-Patient Appointment Booking Backend with Ktor and Atlas MongoDB

## Introduction

This project serves as the backend for the Doctor-Patient Appointment Booking Android Application. It is built using Ktor, a powerful asynchronous web framework for Kotlin, and Atlas MongoDB, a cloud-based database service. The backend provides APIs to handle user authentication, appointment management, and communication between doctors and patients.

## Features

1. **User Authentication**
   - Users can register and create accounts.
   - Existing users can log in securely with their credentials.
   - Token-based authentication is used to validate user sessions.

2. **Doctor Profile Management**
   - Doctors can create and manage their profiles.
   - Profile information includes name, specialty, contact details, and availability.
   - Doctors can update their availability status for appointment booking.

3. **Patient Profile Management**
   - Patients can create and maintain their profiles.
   - Profile information includes name, contact details, and medical history.

4. **Appointment Request and Scheduling**
   - Patients can request appointments with specific doctors.
   - Doctors receive appointment requests and can accept or decline them based on availability.
   - Once accepted, appointments are scheduled and saved in the database.
   - Both doctors and patients can view their scheduled appointments.

5. **Real-time Communication**
   - The backend handles real-time communication between doctors and patients.
   - It enables notifications for appointment status updates, ensuring a seamless experience.

6. **Security**
   - Passwords are stored securely using encryption techniques.
   - Token-based authentication prevents unauthorized access.
This Backend is implemented in Android Application named [DocMate](https://github.com/Shubhanshu156/DocMate-Android)
