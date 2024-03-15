import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
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

fun main() = singleWindowApplication(
    title = "Domino Backgammon",
    state = WindowState(WindowPlacement.Maximized)
) {
        App()
}
