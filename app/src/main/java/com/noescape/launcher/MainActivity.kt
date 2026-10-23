package com.noescape.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Black = Color(0xFF000000)
private val Card = Color(0xFF0D0E0C)
private val CardRaised = Color(0xFF141512)
private val Hairline = Color(0xFF282A24)
private val White = Color(0xFFF3F3ED)
private val Grey = Color(0xFF85877F)
private val Green = Color(0xFFD1FF68)
private val GreenDim = Color(0xFF29351B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK
        setContent {
            MaterialTheme {
                NoEscapeScreen()
            }
        }
    }
}

private data class AppEntry(
    val name: String,
    val packageName: String,
    val icon: Bitmap
)

private fun Drawable.toBitmapSafe(): Bitmap {
    val w = intrinsicWidth.coerceIn(1, 192)
    val h = intrinsicHeight.coerceIn(1, 192)
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val oldBounds = Rect(bounds)
    setBounds(0, 0, w, h)
    draw(canvas)
    setBounds(oldBounds)
    return bitmap
}

@Composable
private fun NoEscapeScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val pm = context.packageManager

    var search by remember { mutableStateOf("") }
    var focusPreview by remember { mutableStateOf(false) }

    val apps = remember {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)

        pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .mapNotNull { info ->
                val packageName = info.activityInfo?.packageName
                    ?: return@mapNotNull null
                if (packageName == context.packageName) {
                    return@mapNotNull null
                }

                val label = info.loadLabel(pm)?.toString()
                    ?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null

                AppEntry(
                    name = label,
                    packageName = packageName,
                    icon = info.loadIcon(pm).toBitmapSafe()
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    val filtered = apps.filter {
        it.name.contains(search.trim(), ignoreCase = true)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        AmbientBackground(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 23.dp)
        ) {
            Spacer(Modifier.height(27.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(CardRaised)
                        .border(1.dp, Hairline, RoundedCornerShape(11.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.size(20.dp)) {
                        drawCircle(
                            color = Green,
                            radius = size.minDimension * .34f,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = Green,
                            radius = size.minDimension * .10f
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 11.dp)
                ) {
                    Text(
                        "NOESCAPE",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.4.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "YOUR SPACE",
                        color = Grey,
                        fontSize = 8.sp,
                        letterSpacing = 2.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        SimpleDateFormat("EEE", Locale.getDefault())
                            .format(Date()).uppercase(),
                        color = Grey,
                        fontSize = 9.sp,
                        letterSpacing = 1.3.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        SimpleDateFormat("d MMM", Locale.getDefault())
                            .format(Date()).uppercase(),
                        color = White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = .5.sp
                    )
                }
            }

            Spacer(Modifier.height(36.dp))

            Text(
                "A LITTLE LESS",
                color = Grey,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.4.sp
            )
            Spacer(Modifier.height(7.dp))

            Text(
                "noise.",
                color = White,
                fontSize = 43.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp,
                lineHeight = 45.sp
            )
            Text(
                "A LOT MORE you.",
                color = Green,
                fontSize = 29.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-1.1).sp,
                lineHeight = 34.sp
            )

            Spacer(Modifier.height(26.dp))

            FocusCard(
                active = focusPreview,
                onClick = { focusPreview = !focusPreview }
            )

            Spacer(Modifier.height(29.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "YOUR APPS",
                    color = White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.width(9.dp))
                Text(
                    filtered.size.toString().padStart(2, '0'),
                    color = Green,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "TAP TO OPEN",
                    color = Grey,
                    fontSize = 8.sp,
                    letterSpacing = 1.4.sp
                )
            }

            Spacer(Modifier.height(13.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(49.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Card)
                    .border(1.dp, Hairline, RoundedCornerShape(15.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchGlyph()

                Spacer(Modifier.width(11.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (search.isEmpty()) {
                        Text(
                            "Search your apps",
                            color = Grey,
                            fontSize = 12.sp
                        )
                    }

                    BasicTextField(
                        value = search,
                        onValueChange = { search = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = White,
                            fontSize = 13.sp
                        ),
                        cursorBrush = SolidColor(Green),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (search.isNotEmpty()) {
                    Text(
                        "CLEAR",
                        color = Green,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { search = "" }
                            .padding(5.dp)
                    )
                }
            }

            Spacer(Modifier.height(7.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(filtered, key = { it.packageName }) { app ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    pm.getLaunchIntentForPackage(app.packageName)?.let {
                                        context.startActivity(it)
                                    }
                                }
                                .padding(vertical = 10.dp, horizontal = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = app.icon.asImageBitmap(),
                                contentDescription = app.name,
                                modifier = Modifier
                                    .size(37.dp)
                                    .clip(RoundedCornerShape(11.dp))
                            )

                            Text(
                                text = app.name,
                                color = White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 14.dp)
                            )

                            Text(
                                "↗",
                                color = Grey,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(start = 10.dp, end = 5.dp)
                            )
                        }

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 51.dp)
                                .height(1.dp)
                                .background(Hairline.copy(alpha = .65f))
                        )
                    }
                }

                if (filtered.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Nothing here.",
                                color = White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Try another search.",
                                color = Grey,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Green)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "BE PRESENT. NOT JUST ONLINE.",
                    color = Grey,
                    fontSize = 8.sp,
                    letterSpacing = 1.8.sp
                )
            }
        }
    }
}

@Composable
private fun FocusCard(
    active: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(19.dp))
            .background(Card)
            .border(1.dp, Hairline, RoundedCornerShape(19.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(58.dp)
                .clip(CircleShape)
                .background(Green)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, end = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Green)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    if (active) "FOCUS PREVIEW ON" else "YOUR NEXT 25 MINUTES",
                    color = Green,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                if (active) "One thing. Just you." else "Make time count.",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-.3).sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                if (active) "Preview selected" else "Start a focus session",
                color = Grey,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(Green),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (active) "Ⅱ" else "↗",
                color = Black,
                fontSize = if (active) 18.sp else 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SearchGlyph() {
    Canvas(Modifier.size(17.dp)) {
        drawCircle(
            color = Grey,
            radius = size.minDimension * .31f,
            center = Offset(size.width * .42f, size.height * .40f),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawLine(
            color = Grey,
            start = Offset(size.width * .64f, size.height * .64f),
            end = Offset(size.width * .91f, size.height * .91f),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

@Composable
private fun AmbientBackground(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF101609).copy(alpha = .42f),
                    Color.Transparent
                ),
                center = Offset(size.width * .98f, size.height * .02f),
                radius = size.width * .77f
            ),
            radius = size.width * .77f,
            center = Offset(size.width * .98f, size.height * .02f)
        )

        val stars = listOf(
            Offset(.09f * size.width, .13f * size.height),
            Offset(.82f * size.width, .10f * size.height),
            Offset(.93f * size.width, .31f * size.height),
            Offset(.07f * size.width, .47f * size.height),
            Offset(.88f * size.width, .64f * size.height),
            Offset(.16f * size.width, .78f * size.height),
            Offset(.75f * size.width, .91f * size.height),
            Offset(.48f * size.width, .06f * size.height),
            Offset(.35f * size.width, .37f * size.height),
            Offset(.96f * size.width, .84f * size.height)
        )

        stars.forEachIndexed { index, point ->
            drawCircle(
                color = if (index == 2) Green.copy(alpha = .48f)
                else White.copy(alpha = .13f),
                radius = if (index == 2) 1.5.dp.toPx() else .7.dp.toPx(),
                center = point
            )
        }
    }
}
