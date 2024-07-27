package emily.gabriela.proyectoformativo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import emily.gabriela.proyectoformativo.ui.dashboard.DashboardFragment
import emily.gabriela.proyectoformativo.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class AgregarEnfermedades : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_enfermedades)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val imvAtrasc = findViewById<ImageView>(R.id.imvAtras)
        val btnAregar = findViewById<Button>(R.id.btnAgregar)
        val txtEnfermedad = findViewById<EditText>(R.id.txtEnfermedad)

        imvAtrasc.setOnClickListener {
            finish()
        }

        btnAregar.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO){
                try{
                    val objConexion = ClaseConexion().cadenaConexion()
                    val agregarEnfermedades = objConexion?.prepareStatement("insert into tbEnfermedad(Nombre_enfermedad, ID_medicamento) values (?, ?)")!!
                    agregarEnfermedades.setString(1, txtEnfermedad.text.toString())
                    agregarEnfermedades.setString(2, "")
                    agregarEnfermedades.executeUpdate()
                    withContext(Dispatchers.Main){
                        println("Enfermedad registrada correctamente")
                        txtEnfermedad.text.clear()
                    }

                }catch (ex: Exception){
                    withContext(Dispatchers.Main){
                        println(ex.message)
                    }
                }
            }
        }
    }
}