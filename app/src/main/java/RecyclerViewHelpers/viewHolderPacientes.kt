package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R

class viewHelperPacientes(view: View) : RecyclerView.ViewHolder(view) {
    val txtNombre : TextView = view.findViewById(R.id.txtNombre)
    val icInfo : ImageView = view.findViewById(R.id.icInfo)
}