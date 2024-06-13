import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/Usuario/autenticar")
    fun autenticarUsuario(
        @Query("nombre") nombre: String,
        @Query("contrasena") contrasena: String
    ): Call<ResponseBody>