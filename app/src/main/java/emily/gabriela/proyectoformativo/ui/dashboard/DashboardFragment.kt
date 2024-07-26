package emily.gabriela.proyectoformativo.ui.dashboard

import RecyclerViewHelpers.CarouselAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import emily.gabriela.proyectoformativo.R
import emily.gabriela.proyectoformativo.databinding.FragmentDashboardBinding

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}