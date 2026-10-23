package com.noescape.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Night = Color(0xFF080B11)
private val SurfaceDark = Color(0xFF131923)
private val Ink = Color(0xFFF1F4FA)
private val Quiet = Color(0xFF8B97AA)
private val Accent = Color(0xFF8BB5FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(8, 11, 17)
        window.navigationBarColor = android.graphics.Color.rgb(8, 11, 17)
        setContent { MaterialTheme { LauncherScreen() } }
    }
}

private data class AppEntry(val name: String, val packageName: String)

@Composable
private fun LauncherScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val pm = context.packageManager
    var search by remember { mutableStateOf("") }
    var focus by remember { mutableStateOf(false) }

    val apps = remember {
        pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            PackageManager.MATCH_ALL
        ).map {
            AppEntry(
                it.loadLabel(pm).toString(),
                it.activityInfo.packageName
            )
        }.filter { it.packageName != context.packageName }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }
    val filtered = apps.filter { it.name.contains(search, ignoreCase = true) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF101827), Night, Night))
        )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val points = listOf(
                Offset(size.width*.08f,size.height*.13f),
                Offset(size.width*.87f,size.height*.09f),
                Offset(size.width*.74f,size.height*.25f),
                Offset(size.width*.93f,size.height*.43f),
                Offset(size.width*.12f,size.height*.52f),
                Offset(size.width*.83f,size.height*.72f),
                Offset(size.width*.19f,size.height*.89f),
                Offset(size.width*.58f,size.height*.94f)
            )
            points.forEachIndexed { i, p ->
                drawCircle(
                    Accent.copy(alpha=if(i==2) .8f else .25f),
                    radius=if(i==2) 2.2.dp.toPx() else 1.dp.toPx(),
                    center=p
                )
            }
        }

        Column(Modifier.fillMaxSize().padding(horizontal=24.dp)) {
            Spacer(Modifier.height(34.dp))
            Row(verticalAlignment=Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("N O E S C A P E", color=Ink, fontSize=12.sp, fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        SimpleDateFormat("EEEE  ·  d MMMM", Locale.getDefault())
                            .format(Date()).uppercase(),
                        color=Quiet, fontSize=10.sp, letterSpacing=1.1.sp
                    )
                }
                Box(
                    Modifier.size(42.dp).clip(CircleShape)
                        .background(Color(0xFF1A2941)),
                    contentAlignment=Alignment.Center
                ) {
                    Canvas(Modifier.size(22.dp)) {
                        drawCircle(Accent, style=androidx.compose.ui.graphics.drawscope.Stroke(1.5.dp.toPx()))
                        drawCircle(Accent.copy(alpha=.65f), radius=size.minDimension*.18f)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Text("Your attention.", color=Ink, fontSize=31.sp, fontWeight=FontWeight.Light, letterSpacing=(-1).sp)
            Text("Back in your hands.", color=Accent, fontSize=29.sp, fontWeight=FontWeight.Light, letterSpacing=(-1).sp)
            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark).clickable { focus = !focus }
                    .padding(16.dp),
                verticalAlignment=Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(42.dp).clip(RoundedCornerShape(13.dp))
                        .background(if(focus) Color(0xFF214237) else Color(0xFF1D2D48)),
                    contentAlignment=Alignment.Center
                ) {
                    Text(if(focus) "✓" else "◷", color=if(focus) Color(0xFF8BE0B7) else Accent, fontSize=22.sp)
                }
                Column(Modifier.weight(1f).padding(start=13.dp)) {
                    Text(if(focus) "Focus mode enabled" else "Start a focus session", color=Ink, fontSize=14.sp, fontWeight=FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text(if(focus) "One thing at a time." else "25 minutes · no distractions", color=Quiet, fontSize=11.sp)
                }
                Text(if(focus) "ON" else "START", color=Accent, fontSize=10.sp, fontWeight=FontWeight.Bold)
            }

            Spacer(Modifier.height(26.dp))
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("YOUR APPS", color=Ink, fontSize=11.sp, fontWeight=FontWeight.Bold, letterSpacing=2.sp)
                Spacer(Modifier.weight(1f))
                Text("${filtered.size} APPS", color=Quiet, fontSize=10.sp, letterSpacing=1.sp)
            }
            Spacer(Modifier.height(13.dp))

            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark).padding(14.dp)
            ) {
                if(search.isEmpty()) Text("Find an app", color=Quiet, fontSize=13.sp)
                BasicTextField(
                    value=search,
                    onValueChange={search=it},
                    singleLine=true,
                    textStyle=androidx.compose.ui.text.TextStyle(color=Ink, fontSize=13.sp),
                    modifier=Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier=Modifier.weight(1f),
                verticalArrangement=Arrangement.spacedBy(2.dp)
            ) {
                items(filtered, key={it.packageName}) { app ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp))
                            .clickable {
                                pm.getLaunchIntentForPackage(app.packageName)?.let {
                                    context.startActivity(it)
                                }
                            }.padding(vertical=12.dp, horizontal=4.dp),
                        verticalAlignment=Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(38.dp).clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1B2433)),
                            contentAlignment=Alignment.Center
                        ) {
                            Text(
                                app.name.take(1).uppercase(),
                                color=Accent, fontSize=16.sp, fontWeight=FontWeight.SemiBold
                            )
                        }
                        Column(Modifier.weight(1f).padding(start=13.dp)) {
                            Text(app.name, color=Ink, fontSize=14.sp, fontWeight=FontWeight.Medium, maxLines=1, overflow=TextOverflow.Ellipsis)
                            Text(app.packageName, color=Quiet.copy(alpha=.8f), fontSize=9.sp, maxLines=1, overflow=TextOverflow.Ellipsis)
                        }
                        Text("↗", color=Quiet, fontSize=17.sp)
                    }
                }
            }
            Text(
                "LESS SCROLLING. MORE LIVING.",
                Modifier.align(Alignment.CenterHorizontally).padding(vertical=14.dp),
                color=Quiet.copy(alpha=.75f), fontSize=9.sp, letterSpacing=1.5.sp
            )
        }
    }
}
