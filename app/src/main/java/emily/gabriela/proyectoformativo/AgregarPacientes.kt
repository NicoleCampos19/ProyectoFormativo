package emily.gabriela.proyectoformativo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbEnfermedades
import modelo.tbMedicamentosSpinner
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.Calendar

class AgregarPacientes : AppCompatActivity() {

    // Declarar las listas como variables de clase para que sean accesibles en toda la clase
    private lateinit var listaMedicamentos: List<tbMedicamentosSpinner>
    private lateinit var listaEnfermedad: List<tbEnfermedades>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_pacientes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()

        val txtNombresPaciente = findViewById<TextView>(R.id.txtNombresPaciente)
        val txtApellidosPaciente = findViewById<TextView>(R.id.txtApellidosPaciente)
        val txtEdadPaciente = findViewById<TextView>(R.id.txtEdadPaciente)
        val txtFechaPaciente = findViewById<TextView>(R.id.txtFechaPaciente)
        val txtnumHabitacionPaciente = findViewById<TextView>(R.id.txtNumHabiPaciente)
        val txtnumCamaPaciente = findViewById<TextView>(R.id.txtNumCamaPaciente)
        val spMedicamentoPaciente = findViewById<Spinner>(R.id.spMedicamentoPaciente)
        val spEnfermedadPaciente = findViewById<Spinner>(R.id.spEnfermedadPaciente)
        val btnAgregarPaciente = findViewById<Button>(R.id.txtAgregarPaciente)
        val imgRegresar = findViewById<ImageButton>(R.id.imgRegresar)

        imgRegresar.setOnClickListener {
            val regresar = Intent(this@AgregarPacientes, MainActivity::class.java)
            startActivity(regresar)
        }

        // Inicializar lista de medicamentos y configurar el adaptador
        CoroutineScope(Dispatchers.IO).launch {
            listaMedicamentos = obtenerMedicamentos()
            val nombreMedicamento = listaMedicamentos.map { it.Nombre_Medicamento }
            withContext(Dispatchers.Main) {
                val miAdaptador = ArrayAdapter(
                    this@AgregarPacientes,
                    android.R.layout.simple_spinner_dropdown_item,
                    nombreMedicamento
                )
                spMedicamentoPaciente.adapter = miAdaptador
            }
        }

        // Inicializar lista de enfermedades y configurar el adaptador
        CoroutineScope(Dispatchers.IO).launch {
            listaEnfermedad = obtenerEnfermedades()
            val nombreEnfermedad = listaEnfermedad.map { it.Nombre_enfermedad }
            withContext(Dispatchers.Main) {
                val miAdaptador = ArrayAdapter(
                    this@AgregarPacientes,
                    android.R.layout.simple_spinner_dropdown_item,
                    nombreEnfermedad
                )
                spEnfermedadPaciente.adapter = miAdaptador
            }
        }

        // Configurar el DatePicker para fechas de nacimiento
        txtFechaPaciente.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anioActual = calendario.get(Calendar.YEAR)
            val mesActual = calendario.get(Calendar.MONTH)
            val diaActual = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fechaSeleccionada =
                        String.format("%02d/%02d/%04d", diaSeleccionado, mesSeleccionado + 1, anioSeleccionado)
                    txtFechaPaciente.text = fechaSeleccionada
                },
                anioActual, mesActual, diaActual
            )

            val fechaMinima = calendario.apply {
                set(1900, 0, 1)
            }.timeInMillis
            datePickerDialog.datePicker.minDate = fechaMinima
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

            datePickerDialog.show()
        }

        // Acción para agregar paciente a la base de datos
        btnAgregarPaciente.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {

                    val objConexion = ClaseConexion().cadenaConexion()

                    val addPaciente = objConexion?.prepareStatement(
                        "INSERT INTO tbPacientes(Nombres, Apellidos, Edad, FechaNacimiento, Número_habitación, Número_cama, ID_Medicamento, ID_Enfermedad) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    )!!

                    addPaciente.setString(1, txtNombresPaciente.text.toString())
                    addPaciente.setString(2, txtApellidosPaciente.text.toString())
                    addPaciente.setInt(3, txtEdadPaciente.text.toString().toInt())
                    addPaciente.setString(4, txtFechaPaciente.text.toString())
                    addPaciente.setInt(5, txtnumHabitacionPaciente.text.toString().toInt())
                    addPaciente.setInt(6, txtnumCamaPaciente.text.toString().toInt())

                    // Obtener el índice y luego el ID de los elementos seleccionados
                    val selectedMedicamentoIndex = spMedicamentoPaciente.selectedItemPosition
                    val selectedEnfermedadIndex = spEnfermedadPaciente.selectedItemPosition

                    val selectedMedicamentoId = listaMedicamentos[selectedMedicamentoIndex].ID_Medicamento
                    val selectedEnfermedadId = listaEnfermedad[selectedEnfermedadIndex].ID_enfermedad

                    addPaciente.setInt(7, selectedMedicamentoId)
                    addPaciente.setInt(8, selectedEnfermedadId)

                    addPaciente.executeUpdate()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AgregarPacientes, "Paciente agregado con éxito", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AgregarPacientes, MainActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AgregarPacientes, "Error al agregar el paciente: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    e.printStackTrace()
                }
            }
        }
    }

    // Obtener la lista de medicamentos de la base de datos
    private suspend fun obtenerMedicamentos(): List<tbMedicamentosSpinner> = withContext(Dispatchers.IO) {
        val listaMedicamentos = mutableListOf<tbMedicamentosSpinner>()
        var objConexion: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {
            objConexion = ClaseConexion().cadenaConexion()
            statement = objConexion?.createStatement()

            if (statement != null) {
                resultSet = statement.executeQuery("SELECT * FROM tbMedicamento")

                while (resultSet.next()) {
                    val ID_Medicamento = resultSet.getInt("ID_Medicamento")
                    val Nombre_Medicamento = resultSet.getString("Nombre_medicamento")
                    val Hora_Aplicación = resultSet.getString("Hora_Aplicación")
                    val mediCompleto = tbMedicamentosSpinner(ID_Medicamento, Nombre_Medicamento, Hora_Aplicación)
                    listaMedicamentos.add(mediCompleto)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                resultSet?.close()
                statement?.close()
                objConexion?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return@withContext listaMedicamentos
    }

    // Obtener la lista de enfermedades de la base de datos
    private suspend fun obtenerEnfermedades(): List<tbEnfermedades> = withContext(Dispatchers.IO) {
        val listaEnfermedades = mutableListOf<tbEnfermedades>()
        var objConexion: Connection? = null
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {
            objConexion = ClaseConexion().cadenaConexion()
            statement = objConexion?.createStatement()

            if (statement != null) {
                resultSet = statement.executeQuery("SELECT * FROM tbEnfermedad")

                while (resultSet.next()) {
                    val ID_Enfermedad = resultSet.getInt("ID_Enfermedad")
                    val Nombre_enfermedad = resultSet.getString("Nombre_enfermedad")
                    val enferCompleto =
                        tbEnfermedades(ID_Enfermedad, Nombre_enfermedad)
                    listaEnfermedades.add(enferCompleto)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                resultSet?.close()
                statement?.close()
                objConexion?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return@withContext listaEnfermedades
    }
}
