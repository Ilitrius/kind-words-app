package app.rpkindwords.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.rpkindwords.MainViewModel
import app.rpkindwords.R
import app.rpkindwords.update.UpdateStatus

/** Nicht blockierender Update-Hinweis: Download läuft im Hintergrund, die App bleibt nutzbar. */
@Composable
private fun UpdateBanner(status: UpdateStatus, onUpdate: () -> Unit, onRestart: () -> Unit, onDismiss: () -> Unit) {
    val downloaded = status == UpdateStatus.DOWNLOADED
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(if (downloaded) R.string.update_ready else R.string.update_available),
                Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
            TextButton(onClick = if (downloaded) onRestart else onUpdate) {
                Text(stringResource(if (downloaded) R.string.update_restart else R.string.update_action))
            }
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.update_later)) }
        }
    }
}

private enum class Tab(val label: Int, val icon: ImageVector) {
    Home(R.string.nav_home, Icons.Filled.Home),
    Favorites(R.string.nav_favorites, Icons.Filled.Favorite),
    Mine(R.string.nav_mine, Icons.Filled.Create),
    Settings(R.string.nav_settings, Icons.Filled.Settings),
}

@Composable
fun MainScreen(
    vm: MainViewModel,
    updateStatus: UpdateStatus = UpdateStatus.NONE,
    onUpdate: () -> Unit = {},
    onRestartForUpdate: () -> Unit = {},
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var bannerDismissed by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    BackHandler(enabled = tab != 0) { tab = 0 }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(t.icon, contentDescription = null) },
                        label = { Text(stringResource(t.label)) },
                    )
                }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (updateStatus != UpdateStatus.NONE && !bannerDismissed) {
                UpdateBanner(updateStatus, onUpdate, onRestartForUpdate, onDismiss = { bannerDismissed = true })
            }
            Box(Modifier.weight(1f)) {
                when (Tab.entries[tab]) {
                    Tab.Home -> HomeScreen(vm, snackbar)
                    Tab.Favorites -> FavoritesScreen(vm)
                    Tab.Mine -> MineScreen(vm)
                    Tab.Settings -> SettingsScreen(vm)
                }
            }
        }
    }
}
