package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R

class ViewHolderEnfermedades (view: View): RecyclerView.ViewHolder(view){
    val txtEnfermedadCard = view.findViewById<TextView>(R.id.txtEnfermedadCard)
    val ImageView = view.findViewById<ImageView>(R.id.btnEliminar)
}