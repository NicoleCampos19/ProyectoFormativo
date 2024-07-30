package emily.gabriela.proyectoformativo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class AgregarMedicamentos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_medicamentos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        val imvAtrasc = findViewById<ImageView>(R.id.imvAtras)
        val btnAgregarMedicamento = findViewById<Button>(R.id.btnAgregarMedicamento)
        val txtMedicamento = findViewById<EditText>(R.id.txtMedicamento)
        imvAtrasc.setOnClickListener {

            finish()
        }

        btnAgregarMedicamento.setOnClickListener{
            GlobalScope.launch(Dispatchers.IO){
                try{
                    val objConexion = ClaseConexion().cadenaConexion()
                    val agregarMedicamentos = objConexion?.prepareStatement("insert into tbMedicamento(Nombre_medicamento) values (?)")!!
                    agregarMedicamentos.setString(1, txtMedicamento.text.toString())
                    agregarMedicamentos.executeUpdate()
                    withContext(Dispatchers.Main){
                        println("Enfermedad registrada correctamente")
                        txtMedicamento.text.clear()
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



