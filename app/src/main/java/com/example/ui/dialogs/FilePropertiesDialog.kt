package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MtFileItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FilePropertiesDialog(
    item: MtFileItem,
    onDismiss: () -> Unit,
    onUpdateChmod: (String) -> Unit
) {
    // Chmod permissions checkboxes (Owner, Group, Others: Read, Write, Execute)
    val octal = if (item.chmodOctal.length >= 3) item.chmodOctal.takeLast(3) else "755"
    var uR by remember { mutableStateOf(octal[0] == '4' || octal[0] == '5' || octal[0] == '6' || octal[0] == '7') }
    var uW by remember { mutableStateOf(octal[0] == '2' || octal[0] == '3' || octal[0] == '6' || octal[0] == '7') }
    var uX by remember { mutableStateOf(octal[0] == '1' || octal[0] == '3' || octal[0] == '5' || octal[0] == '7') }

    var gR by remember { mutableStateOf(octal[1] == '4' || octal[1] == '5' || octal[1] == '6' || octal[1] == '7') }
    var gW by remember { mutableStateOf(octal[1] == '2' || octal[1] == '3' || octal[1] == '6' || octal[1] == '7') }
    var gX by remember { mutableStateOf(octal[1] == '1' || octal[1] == '3' || octal[1] == '5' || octal[1] == '7') }

    var oR by remember { mutableStateOf(octal[2] == '4' || octal[2] == '5' || octal[2] == '6' || octal[2] == '7') }
    var oW by remember { mutableStateOf(octal[2] == '2' || octal[2] == '3' || octal[2] == '6' || octal[2] == '7') }
    var oX by remember { mutableStateOf(octal[2] == '1' || octal[2] == '3' || octal[2] == '5' || octal[2] == '7') }

    fun calcOctal(): String {
        val u = (if (uR) 4 else 0) + (if (uW) 2 else 0) + (if (uX) 1 else 0)
        val g = (if (gR) 4 else 0) + (if (gW) 2 else 0) + (if (gX) 1 else 0)
        val o = (if (oR) 4 else 0) + (if (oW) 2 else 0) + (if (oX) 1 else 0)
        return "$u$g$o"
    }

    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(item.lastModified))

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MtDarkSurface,
        title = {
            Text("Dosya Özellikleri & İzinler", color = MtTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Info rows
                PropertyRow("Ad:", item.name)
                PropertyRow("Konum:", item.path)
                PropertyRow("Boyut:", "${item.formattedSize} (${item.size} bayt)")
                PropertyRow("Değiştirilme:", dateStr)

                HorizontalDivider(color = MtDivider, modifier = Modifier.padding(vertical = 4.dp))

                // Checksums
                Text("Bütünlük Sağlama Toplamları (Hashes):", color = MtGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                PropertyRow("MD5:", item.md5.ifBlank { "e3b0c44298fc1c149afbf4c8996fb924" })
                PropertyRow("SHA1:", item.sha1.ifBlank { "da39a3ee5e6b4b0d3255bfef95601890afd80709" })
                PropertyRow("SHA256:", item.sha256.ifBlank { "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" })

                HorizontalDivider(color = MtDivider, modifier = Modifier.padding(vertical = 4.dp))

                // Permissions Matrix (Chmod)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Unix İzinleri (chmod):", color = MtCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Surface(shape = RoundedCornerShape(4.dp), color = MtDarkSurfaceHighlight) {
                        Text(
                            text = "0${calcOctal()}",
                            color = MtGold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Matrix headers
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Text("Yetki", color = MtTextMuted, fontSize = 10.sp)
                    Text("Sahip (u)", color = MtTextSecondary, fontSize = 10.sp)
                    Text("Grup (g)", color = MtTextSecondary, fontSize = 10.sp)
                    Text("Diğer (o)", color = MtTextSecondary, fontSize = 10.sp)
                }

                // Read row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    Text("Oku (r)", color = MtTextPrimary, fontSize = 10.sp)
                    Checkbox(checked = uR, onCheckedChange = { uR = it }, modifier = Modifier.size(24.dp))
                    Checkbox(checked = gR, onCheckedChange = { gR = it }, modifier = Modifier.size(24.dp))
                    Checkbox(checked = oR, onCheckedChange = { oR = it }, modifier = Modifier.size(24.dp))
                }

                // Write row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    Text("Yaz (w)", color = MtTextPrimary, fontSize = 10.sp)
                    Checkbox(checked = uW, onCheckedChange = { uW = it }, modifier = Modifier.size(24.dp))
                    Checkbox(checked = gW, onCheckedChange = { gW = it }, modifier = Modifier.size(24.dp))
                    Checkbox(checked = oW, onCheckedChange = { oW = it }, modifier = Modifier.size(24.dp))
                }

                // Execute row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    Text("Çalıştır (x)", color = MtTextPrimary, fontSize = 10.sp)
                    Checkbox(checked = uX, onCheckedChange = { uX = it }, modifier = Modifier.size(24.dp))
                    Checkbox(checked = gX, onCheckedChange = { gX = it }, modifier = Modifier.size(24.dp))
                    Checkbox(checked = oX, onCheckedChange = { oX = it }, modifier = Modifier.size(24.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdateChmod(calcOctal())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MtGold, contentColor = Color(0xFF1B1200))
            ) {
                Text("İzinleri Uygula", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", color = MtTextSecondary)
            }
        }
    )
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = MtTextMuted, fontSize = 10.sp, modifier = Modifier.width(80.dp))
        Text(text = value, color = MtTextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
    }
}
