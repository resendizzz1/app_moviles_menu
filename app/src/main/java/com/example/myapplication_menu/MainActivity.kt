package com.example.myapplication_menu

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication_menu.ui.theme.CoffeeMenuTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Path
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import java.io.File
import coil3.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.rememberSearchBarState
import org.jetbrains.annotations.ApiStatus

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffeeMenuTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingMenu()
                }
            }
        }
    }
}

data class Ingrediente(
    val nombre: String,
    val cantidad: String
)

data class Pancito(
    val pan: String,
    val price: String,
    val foto: Int,
    val imagen: Int = 0,
    val imagePath: String? = null,
    val ingredientes: List<Ingrediente> = emptyList()
)

fun guardarImagen(context: Context, uri: Uri): String?{
    val carpeta = File(
        context.filesDir,
        "platillos"
    )
    if (!carpeta.exists()){
        carpeta.mkdirs()
    }
    val archivo = File(
        carpeta,
        "platillo_${System.currentTimeMillis()}.jpg"
    )
    context.contentResolver
        .openInputStream(uri)
        ?.use { entrada -> archivo.outputStream().use {
            salida -> entrada.copyTo(salida)
        } }
    return archivo.absolutePath
}


data class Bebida(
    val bebida: String,
    val price: String,
    val foto: Int
)

