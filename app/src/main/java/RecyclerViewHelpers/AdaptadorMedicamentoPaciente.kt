package RecyclerViewHelpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R
import modelo.dataClassMedicamentosPaciente
import modelo.tbPacientes

class AdaptadorMedicamentoPaciente(
    private var items: List<dataClassMedicamentosPaciente>) : RecyclerView.Adapter<AdaptadorMedicamentoPaciente.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreMedicamento: TextView = view.findViewById(R.id.txtMedicamentoPaciente)
        val HoraAplicacion: TextView = view.findViewById(R.id.txtHoraAplicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_card_medicamento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nombreMedicamento.text = item.nombreMedicamento
        holder.HoraAplicacion.text = item.horaAplicacion
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
