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
            val volverAtras = Intent(this, DashboardViewModel::class.java)
            startActivity(volverAtras)
        }
    }
}