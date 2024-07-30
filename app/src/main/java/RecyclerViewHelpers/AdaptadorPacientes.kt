package RecyclerViewHelpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import emily.gabriela.proyectoformativo.R
import emily.gabriela.proyectoformativo.activity_detallePaciente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.tbPacientes

class AdaptadorPacientes(private var Datos: List<tbPacientes>): RecyclerView.Adapter<viewHelperPacientes>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHelperPacientes {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_paciente, parent, false)
        return viewHelperPacientes(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: viewHelperPacientes, position: Int) {
        val pacientes = Datos[position]
        val nombreCompleto = holder.itemView.context.getString(R.string.nombre_completo, pacientes.Nombres, pacientes.Apellidos)
        holder.txtNombre.text = nombreCompleto
        val item = Datos[position]
        holder.icInfo.setOnClickListener {
            val context = holder.itemView.context
            val pantalladetalles = Intent(context, activity_detallePaciente::class.java)
            pantalladetalles.putExtra("ID_Paciente", item.ID_Paciente)
            pantalladetalles.putExtra("Nombres", item.Nombres)
            pantalladetalles.putExtra("Apellidos", item.Apellidos)
            pantalladetalles.putExtra("Edad", item.Edad)
            pantalladetalles.putExtra("FechaNacimiento", item.FechaNacimiento)
            pantalladetalles.putExtra("Número_habitación", item.Número_habitación)
            pantalladetalles.putExtra("Número_cama", item.Número_cama)
            pantalladetalles.putExtra("ID_medicamento", item.ID_medicamento)
            pantalladetalles.putExtra("ID_Enfermedad", item.ID_Enfermedad)
            context.startActivity(pantalladetalles)
        }
    }

    fun Actualizarlista(nuevalista: List<tbPacientes>){
        Datos = nuevalista
        notifyDataSetChanged()
    }

    fun Actualizarlistadespuesdecargardatos(ID_Paciente: Int, nuevoNombre: String, nuevoApellido: String, nuevoEdad: Int, nuevoNumero_Habitacion: Int, nuevoNumero_cama: Int, nuevaEnfermedad: Int){
        val index = Datos.indexOfFirst { it.ID_Paciente == ID_Paciente }
        Datos[index].Nombres = nuevoNombre
        Datos[index].Apellidos = nuevoApellido
        Datos[index].Edad = nuevoEdad
        Datos[index].Número_habitación = nuevoNumero_Habitacion
        Datos[index].Número_cama = nuevoNumero_cama
        Datos[index].ID_Enfermedad = nuevaEnfermedad
        notifyItemChanged(index)
    }


    fun EliminarPacientes(ID_Paciente: Int, posicion: Int) {
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        // Quitar de la base
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()
            val delProductos = objConexion?.prepareStatement("DELETE FROM tbPacientes WHERE ID_Paciente = ?")
            delProductos?.setInt(1, ID_Paciente)
            delProductos?.executeUpdate()

            val commit = objConexion?.prepareStatement("COMMIT")
            commit?.executeUpdate()
        }

        // Notificamos que se eliminaron los datos
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }


}