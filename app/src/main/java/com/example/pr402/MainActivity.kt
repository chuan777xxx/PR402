package com.example.pr402

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pr402.ui.theme.PR402Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PR402Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

// Clase base para representar un tipo genérico de vehículo
open class TipoVehiculo(
    val ruedas: Int,
    val motor: String,
    val asientos: Int,
    val color: String,
    val modelo: String
)

// Clases específicas que heredan de TipoVehiculo para representar diferentes tipos de vehículos
class Coche(
    motor: String,
    asientos: Int,
    color: String,
    modelo: String
) : TipoVehiculo(4, motor, asientos, color, modelo) {
    init {
        require(ruedas >= 4) { "Las coches no pueden tener menos de 4 ruedas." }
    }
}

class Moto(
    motor: String,
    ruedas: Int,
    color: String,
    modelo: String
) : TipoVehiculo(0, motor, 2, color, modelo){
    init {
        require(ruedas >= 2) { "Las motos no pueden tener menos de 2 ruedas." }
    }
}

class Patinete(
    motor: String,
    ruedas: Int,
    color: String,
    modelo: String
) : TipoVehiculo(0, motor, 0, color, modelo){
    init {
        require(ruedas >= 2) { "Las patinetes no pueden tener menos de 2 ruedas." }
    }
}

class Furgoneta(
    motor: String,
    ruedas: Int,
    asientos: Int,
    color: String,
    modelo: String,
    val cargaMaxima: Int
) : TipoVehiculo(0, motor, asientos, color, modelo) {
    init {
        require(ruedas <= 6) { "Las furgonetas no pueden tener más de 6 ruedas." }
    }
}

class Trailer(
    motor: String,
    ruedas: Int,
    color: String,
    modelo: String,
    val cargaMaxima: Int
) : TipoVehiculo(6, motor, 0, color, modelo) {
    init {
        require(ruedas >= 6) { "Los tráileres deben tener al menos 6 ruedas." }
    }
}

// Clase que representa un concesionario de vehículos
class Concesionario {
    private var vehiculos = arrayOf<TipoVehiculo>()

    // Método para crear un nuevo vehículo y agregarlo al concesionario
    fun crearVehiculo(vehiculo: TipoVehiculo) {
        vehiculos = vehiculos.plus(vehiculo)
    }

    // Método para consultar la cantidad de vehículos de un tipo específico en el concesionario
    fun consultarCantidadPorTipo(tipo: String): Int {
        return vehiculos.count { it::class.simpleName == tipo }
    }

    // Método para listar los nombres de todos los vehículos en el concesionario
    fun listarPorNombre(): List<String> {
        return vehiculos.map { it.modelo }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    // Estado mutable para los campos de entrada y la información del concesionario
    var concesionario by remember { mutableStateOf(Concesionario()) }
    var motor by remember { mutableStateOf("") }
    var ruedas by remember { mutableStateOf("") }
    var asientos by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var tipoVehiculo by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var listado by remember { mutableStateOf("") }
    var cargaMaxima by remember { mutableStateOf("") }

    // Columna que contiene varios campos de entrada y botones
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campos de entrada para información del vehículo
        TextField(
            value = motor,
            onValueChange = { motor = it },
            label = { Text("Motor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = ruedas,
            onValueChange = { ruedas = it },
            label = { Text("Ruedas") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = asientos,
            onValueChange = { asientos = it },
            label = { Text("Número de asientos") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        TextField(
            value = tipoVehiculo,
            onValueChange = { tipoVehiculo = it },
            label = { Text("Tipo de Vehículo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        // Nuevo TextField para la carga máxima
        if (tipoVehiculo.lowercase() == "furgoneta" || tipoVehiculo.lowercase() == "trailer") {
            TextField(
                value = cargaMaxima,
                onValueChange = { cargaMaxima = it },
                label = { Text("Carga Máxima") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Botón para crear un nuevo vehículo en el concesionario
        Button(
            onClick = {
                try {
                    val ruedasInt = ruedas.toIntOrNull() ?: 0

                    // Verificación de carga máxima no vacía
                    if (tipoVehiculo.lowercase() == "furgoneta" || tipoVehiculo.lowercase() == "trailer") {
                        if (cargaMaxima.isBlank()) {
                            mensajeError = "La carga máxima no puede estar vacía."
                            return@Button
                        }
                    }

                    // Intentar convertir asientos a entero
                    val asientosInt = asientos.toInt()

                    mensajeError = ""

                    when (tipoVehiculo.lowercase()) {
                        "coche" -> concesionario.crearVehiculo(Coche(motor, asientosInt, color, modelo))
                        "moto" -> {
                            if (asientosInt <= 2) {
                                concesionario.crearVehiculo(Moto(motor, ruedasInt, color, modelo))
                            } else {
                                mensajeError = "Las motos no pueden tener más de 2 asientos."
                            }
                        }
                        "patinete" -> {
                            if (asientosInt == 0) {
                                concesionario.crearVehiculo(Patinete(motor, ruedasInt, color, modelo))
                            } else {
                                mensajeError = "Los patinetes no pueden tener asientos."
                            }
                        }
                        "furgoneta" -> {
                            val cargaMaximaInt = cargaMaxima.toIntOrNull() ?: throw NumberFormatException("La carga máxima no es un número válido.")
                            concesionario.crearVehiculo(Furgoneta(motor, ruedasInt, asientosInt, color, modelo, cargaMaximaInt))
                        }
                        "trailer" -> {
                            val cargaMaximaInt = cargaMaxima.toIntOrNull() ?: throw NumberFormatException("La carga máxima no es un número válido.")
                            concesionario.crearVehiculo(Trailer(motor, ruedasInt, color, modelo, cargaMaximaInt))
                        }
                        else -> mensajeError = "Tipo de vehículo no reconocido"
                    }
                    motor=""
                    ruedas=""
                    asientos=""
                    color=""
                    modelo=""
                    tipoVehiculo=""
                } catch (e: NumberFormatException) {
                    mensajeError = "Error no puedes introducir algo que no sea numero: ${e.message}"
                    e.printStackTrace()
                } catch (e: Exception) {
                    mensajeError = "Error al crear el vehículo: ${e.message}"
                    e.printStackTrace()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Crear Vehículo")
        }

        // Campo para mostrar mensajes de error o información del concesionario
        Text(
            text = if (mensajeError.isNotEmpty()) mensajeError else "",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Botón para consultar la cantidad de vehículos por tipo y listar por nombre
        Button(
            onClick = {
                val coches = concesionario.consultarCantidadPorTipo("Coche")
                val motos = concesionario.consultarCantidadPorTipo("Moto")
                val patinetes = concesionario.consultarCantidadPorTipo("Patinete")
                val furgonetas = concesionario.consultarCantidadPorTipo("Furgoneta")
                val trailers = concesionario.consultarCantidadPorTipo("Trailer")

                // Display the information in the UI
                listado = buildString {
                    append("Cantidad de coches: $coches\n")
                    append("Cantidad de motos: $motos\n")
                    append("Cantidad de patinetes: $patinetes\n")
                    append("Cantidad de furgonetas: $furgonetas\n")
                    append("Cantidad de trailers: $trailers\n")
                }

                val vehiculosPorNombre = concesionario.listarPorNombre()
                listado += "Listado de vehículos por nombre: $vehiculosPorNombre"
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Consultar y Listar")
        }

        // Campo para mostrar información de la consulta y listado
        Text(
            text = if (listado.isNotEmpty()) listado else "",
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}
