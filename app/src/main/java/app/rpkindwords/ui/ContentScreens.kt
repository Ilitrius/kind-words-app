package app.rpkindwords.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.rpkindwords.MainViewModel
import app.rpkindwords.R
import app.rpkindwords.data.MessageEntity
import app.rpkindwords.data.MessageRepository
import app.rpkindwords.reminder.ShareIntents
import kotlinx.coroutines.launch

@Composable
fun categoryLabel(id: String): String = when (id) {
    "love" -> stringResource(R.string.category_love)
    "appreciation" -> stringResource(R.string.category_appreciation)
    "morning" -> stringResource(R.string.category_morning)
    "goodnight" -> stringResource(R.string.category_goodnight)
    "encouragement" -> stringResource(R.string.category_encouragement)
    "missyou" -> stringResource(R.string.category_missyou)
    "compliment" -> stringResource(R.string.category_compliment)
    "humor" -> stringResource(R.string.category_humor)
    "thanks" -> stringResource(R.string.category_thanks)
    "apology" -> stringResource(R.string.category_apology)
    MessageRepository.CATEGORY_CUSTOM -> stringResource(R.string.category_custom)
    else -> id
}

private fun share(context: Context, text: String) {
    context.startActivity(Intent.createChooser(ShareIntents.send(text), null))
}

private fun copy(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("", text))
}

@Composable
fun HomeScreen(vm: MainViewModel, snackbar: SnackbarHostState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val current by vm.current.collectAsStateWithLifecycle()
    val category by vm.category.collectAsStateWithLifecycle()
    val loaded by vm.loaded.collectAsStateWithLifecycle()
    val copiedText = stringResource(R.string.copied)

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(selected = category == null, onClick = { vm.selectCategory(null) }, label = { Text(stringResource(R.string.category_all)) })
            }
            items(MessageRepository.categories) { c ->
                FilterChip(selected = category == c, onClick = { vm.selectCategory(c) }, label = { Text(categoryLabel(c)) })
            }
        }
        val msg = current
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (msg != null) {
                    Text(categoryLabel(msg.category), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(msg.text, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                } else if (loaded) {
                    Text(stringResource(R.string.home_empty), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                }
            }
        }
        Button(onClick = { vm.newSuggestion() }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Refresh, contentDescription = null)
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text(stringResource(R.string.action_new_suggestion))
        }
        if (msg != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                FilledTonalButton(onClick = { share(context, msg.text) }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text(stringResource(R.string.action_share))
                }
                OutlinedButton(onClick = {
                    copy(context, msg.text)
                    scope.launch { snackbar.showSnackbar(copiedText) }
                }, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.action_copy)) }
                FavoriteButton(msg) { vm.toggleFavorite(msg) }
            }
        }
    }
}

@Composable
private fun FavoriteButton(msg: MessageEntity, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            if (msg.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(if (msg.favorite) R.string.favorite_remove else R.string.favorite_add),
        )
    }
}

@Composable
fun FavoritesScreen(vm: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val items by vm.favorites.collectAsStateWithLifecycle()
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(stringResource(R.string.nav_favorites), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 16.dp))
        if (items.isEmpty()) {
            Text(stringResource(R.string.favorites_empty), style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(items, key = { it.id }) { m ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(m.text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = { share(context, m.text) }) {
                                Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.action_share))
                            }
                            FavoriteButton(m) { vm.toggleFavorite(m) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MineScreen(vm: MainViewModel, modifier: Modifier = Modifier) {
    val items by vm.userMessages.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<MessageEntity?>(null) }
    var adding by rememberSaveable { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<MessageEntity?>(null) }

    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text(stringResource(R.string.nav_mine), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 16.dp))
            if (items.isEmpty()) {
                Text(stringResource(R.string.mine_empty), style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
                    items(items, key = { it.id }) { m ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(m.text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                                IconButton(onClick = { editing = m }) {
                                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                                }
                                IconButton(onClick = { deleting = m }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.action_delete))
                                }
                            }
                        }
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { adding = true },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.mine_add)) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
        )
    }

    if (adding) {
        TextDialog(R.string.mine_dialog_add_title, "", onDismiss = { adding = false }) { vm.addUserMessage(it); adding = false }
    }
    editing?.let { m ->
        TextDialog(R.string.mine_dialog_edit_title, m.text, onDismiss = { editing = null }) { vm.editUserMessage(m.id, it); editing = null }
    }
    deleting?.let { m ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(stringResource(R.string.mine_delete_title)) },
            text = { Text(stringResource(R.string.mine_delete_text)) },
            confirmButton = { TextButton(onClick = { vm.deleteUserMessage(m.id); deleting = null }) { Text(stringResource(R.string.action_delete)) } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(stringResource(R.string.action_cancel)) } },
        )
    }
}

private const val MAX_LEN = 300

@Composable
private fun TextDialog(title: Int, initial: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it.take(MAX_LEN) },
                label = { Text(stringResource(R.string.mine_hint)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = { TextButton(onClick = { onSave(text) }, enabled = text.isNotBlank()) { Text(stringResource(R.string.action_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}
