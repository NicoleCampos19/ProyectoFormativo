package emily.gabriela.proyectoformativo

import RecyclerViewHelpers.AdaptadorMedicamentoPaciente
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import modelo.ClaseConexion
import RecyclerViewHelpers.AdaptadorPacientes
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import modelo.dataClassMedicamentosPaciente
import modelo.tbPacientes
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class activity_detallePaciente : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_paciente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()

        val imgBack = findViewById<ImageButton>(R.id.imgBack)
        val txtNombreCompleto = findViewById<TextView>(R.id.txtNombreCompleto)
        val txtEdad = findViewById<TextView>(R.id.txtEdad)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val txtNumHabitacion = findViewById<TextView>(R.id.txtNumHabitacion)
        val txtNumCama = findViewById<TextView>(R.id.txtNumCama)
        val txtEnfermedadPaciente = findViewById<TextView>(R.id.txtEnfermedadPaciente)
        val rcvPacienteMedicamento = findViewById<RecyclerView>(R.id.rcvPacienteMedicamento)
        val btnEditar = findViewById<ImageButton>(R.id.btnEditar)
        val btnBorrar = findViewById<ImageButton>(R.id.btnBorrar)


        val ID_Paciente = intent.getIntExtra("ID_Paciente", 0)
        val nombres = intent.getStringExtra("Nombres")
        val apellidos = intent.getStringExtra("Apellidos")
        val edad = intent.getIntExtra("Edad", 0)
        val fecha = intent.getStringExtra("FechaNacimiento")
        val numero_habitacion = intent.getIntExtra("Número_habitación", 0)
        val numero_cama = intent.getIntExtra("Número_cama", 0)
        val ID_Enfermedad = intent.getIntExtra("ID_Enfermedad", 0)

        val nombreCompleto = getString(R.string.nombre_completo, nombres, apellidos)
        txtNombreCompleto.text = nombreCompleto
        txtEdad.text = edad.toString()
        txtNumHabitacion.text = numero_habitacion.toString()
        txtNumCama.text = numero_cama.toString()
        txtFecha.text = fecha
        obtenerNombreEnfermedad(ID_Enfermedad, txtEnfermedadPaciente)

        rcvPacienteMedicamento.layoutManager = LinearLayoutManager(this)
        CoroutineScope(Dispatchers.Default).launch {
            val medicamentosPaciente = mostrarPacientesMedicamento(ID_Paciente)
            withContext(Dispatchers.Main) {
                val miAdaptador = AdaptadorMedicamentoPaciente(medicamentosPaciente)
                rcvPacienteMedicamento.adapter = miAdaptador
            }
        }

        imgBack.setOnClickListener {
            finish()
        }

        btnBorrar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmación")
            builder.setMessage("¿Quieres eliminar este paciente?")

            builder.setPositiveButton("Sí") { dialog, which ->
                EliminarPaciente(ID_Paciente)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        btnEditar.setOnClickListener {
            mostrarDialogoEdicion(
                ID_Paciente,
                nombres,
                apellidos,
                edad,
                ID_Enfermedad,
                numero_habitacion,
                numero_cama
            )
        }
    }

    private fun obtenerNombreEnfermedad(ID_Enfermedad: Int, txtEnfermedadPaciente: TextView) {
        GlobalScope.launch(Dispatchers.IO) {
            val nombreEnfermedad = obtenerNombreEnfermedadPorId(ID_Enfermedad)
            withContext(Dispatchers.Main) {
                txtEnfermedadPaciente.text = nombreEnfermedad
            }
        }
    }

    private fun EliminarPaciente(ID_Paciente: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                if (objConexion != null) {
                    val preparedStatement =
                        objConexion.prepareStatement("DELETE FROM tbPacientes WHERE ID_Paciente = ?")
                    preparedStatement.setInt(1, ID_Paciente)
                    preparedStatement.executeUpdate()
                    val commit = objConexion.prepareStatement("commit")!!
                    commit.executeUpdate()
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_detallePaciente, "Paciente eliminado", Toast.LENGTH_SHORT).show()
                    val backIntent = Intent(this@activity_detallePaciente, MainActivity::class.java)
                    startActivity(backIntent)
                    finish()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_detallePaciente, "Error al eliminar el paciente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarPaciente(idPaciente: Int, nuevoNombre: String, nuevoApellido: String, nuevaEdad: Int, nuevaEnfermedad: Int, nuevoNumeroHabitacion: Int, nuevoNumeroCama: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val objConexion = ClaseConexion().cadenaConexion()
                if (objConexion != null) {
                    val preparedStatement =
                        objConexion.prepareStatement("UPDATE tbPacientes SET Nombres = ?, Apellidos = ?, Edad = ?, Número_habitación = ?, Número_cama = ?, ID_Enfermedad = ? WHERE ID_Paciente = ?")
                    preparedStatement.setString(1, nuevoNombre)
                    preparedStatement.setString(2, nuevoApellido)
                    preparedStatement.setInt(3, nuevaEdad)
                    preparedStatement.setInt(4, nuevoNumeroHabitacion)
                    preparedStatement.setInt(5, nuevoNumeroCama)
                    preparedStatement.setInt(6, nuevaEnfermedad)
                    preparedStatement.setInt(7, idPaciente)
                    preparedStatement.executeUpdate()

                    val commit = objConexion.prepareStatement("commit")!!
                    commit.executeUpdate()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@activity_detallePaciente,
                            "Paciente actualizado",
                            Toast.LENGTH_SHORT
                        ).show()
                        val volver = Intent(this@activity_detallePaciente, MainActivity::class.java)
                        startActivity(volver)
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_detallePaciente, "Error al actualizar el paciente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun mostrarDialogoEdicion(
        ID_Paciente: Int,
        nombres: String?,
        apellidos: String?,
        edad: Int,
        ID_Enfermedad: Int,
        numero_habitacion: Int,
        numero_cama: Int
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val listaEnfermedades = obtenerListaEnfermedades()

            withContext(Dispatchers.Main) {
                val builder = AlertDialog.Builder(this@activity_detallePaciente, R.style.CustomAlertDialog)
                val txtNuevoNombrePaciente = EditText(this@activity_detallePaciente).apply { setText(nombres) }
                val txtNuevoApellidoPaciente = EditText(this@activity_detallePaciente).apply { setText(apellidos) }
                val txtNuevaEdad = EditText(this@activity_detallePaciente).apply { setText(edad.toString()) }
                val spNuevaEnfermedad = Spinner(this@activity_detallePaciente).apply {
                    val adapter = ArrayAdapter(this@activity_detallePaciente, android.R.layout.simple_spinner_item, listaEnfermedades.map { it.second })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    this.adapter = adapter

                    val posicionSeleccionada = listaEnfermedades.indexOfFirst { it.first == ID_Enfermedad }
                    if (posicionSeleccionada != -1) {
                        setSelection(posicionSeleccionada)
                    }
                }
                val txtNuevoNumeroHabitacion = EditText(this@activity_detallePaciente).apply { setText(numero_habitacion.toString()) }
                val txtNuevoNumCama = EditText(this@activity_detallePaciente).apply { setText(numero_cama.toString()) }

                val layout = LinearLayout(this@activity_detallePaciente).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(txtNuevoNombrePaciente)
                    addView(txtNuevoApellidoPaciente)
                    addView(txtNuevaEdad)
                    addView(spNuevaEnfermedad)
                    addView(txtNuevoNumeroHabitacion)
                    addView(txtNuevoNumCama)
                }
                builder.setView(layout)

                builder.setPositiveButton("Guardar") { dialog, which ->
                    val nuevoNombre = txtNuevoNombrePaciente.text.toString()
                    val nuevoApellido = txtNuevoApellidoPaciente.text.toString()
                    val nuevaEdad = txtNuevaEdad.text.toString().toInt()
                    val nuevaEnfermedad = listaEnfermedades[spNuevaEnfermedad.selectedItemPosition].first
                    val nuevoNumeroHabitacion = txtNuevoNumeroHabitacion.text.toString().toInt()
                    val nuevoNumeroCama = txtNuevoNumCama.text.toString().toInt()

                    actualizarPaciente(
                        ID_Paciente,
                        nuevoNombre,
                        nuevoApellido,
                        nuevaEdad,
                        nuevaEnfermedad,
                        nuevoNumeroHabitacion,
                        nuevoNumeroCama
                    )
                }
                builder.setNegativeButton("Cancelar", null)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun obtenerListaEnfermedades(): List<Pair<Int, String>> {
        val listaEnfermedades = mutableListOf<Pair<Int, String>>()
        val conexion = ClaseConexion().cadenaConexion()
        conexion?.use { conn ->
            try {
                val query = "SELECT ID_enfermedad, Nombre_enfermedad FROM tbEnfermedad"
                val statement = conn.createStatement()
                val resultSet: ResultSet = statement.executeQuery(query)

                while (resultSet.next()) {
                    val id = resultSet.getInt("ID_enfermedad")
                    val nombre = resultSet.getString("Nombre_enfermedad")
                    listaEnfermedades.add(id to nombre)
                }
            } catch (e: SQLException) {
                Log.e("Problema_DB", "Error en consulta: ${e.message}", e)
            }
        }
        return listaEnfermedades
    }

    private suspend fun obtenerNombreEnfermedadPorId(ID_Enfermedad: Int): String? {
        return withContext(Dispatchers.IO) {
            var nombreEnfermedad: String? = null
            val conexion = ClaseConexion().cadenaConexion()
            conexion?.use {
                try {
                    val query = "SELECT Nombre_enfermedad FROM tbEnfermedad WHERE ID_enfermedad = ?"
                    val preparedStatement = it.prepareStatement(query)
                    preparedStatement.setInt(1, ID_Enfermedad)

                    val resultSet: ResultSet = preparedStatement.executeQuery()
                    if (resultSet.next()) {
                        nombreEnfermedad = resultSet.getString("Nombre_enfermedad")
                    } else {

                    }
                } catch (e: SQLException) {
                    Log.e("Problema_DB", "Error en consulta: ${e.message}", e)
                }
            }
            nombreEnfermedad
        }
    }

    suspend fun mostrarPacientesMedicamento(ID_Paciente: Int): List<dataClassMedicamentosPaciente> = withContext(Dispatchers.Default) {
        val listaMedicamentos = mutableListOf<dataClassMedicamentosPaciente>()
        var objConexion: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {
            objConexion = ClaseConexion().cadenaConexion()
            if (objConexion != null) {
                preparedStatement = objConexion.prepareStatement(""" 
                     SELECT 
                     m.ID_Medicamento,
                     m.Nombre_medicamento,
                     m.Hora_Aplicación
                     FROM
                         tbPacientes p
                     INNER JOIN 
                         tbMedicamento m ON p.ID_Medicamento = m.ID_Medicamento
                     WHERE p.ID_Paciente = ?
                """.trimIndent())
                preparedStatement.setInt(1, ID_Paciente)
                resultSet = preparedStatement.executeQuery()
                while (resultSet.next()) {
                    val Nombre_medicamento = resultSet.getString("Nombre_medicamento")
                    val Hora_Aplicación = resultSet.getString("Hora_Aplicación")
                    val ID_Medicamento = resultSet.getInt("ID_Medicamento")
                    val medicamento = dataClassMedicamentosPaciente(ID_Paciente, Nombre_medicamento, Hora_Aplicación, ID_Medicamento)
                    listaMedicamentos.add(medicamento)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                resultSet?.close()
                preparedStatement?.close()
                objConexion?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return@withContext listaMedicamentos
    }
}