data class Pastel(
    val pastel: String,
    val price: String,
    val foto: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreetingMenu(modifier: Modifier = Modifier) {
    val name = remember() { mutableStateOf("") }
    val hmuch = remember() { mutableStateOf("") }
    val context = LocalContext.current
    val estadoBusqueda = rememberTextFieldState()
    val estadoSearchBar = rememberSearchBarState()
    val imagenSeleccionada = remember {
        mutableStateOf<String?>(null)
    }
    val seleccionarImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()) {
        uri ->
        if (uri != null)
            imagenSeleccionada.value = guardarImagen(
                context, uri
            )
    }

    val pancitos = remember {
        mutableStateListOf(
            Pancito("Dona Maple", "$15 pesos", R.drawable.donamaple),
            Pancito("Dona Chocolate", "$16 pesos", R.drawable.donachoco),
            Pancito("Dona Choco Coco", "$18 pesos", R.drawable.donachocococo),
            Pancito("Dona Azucar", "$12 pesos", R.drawable.donaazucar)
        )
    }

    val bebidas = listOf(
        Bebida("Cafe", "$15 pesos", R.drawable.cafe),
        Bebida("Cafe Cappuccino", "$22 pesos", R.drawable.cafecappu),
        Bebida("Chocolate Caliente", "$20 pesos", R.drawable.chocohot)
    )

    val pasteles = listOf(
        Pastel("Pastel Merengue", "$25 pesos", R.drawable.pastelmerenge),
        Pastel("Pastel Moka", "$24 pesos", R.drawable.pastelmoka),
        Pastel("Pastel Chocolate", "$25 pesos", R.drawable.pastelchoco)
    )
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(R.drawable.menuborder),
            contentDescription = "Fondo del menu",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        var pancitosFiltrados = pancitos.filter {
            pancito -> val texto = estadoBusqueda.text.toString()
            texto.isBlank() ||
                    pancito.pan.contains(
                        texto,
                        ignoreCase = true
                    )|| pancito.price.contains(texto)||
                    pancito.ingredientes.any{
                        ingrediente -> ingrediente.nombre.contains(texto, ignoreCase = true)
                    }
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            item {
                Text(
                    text = "Menu",
                    fontSize = 35.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            item{
                SearchBar(
                    state = estadoSearchBar,
                        inputField = {
                            SearchBarDefaults.InputField(
                            textFieldState = estadoBusqueda,
                            searchBarState = estadoSearchBar,
                            onSearch = {},
                            placeholder = {
                                Text("Buscar pancito")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector =  Icons.Default.Search,
                                    contentDescription = "Buscar"
                                )
                            }
                        )
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = {
                        name.value = it
                    },
                    label = {
                        Text("Nombre del pancito")
                    }
                )
                OutlinedTextField(
                    value = hmuch.value,
                    onValueChange = {
                        hmuch.value = it
                    },
                    label = {
                        Text("A cuanto el pan")
                    }
                )
                Button(onClick = {
                    seleccionarImagen.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text(if (imagenSeleccionada.value != null) "Imagen Seleccionada ✓" else "Seleccionar Imagen")
                }
                Button(onClick = {
                    if (name.value.isNotBlank() && hmuch.value.isNotBlank()) {
                        val precioTexto = if (hmuch.value.contains("pesos") || hmuch.value.contains("$")) {
                            hmuch.value
                        } else {
                            "$${hmuch.value} pesos"
                        }
                        pancitos.add(
                            Pancito(
                                pan = name.value,
                                price = precioTexto,
                                foto = R.drawable.donamaple,
                                imagePath = imagenSeleccionada.value
                            )
                        )
                        name.value = ""
                        hmuch.value = ""
                        imagenSeleccionada.value = null
                    }
                }) {
                    Text("Agregar Pan")
                }
            }


            item {
                Text(
                    text = "Panes",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            items(pancitosFiltrados) { pancito ->
                val editado = remember {
                    mutableStateOf(false)
                }

                val nombreEditado = remember {
                    mutableStateOf(pancito.pan)
                }

                val precioEditado = remember {
                    mutableStateOf(pancito.price.removePrefix("$"))
                }

                val mostrarIngrediente = remember {
                    mutableStateOf(false)
                }
                val nombreIngrediente = remember {
                    mutableStateOf("")
                }
                val cantidadIngrediente = remember {
                    mutableStateOf("")
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pancito.imagePath != null) {
                        AsyncImage(
                            model = pancito.imagePath,
                            contentDescription = pancito.pan,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(pancito.foto),
                            contentDescription = pancito.pan,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        if(editado.value){
                            OutlinedTextField(
                                value = nombreEditado.value,
                                onValueChange = {
                                    nombreEditado.value = it
                                },
                                label = {
                                    Text("Nombre del pancito")
                                }
                            )
                            OutlinedTextField(
                                value = precioEditado.value,
                                onValueChange = {
                                    precioEditado.value = it
                                },
                                label = {
                                    Text("Precio del platillo")
                                }
                            )
                            Button(
                                onClick = {
                                    val position = pancitos.indexOf(pancito)
                                    if (
                                        nombreEditado.value.isNotBlank() &&
                                        precioEditado.value.isNotBlank()
                                    ){
                                        pancitos[position] = pancito.copy(
                                            pan = nombreEditado.value,
                                            price = "$${precioEditado.value}"
                                        )
                                        editado.value = false
                                    }
                                }
                            ) {
                                Text("Guardar")
                            }
                        }else {
                            Text(
                                text = pancito.pan,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = pancito.price,
                                fontSize = 18.sp
                            )
                        }
                    Row(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                mostrarIngrediente.value = !mostrarIngrediente.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Ver Ingredientes"
                            )
                        }
                        IconButton(
                            onClick = {
                                nombreEditado.value = pancito.pan
                                precioEditado.value = pancito.price.removePrefix("$")
                                editado.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(
                            onClick = {
                                pancitos.add(pancito.copy())
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar"
                            )
                        }
                        IconButton(
                            onClick = {
                                pancitos.remove(pancito)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                    if (mostrarIngrediente.value) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Ingredientes:",
                                fontWeight = FontWeight.Bold
                            )
                            if (pancito.ingredientes.isEmpty()) {
                                Text(text = "Sin ingredientes registrados", fontSize = 14.sp)
                            } else {
                                pancito.ingredientes.forEach { ingrediente ->
                                    Text(
                                        text = "• ${ingrediente.nombre} - ${ingrediente.cantidad}",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = nombreIngrediente.value,
                                onValueChange = {
                                    nombreIngrediente.value = it
                                },
                                label = {
                                    Text("Ingrediente")
                                }
                            )
                            OutlinedTextField(
                                value = cantidadIngrediente.value,
                                onValueChange = {
                                    cantidadIngrediente.value = it
                                },
                                label = {
                                    Text("Cantidad")
                                }
                            )
                            Button(
                                onClick = {
                                    if (nombreIngrediente.value.isNotBlank() && cantidadIngrediente.value.isNotBlank()) {
                                        val posicion = pancitos.indexOf(pancito)
                                        if (posicion != -1) {
                                            pancitos[posicion] = pancito.copy(
                                                ingredientes = pancito.ingredientes + Ingrediente(
                                                    nombre = nombreIngrediente.value,
                                                    cantidad = cantidadIngrediente.value
                                                )
                                            )
                                            nombreIngrediente.value = ""
                                            cantidadIngrediente.value = ""
                                        }
                                    }
                                }
                            ) {
                                Text("Agregar Ingrediente")
                            }
                        }
                    }
                    }
                }
            }

            item {
                Text(
                    text = "Bebidas",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            items(bebidas) { bebida ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(bebida.foto),
                        contentDescription = bebida.bebida,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = bebida.bebida,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = bebida.price,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Pasteles",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            items(pasteles) { pastel ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(pastel.foto),
                        contentDescription = pastel.pastel,
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = pastel.pastel,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pastel.price,
                            fontSize = 18.sp
                        )
                    }
                }
            }

        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingMenuPreview() {
    CoffeeMenuTheme() {
        GreetingMenu(
            modifier = Modifier.padding(8.dp)
        )

    }
}