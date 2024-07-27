package emily.gabriela.proyectoformativo.ui.dashboard

import RecyclerViewHelpers.AdaptadorEnfermedades
import RecyclerViewHelpers.CarouselAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import emily.gabriela.proyectoformativo.AgregarEnfermedades
import emily.gabriela.proyectoformativo.R
import emily.gabriela.proyectoformativo.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbEnfermedades

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            val agregar = Intent(requireContext(), AgregarEnfermedades::class.java)

            startActivity(agregar)
        }

        // Configurar el RecyclerView del carrusel
        val carouselRecyclerView = binding.carouselRecyclerView
        carouselRecyclerView.layoutManager = CarouselLayoutManager()

        // Configura el adaptador con los datos
        val images = listOf(
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7,
            R.drawable.image8
        )
        carouselRecyclerView.adapter = CarouselAdapter(images)
        val rcvEnfermedades = root.findViewById<RecyclerView>(R.id.rcvEnfermedades)
        rcvEnfermedades.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        fun verEnfermedades(): List<tbEnfermedades> {
            val listaEnfermedades = mutableListOf<tbEnfermedades>()
            val objConexion = ClaseConexion().cadenaConexion()

            if (objConexion == null) {
                // Manejo del caso en que la conexión es null
                println("Error: La conexión a la base de datos es nula.")
                return listaEnfermedades
            }

            try {
                val statement = objConexion.prepareStatement("select * from tbEnfermedad")
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val ID_enfermedad = resultSet.getInt("ID_enfermedad")
                    val Nombre_enfermedad = resultSet.getString("Nombre_enfermedad")
                    val enfermedad = tbEnfermedades(ID_enfermedad, Nombre_enfermedad)
                    listaEnfermedades.add(enfermedad)
                }

                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error al ejecutar la consulta: ${e.message}")
            } finally {
                objConexion.close()
            }

            return listaEnfermedades
        }



        CoroutineScope(Dispatchers.IO).launch{
            val enfermedadesDB = verEnfermedades()
            withContext(Dispatchers.Main){
                val miAdaptador = AdaptadorEnfermedades(enfermedadesDB)
                rcvEnfermedades.adapter = miAdaptador
            }
        }
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}