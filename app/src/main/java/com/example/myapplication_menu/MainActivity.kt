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
import com.example.myapplication_menu.ui.theme.ChocolateBrown
import com.example.myapplication_menu.ui.theme.CocoaBrown
import com.example.myapplication_menu.ui.theme.DonutPink
import com.example.myapplication_menu.ui.theme.MenuTitleStyle
import com.example.myapplication_menu.ui.theme.CategoryTitleStyle
import com.example.myapplication_menu.ui.theme.ProductNameStyle
import com.example.myapplication_menu.ui.theme.ProductPriceStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VolumeUp
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.ApiStatus

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val altoContraste = remember { mutableStateOf(false) }
            CoffeeMenuTheme(highContrast = altoContraste.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingMenu(
                        altoContraste = altoContraste.value,
                        onAltoContrasteChange = { altoContraste.value = it }
                    )
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
    val ingredientes: List<Ingrediente> = emptyList(),
    val disponible: Boolean = true
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
fun GreetingMenu(
    modifier: Modifier = Modifier,
    altoContraste: Boolean = false,
    onAltoContrasteChange: (Boolean) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val categoriaSeleccionada = remember {
        mutableStateOf<String?>("Donas")
    }
    val categorias = listOf(
        "Donas",
        "Drinks",
        "Postres"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.7f),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Menú Principal",
                    style = MenuTitleStyle,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Todas las Categorías") },
                    selected = categoriaSeleccionada.value == null,
                    onClick = {
                        categoriaSeleccionada.value = null
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                categorias.forEach { categoria ->
                    NavigationDrawerItem(
                        label = { Text(categoria) },
                        selected = categoriaSeleccionada.value == categoria,
                        onClick = {
                            categoriaSeleccionada.value = categoria
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Alto Contraste", 
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = altoContraste,
                        onCheckedChange = onAltoContrasteChange
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {

        val name = remember() { mutableStateOf("") }
        val hmuch = remember() { mutableStateOf("") }
        val context = LocalContext.current
        val tts = remember { mutableStateOf<TextToSpeech?>(null) }

        DisposableEffect(context) {
            val ttsEngine = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.value?.setLanguage(Locale.forLanguageTag("es"))
                }
            }
            tts.value = ttsEngine
            onDispose {
                ttsEngine.stop()
                ttsEngine.shutdown()
            }
        }
        val estadoBusqueda = rememberTextFieldState()
        val estadoSearchBar = rememberSearchBarState()
        val imagenSeleccionada = remember {
            mutableStateOf<String?>(null)
        }
        val seleccionarImagen = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null)
                imagenSeleccionada.value = guardarImagen(
                    context, uri
                )
        }
        val mostrarFormulario = remember { mutableStateOf(false) }

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
            Bebida("Chocolate Caliente", "$20 pesos", R.drawable.chocohot),
            Bebida("Frappé de Caramelo", "$35 pesos", R.drawable.cafecappu),
            Bebida("Té Chai Latté", "$28 pesos", R.drawable.cafe),
            Bebida("Malteada de Fresa", "$30 pesos", R.drawable.chocohot),
            Bebida("Café Helado", "$25 pesos", R.drawable.cafe)
        )

        val pasteles = listOf(
            Pastel("Pastel Merengue", "$25 pesos", R.drawable.pastelmerenge),
            Pastel("Pastel Moka", "$24 pesos", R.drawable.pastelmoka),
            Pastel("Pastel Chocolate", "$25 pesos", R.drawable.pastelchoco),
            Pastel("Cheesecake Clásico", "$35 pesos", R.drawable.pastelmerenge),
            Pastel("Tiramisú", "$40 pesos", R.drawable.pastelmoka),
            Pastel("Brownie con Nuez", "$20 pesos", R.drawable.pastelchoco),
            Pastel("Tarta de Frutas", "$30 pesos", R.drawable.pastelmerenge)
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
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                )

                val pancitosFiltrados = pancitos.filter { pancito ->
                    val texto = estadoBusqueda.text.toString()
                    texto.isBlank() ||
                            pancito.pan.contains(
                                texto,
                                ignoreCase = true
                            ) || pancito.price.contains(texto) ||
                            pancito.ingredientes.any { ingrediente ->
                                ingrediente.nombre.contains(texto, ignoreCase = true)
                            }
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 350.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Menu",
                            style = MenuTitleStyle,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
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
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Buscar"
                                        )
                                    }
                                )
                            }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        if (mostrarFormulario.value) {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Agregar Nuevo Producto", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    OutlinedTextField(
                                        value = name.value,
                                        onValueChange = { name.value = it },
                                        label = { Text("Nombre del pancito") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = hmuch.value,
                                        onValueChange = { hmuch.value = it },
                                        label = { Text("A cuanto el pan") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button(onClick = {
                                            seleccionarImagen.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }) {
                                            Text(if (imagenSeleccionada.value != null) "Imagen ✓" else "Imagen")
                                        }
                                        Button(onClick = {
                                            if (name.value.isNotBlank() && hmuch.value.isNotBlank()) {
                                                val precioTexto =
                                                    if (hmuch.value.contains("pesos") || hmuch.value.contains("$")) {
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
                                                mostrarFormulario.value = false // Cierra el formulario tras agregar
                                            }
                                        }) {
                                            Text("Agregar")
                                        }
                                    }
                                }
                            }
                        }
                    }


                    if (categoriaSeleccionada.value == null || categoriaSeleccionada.value == "Donas") {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Panes",
                                style = CategoryTitleStyle,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }

                        items(pancitosFiltrados) { pancito ->
                            val editado = remember { mutableStateOf(false) }
                        val nombreEditado = remember { mutableStateOf(pancito.pan) }
                        val precioEditado = remember { mutableStateOf(pancito.price.removePrefix("$")) }
                        val mostrarIngrediente = remember { mutableStateOf(false) }
                        val nombreIngrediente = remember { mutableStateOf("") }
                        val cantidadIngrediente = remember { mutableStateOf("") }

                        Card(
                            modifier = Modifier.padding(4.dp).fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (pancito.imagePath != null) {
                                    AsyncImage(
                                        model = pancito.imagePath,
                                        contentDescription = pancito.pan,
                                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(pancito.foto),
                                        contentDescription = pancito.pan,
                                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Column(
                                    modifier = Modifier.padding(start = 16.dp).weight(1f)
                                ) {
                                if (editado.value) {
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
                                            ) {
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
                                } else {
                                    Text(
                                        text = pancito.pan,
                                        style = ProductNameStyle,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = pancito.price,
                                        style = ProductPriceStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Text(
                                        text = if (pancito.disponible)
                                            "Disponible"
                                        else
                                            "Agotado"
                                    )
                                    Switch(
                                        checked = pancito.disponible,
                                        onCheckedChange = { nuevoEstado ->
                                            val position = pancitos.indexOf(pancito)
                                            if (position != -1) {
                                                pancitos[position] = pancito.copy(
                                                    disponible = nuevoEstado
                                                )
                                            }
                                        },
                                        thumbContent = if (pancito.disponible) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Disponible",
                                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                                )
                                            }
                                        } else {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Agotado",
                                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                                )
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = MaterialTheme.colorScheme.secondary,
                                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                                            uncheckedThumbColor = MaterialTheme.colorScheme.error,
                                            uncheckedTrackColor = MaterialTheme.colorScheme.error
                                        )
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
                                    IconButton(
                                        onClick = {
                                            val disp = if (pancito.disponible) "Disponible" else "Agotado"
                                            tts.value?.speak("${pancito.pan}, precio ${pancito.price}, estado $disp", TextToSpeech.QUEUE_FLUSH, null, null)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Leer en voz alta"
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
                                            Text(
                                                text = "Sin ingredientes registrados",
                                                fontSize = 14.sp
                                            )
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
                    } // closes items(pancitosFiltrados)
                    } // closes if (categoriaSeleccionada.value == null || categoriaSeleccionada.value == "Donas")

                    if (categoriaSeleccionada.value == null || categoriaSeleccionada.value == "Drinks" || categoriaSeleccionada.value == "Bebidas") {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Bebidas",
                                style = CategoryTitleStyle,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }

                        items(bebidas) { bebida ->
                        Card(
                            modifier = Modifier.padding(4.dp).fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(bebida.foto),
                                    contentDescription = bebida.bebida,
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier.padding(start = 16.dp).weight(1f)
                                ) {
                                    Text(
                                        text = bebida.bebida,
                                        style = ProductNameStyle,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = bebida.price,
                                        style = ProductPriceStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                    }

                    if (categoriaSeleccionada.value == null || categoriaSeleccionada.value == "Postres" || categoriaSeleccionada.value == "Pasteles") {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = "Pasteles",
                                style = CategoryTitleStyle,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }

                        items(pasteles) { pastel ->
                        Card(
                            modifier = Modifier.padding(4.dp).fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(pastel.foto),
                                    contentDescription = pastel.pastel,
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier.padding(start = 16.dp).weight(1f)
                                ) {
                                    Text(
                                        text = pastel.pastel,
                                        style = ProductNameStyle,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = pastel.price,
                                        style = ProductPriceStyle,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                    } // This closes the if block
                } // This closes the LazyVerticalGrid

                // Botón flotante para mostrar/ocultar formulario de agregar producto
                FloatingActionButton(
                    onClick = {
                        mostrarFormulario.value = !mostrarFormulario.value
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (mostrarFormulario.value) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Alternar Formulario de Registro"
                    )
                }

                // Botón de hamburguesa para el menú lateral (bajado para evitar la barra de estado)
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir Menú Lateral",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }