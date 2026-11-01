package app.rpkindwords.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import app.rpkindwords.R

/** Kleine Spendenbitte nach jedem echten App-Start: Spenden (öffnet den Link) oder Abbrechen. */
@Composable
fun DonateDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val url = stringResource(R.string.donate_url)
    val error = stringResource(R.string.donate_error)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.donate_prompt_title)) },
        text = { Text(stringResource(R.string.donate_prompt_text)) },
        confirmButton = {
            TextButton(onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: Exception) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                onDismiss()
            }) { Text(stringResource(R.string.donate_prompt_donate)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.donate_prompt_cancel)) } },
    )
}
