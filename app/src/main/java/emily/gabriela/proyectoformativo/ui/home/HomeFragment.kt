package emily.gabriela.proyectoformativo.ui.home

import RecyclerViewHelpers.AdaptadorPacientes
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import emily.gabriela.proyectoformativo.AgregarMedicamentos
import emily.gabriela.proyectoformativo.AgregarPacientes
import emily.gabriela.proyectoformativo.R
import emily.gabriela.proyectoformativo.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbPacientes
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class HomeFragment : Fragment() {
    private lateinit var adaptadorPacientes: AdaptadorPacientes
    private lateinit var rcvPacientes: RecyclerView
    private var listaPacientes: MutableList<tbPacientes> = mutableListOf()
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val fab: FloatingActionButton = binding.fab1
        fab.setOnClickListener {
            val agregar = Intent(requireContext(), AgregarPacientes::class.java)
            startActivity(agregar)
        }
        val root: View = binding.root


        val rcvPacientes = root.findViewById<RecyclerView>(R.id.rcvPacientes)
        rcvPacientes.layoutManager = LinearLayoutManager(requireContext())
        CoroutineScope(Dispatchers.IO).launch{
            val pacientesDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val miAdaptador = AdaptadorPacientes(pacientesDB)
                rcvPacientes.adapter = miAdaptador

            }
        }
        fun actualizarDatos() {
            CoroutineScope(Dispatchers.IO).launch {
                val pacientesDB = obtenerDatos()
                withContext(Dispatchers.Main) {
                    val miAdaptador = AdaptadorPacientes(pacientesDB)
                    rcvPacientes.adapter = miAdaptador
                    miAdaptador.Actualizarlista(pacientesDB)
                }
            }
        }
        obtenerDatos()
        actualizarDatos()
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rcvPacientes = view.findViewById(R.id.rcvPacientes)
        adaptadorPacientes = AdaptadorPacientes(listaPacientes)
        rcvPacientes.adapter = adaptadorPacientes
        rcvPacientes.layoutManager = LinearLayoutManager(context)

        cargarPacientes()
    }
    private fun cargarPacientes() {
        GlobalScope.launch(Dispatchers.IO) {
            val listaPacientes = obtenerListaPacientesDesdeBaseDeDatos()
            withContext(Dispatchers.Main) {
                adaptadorPacientes.Actualizarlista(listaPacientes)
            }
        }
    }


    private fun obtenerListaPacientesDesdeBaseDeDatos(): List<tbPacientes> {
        val listaPacientes = mutableListOf<tbPacientes>()
        val conexion = ClaseConexion().cadenaConexion()
        conexion?.use { conn ->
            try {
                val query = "SELECT * FROM tbPacientes"
                val statement = conn.createStatement()
                val resultSet: ResultSet = statement.executeQuery(query)

                while (resultSet.next()) {
                    val paciente = tbPacientes(
                        resultSet.getInt("ID_Paciente"),
                        resultSet.getString("Nombres"),
                        resultSet.getString("Apellidos"),
                        resultSet.getInt("Edad"),
                        resultSet.getString("FechaNacimiento"),
                        resultSet.getInt("Número_habitación"),
                        resultSet.getInt("Número_cama"),
                        resultSet.getInt("ID_medicamento"),
                        resultSet.getInt("ID_Enfermedad")
                    )
                    listaPacientes.add(paciente)

                    val commit = conexion?.prepareStatement("COMMIT")
                    commit?.executeUpdate()
                }
            } catch (e: SQLException) {
                Log.e("Problema_DB", "Error en consulta: ${e.message}", e)
            }
        }
        return listaPacientes
    }
}

    private fun obtenerDatos(): List<tbPacientes> {
        val pacientes = mutableListOf<tbPacientes>()
        val objConexion = ClaseConexion().cadenaConexion()
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {
            if (objConexion != null) {
                statement = objConexion.createStatement()
                resultSet = statement.executeQuery("SELECT * FROM tbPacientes")!!
                while (resultSet.next()) {
                    val ID_Paciente = resultSet.getInt("ID_Paciente")
                    val Nombres = resultSet.getString("Nombres")
                    val Apellidos = resultSet.getString("Apellidos")
                    val Edad = resultSet.getInt("Edad")
                    val FechaNacimiento = resultSet.getString("FechaNacimiento")
                    val Número_habitación = resultSet.getInt("Número_habitación")
                    val Número_cama = resultSet.getInt("Número_cama")
                    val ID_medicamento = resultSet.getInt("ID_medicamento")
                    val ID_Enfermedad = resultSet.getInt("ID_Enfermedad")
                    val paciente = tbPacientes(ID_Paciente , Nombres, Apellidos, Edad, FechaNacimiento, Número_habitación, Número_cama, ID_medicamento, ID_Enfermedad)
                    pacientes.add(paciente)
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

        return pacientes
    }