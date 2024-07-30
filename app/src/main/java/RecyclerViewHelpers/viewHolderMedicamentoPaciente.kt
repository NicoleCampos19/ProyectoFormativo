package RecyclerViewHelpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R

class viewHolderMedicamentoPaciente (view: View): RecyclerView.ViewHolder(view){
    val txtMedicamentoPaciente = view.findViewById<TextView>(R.id.txtMedicamentoPaciente)
}