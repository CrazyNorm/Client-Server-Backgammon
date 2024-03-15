import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.dominobackgammonclient.ui.BGApp
import com.example.dominobackgammonclient.ui.BGViewModel
import com.example.dominobackgammonclient.ui.theme.DominoBackgammonClientTheme

@Composable
@Preview
fun App() {
    val viewModel by remember { mutableStateOf(BGViewModel()) }
    val uiState by viewModel.uiState.collectAsState()

    DominoBackgammonClientTheme(colourScheme = uiState.colourScheme) {
        BGApp(true, viewModel)
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
