package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection?{
        try {
            //cambiar ip dependiendo de la computadora
            val ipEmily = "jdbc:oracle:thin:@192.168.0.17:1521:xe"
            val ipGabriela = "jdbc:oracle:thin:@192.168.1.13:1521:xe"
            val ipprueba = "jdbc:oracle:thin:@10.10.1.119:1521:xe"

            val usuario = "proyectoformativo24"
            val contrasena = "GabEmi"

            val conexion = DriverManager.getConnection(ipGabriela, usuario, contrasena)

            return conexion
        }catch (e: Exception){
            println("El error es este: $e")
            return null
        }
    }
}