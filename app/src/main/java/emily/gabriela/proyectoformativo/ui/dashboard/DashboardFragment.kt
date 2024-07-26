package emily.gabriela.proyectoformativo.ui.dashboard

import RecyclerViewHelpers.AdaptadorEnfermedades
import RecyclerViewHelpers.CarouselAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.withCreated
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
            // Realiza alguna acción aquí
        }

        // Configurar el RecyclerView del carrusel
        val carouselRecyclerView = binding.carouselRecyclerView
        carouselRecyclerView.layoutManager = CarouselLayoutManager()

        // Configura el adaptador con los datos
        val images = listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3
            // Agrega más imágenes aquí
        )
        carouselRecyclerView.adapter = CarouselAdapter(images)
        val rcvEnfermedades = root.findViewById<RecyclerView>(R.id.rcvEnfermedades)
        rcvEnfermedades.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        fun verEnfermedades(): List<tbEnfermedades>{
            val objConexion = ClaseConexion().cadenaConexion()
            val statement = objConexion?.prepareStatement("select * from tbEnfermedad")!!

            val resultset = statement.executeQuery()

            val listaEnfermedades = mutableListOf<tbEnfermedades>()

            while(resultset.next()){
                val ID_enfermedad = resultset.getInt("ID_enfermedad")
                val Nombre_enfermedad = resultset.getString("Nombre_enfermedad")

                val enfermedad =tbEnfermedades(ID_enfermedad, Nombre_enfermedad)
                listaEnfermedades.add(enfermedad)
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