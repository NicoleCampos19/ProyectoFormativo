package modelo

data class tbPacientes(
    val ID_Paciente : Int,
    var Nombres : String,
    var Apellidos : String,
    var Edad : Int,
    val FechaNacimiento : String,
    var Número_habitación : Int,
    var Número_cama : Int,
    val ID_medicamento : Int,
    var ID_Enfermedad : Int
)