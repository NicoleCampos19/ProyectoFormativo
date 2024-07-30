package emily.gabriela.proyectoformativo.ui.notifications

import RecyclerViewHelpers.AdaptadorEnfermedades
import RecyclerViewHelpers.AdaptadorMedicamento
import RecyclerViewHelpers.CarouselAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import emily.gabriela.proyectoformativo.AgregarEnfermedades
import emily.gabriela.proyectoformativo.AgregarMedicamentos
import emily.gabriela.proyectoformativo.R
import emily.gabriela.proyectoformativo.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbMedicamento

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fab: FloatingActionButton = binding.fab1
        fab.setOnClickListener {
            val agregar = Intent(requireContext(), AgregarMedicamentos::class.java)

            startActivity(agregar)
        }
        val carouselRecyclerView = binding.carouselRecyclerView
        carouselRecyclerView.layoutManager = CarouselLayoutManager()

        // Configura el adaptador con los datos
        val images = listOf(
            R.drawable.image10,
            R.drawable.image11,
            R.drawable.image12,
            R.drawable.image13,
            R.drawable.image14,
            R.drawable.image15
            // Agrega más imágenes aquí
        )
        carouselRecyclerView.adapter = CarouselAdapter(images)
        val rcvMedicamentos = root.findViewById<RecyclerView>(R.id.rcvMedicamentos)
        rcvMedicamentos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        fun verMedicamentos(): List<tbMedicamento> {
            val listaMedicamento = mutableListOf<tbMedicamento>()
            val objConexion = ClaseConexion().cadenaConexion()

            if (objConexion == null) {
                // Manejo del caso en que la conexión es null
                println("Error: La conexión a la base de datos es nula.")
                return listaMedicamento
            }

            try {
                val statement = objConexion.prepareStatement("select * from tbMedicamento")
                val resultSet = statement.executeQuery()

                while (resultSet.next()) {
                    val ID_medicamento = resultSet.getInt("ID_medicamento")
                    val Nombre_medicamento = resultSet.getString("Nombre_medicamento")
                    val medicamento = tbMedicamento(ID_medicamento, Nombre_medicamento)
                    listaMedicamento.add(medicamento)
                }

                resultSet.close()
                statement.close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error al ejecutar la consulta: ${e.message}")
            } finally {
                objConexion.close()
            }

            return listaMedicamento
        }



        CoroutineScope(Dispatchers.IO).launch{
            val medicamentosDB = verMedicamentos()
            withContext(Dispatchers.Main){
                val miAdaptador = AdaptadorMedicamento(medicamentosDB)
                rcvMedicamentos.adapter = miAdaptador
            }
        }
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}