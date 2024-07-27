package RecyclerViewHelpers

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.tbMedicamento

class AdaptadorMedicamento(var Datos: List<tbMedicamento>): RecyclerView.Adapter<ViewHolderMedicamento>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMedicamento {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_medicamento, parent, false)
        return ViewHolderMedicamento(vista)
    }
    
    fun eliminarDatos(ID_enfermedad: Int, posicion: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteComentario = objConexion?.prepareStatement("DELETE FROM tbEnfermedad WHERE ID_enfermedad = ?")
            deleteComentario?.setInt(1, ID_enfermedad)
            deleteComentario?.executeUpdate()

            val commit = objConexion?.prepareStatement("commit")
            commit?.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }
    
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolderMedicamento, position: Int) {
        val item = Datos[position]
        val context = holder.itemView.context
        holder.txtMedicamentoCard.text = item.Nombre_medicamento

        holder.ImageView.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menuu, context, item, position)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, item: tbMedicamento, position: Int) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)



        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.option_2 -> {
                    eliminarDatos(item.ID_medicamento, position)
                    true
                }
                else -> false
            }
        }

        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}