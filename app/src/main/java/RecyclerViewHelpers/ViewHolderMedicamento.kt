package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R

class ViewHolderMedicamento (view: View): RecyclerView.ViewHolder(view){
    val txtMedicamentoCard = view.findViewById<TextView>(R.id.txtMedicamentoCard)
    val ImageView = view.findViewById<ImageView>(R.id.menu_button)
}