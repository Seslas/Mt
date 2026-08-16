package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TerminalDialog(
    currentPath: String,
    onDismiss: () -> Unit
) {
    val logs = remember {
        mutableStateListOf(
            "MT Manager Terminal v2.14.0 (Root Shell Mode)",
            "Linux localhost 6.1.75-android14-perf-g9b2a1 #1 SMP PREEMPT aarch64",
            "root@android:/storage/emulated/0/MT2 # id",
            "uid=0(root) gid=0(root) groups=0(root) context=u:r:su:s0",
            "root@android:/storage/emulated/0/MT2 # "
        )
    }

    var cmdInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val quickCommands = listOf(
        "ls -la",
        "pm list packages -3",
        "dumpsys package com.target.game",
        "getprop ro.build.version.release",
        "cat /proc/cpuinfo",
        "top -n 1",
        "chmod 777 target_game_mod.apk"
    )

    fun executeCmd(cmd: String) {
        if (cmd.isBlank()) return
        logs.add("root@android:$currentPath # $cmd")
        when {
            cmd.startsWith("ls") -> {
                logs.add("total 14840")
                logs.add("-rw-r--r-- 1 root root 14250000 2026-08-16 12:40 target_game_mod.apk")
                logs.add("-rw-r--r-- 1 root root   482910 2026-08-16 12:35 classes.dex")
                logs.add("-rw-r--r-- 1 root root    94100 2026-08-16 12:30 resources.arsc")
                logs.add("-rw-r--r-- 1 root root     2840 2026-08-16 12:28 AndroidManifest.xml")
                logs.add("-rwxr-xr-x 1 root root   842000 2026-08-16 12:20 libnative-mod.so")
                logs.add("drwxrwxrwx 2 root root     4096 2026-08-16 12:15 apks_backup")
            }
            cmd.contains("pm list") -> {
                logs.add("package:com.target.game")
                logs.add("package:com.google.android.youtube")
                logs.add("package:com.android.chrome")
                logs.add("package:com.spotify.music")
                logs.add("package:com.termux")
            }
            cmd.contains("getprop") -> {
                logs.add("14 (Android 14 UpsideDownCake - API 34)")
            }
            cmd.contains("dumpsys") -> {
                logs.add("Package [com.target.game] (0x7a8b):")
                logs.add("  userId=10245")
                logs.add("  pkg=Package{8e3a2b com.target.game}")
                logs.add("  codePath=/data/app/~~a9c8==/com.target.game")
                logs.add("  versionCode=104 minSdk=24 targetSdk=34")
                logs.add("  Signatures: [SHA256: e3b0c44298fc1c149afbf4c8996fb924]")
            }
            cmd.contains("chmod") -> {
                logs.add("✅ İzinler başarıyla güncellendi (chmod 777).")
            }
            else -> {
                logs.add("✅ Komut başarıyla tamamlandı (exit code 0)")
            }
        }
        cmdInput = ""
        coroutineScope.launch {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("terminal_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0C1017),
            border = androidx.compose.foundation.BorderStroke(1.dp, MtGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(color = MtDarkSurface, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Terminal, contentDescription = null, tint = MtGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Root Terminal Console", color = MtTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF003814)) {
                                    Text("ROOT (uid=0)", color = MtGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(text = currentPath, color = MtTextMuted, fontSize = 10.sp)
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = MtTextSecondary)
                        }
                    }
                }

                // Quick Command Bar
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MtDarkSurfaceHighlight)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickCommands) { qCmd ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MtDarkSurfaceVariant,
                            modifier = Modifier.clickable { executeCmd(qCmd) }
                        ) {
                            Text(
                                text = qCmd,
                                color = MtGreen,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Terminal Logs
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    items(logs) { line ->
                        Text(
                            text = line,
                            color = if (line.startsWith("root@")) MtGoldLight else if (line.contains("✅") || line.contains("uid=0")) MtGreen else MtTextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Input row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MtDarkSurface)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "# ", color = MtGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    TextField(
                        value = cmdInput,
                        onValueChange = { cmdInput = it },
                        placeholder = { Text("Komut yazın (örn. pm list, ls, chmod)...", fontSize = 11.sp, color = MtTextMuted) },
                        modifier = Modifier.weight(1f).testTag("terminal_input"),
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = MtTextPrimary),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = { executeCmd(cmdInput) },
                        modifier = Modifier.size(36.dp).testTag("btn_terminal_send")
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Gönder", tint = MtGold)
                    }
                }
            }
        }
    }
}
