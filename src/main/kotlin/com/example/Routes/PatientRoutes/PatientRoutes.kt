import com.example.Routes.PatientRoutes.*
import com.example.interfaces.PatientService
import io.ktor.server.routing.*

fun Route.PatientRoutes(PatientService: PatientService) {
    Profile(PatientService)
    getReviews(PatientService)
    addReview(PatientService)
    bookappointment(PatientService)
    cancelAppointMent(PatientService)
    getappointment(PatientService)
    SearchDoctor(PatientService)
    GetCategories(PatientService)
    getDoctor(PatientService)
    getAllDoctors(PatientService)
    getTopDoctors(PatientService)
    getSlots(PatientService)
}