package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Theme color tokens
val DarkBackground = Color(0xFF0C0C12)
val ObsidianSurface = Color(0xFF14141F)
val ObsidianBorder = Color(0xFF262635)
val EmeraldAccent = Color(0xFF00E676)
val CyanAccent = Color(0xFF00E5FF)
val AmberAccent = Color(0xFFFFC107)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                QSIAppMainScreen()
            }
        }
    }
}

@Composable
fun QSIAppMainScreen(viewModel: QSIViewModel = viewModel()) {
    val tabIndex by viewModel.currentTab.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianSurface,
                modifier = Modifier.border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)),
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = tabIndex == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(Icons.Default.List, contentDescription = "KB") },
                    label = { Text("Spectral KB", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldAccent,
                        selectedTextColor = EmeraldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ObsidianBorder
                    )
                )
                NavigationBarItem(
                    selected = tabIndex == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(Icons.Default.Share, contentDescription = "Clustering") },
                    label = { Text("Attention Matrix", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanAccent,
                        selectedTextColor = CyanAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ObsidianBorder
                    )
                )
                NavigationBarItem(
                    selected = tabIndex == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Reasoning") },
                    label = { Text("Ref Operators", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldAccent,
                        selectedTextColor = EmeraldAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ObsidianBorder
                    )
                )
                NavigationBarItem(
                    selected = tabIndex == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Operators") },
                    label = { Text("Neural combination", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberAccent,
                        selectedTextColor = AmberAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ObsidianBorder
                    )
                )
                NavigationBarItem(
                    selected = tabIndex == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "NumTheory") },
                    label = { Text("Number Theory", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyanAccent,
                        selectedTextColor = CyanAccent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = ObsidianBorder
                    )
                )
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            // Header Bar
            QSIHeaderBar()

            Box(modifier = Modifier.fillMaxSize()) {
                when (tabIndex) {
                    0 -> QSIKBExplorerScreen(viewModel)
                    1 -> QSIAttentionScreen(viewModel)
                    2 -> QSIReasoningEngineScreen(viewModel)
                    3 -> QSILearningLabScreen(viewModel)
                    4 -> QSINumberTheoryScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun QSIHeaderBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .border(1.dp, ObsidianBorder)
            .padding(16.dp, 20.dp, 16.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Quranic Spectral Intelligence",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp
            )
            Text(
                text = "Offline 100% Analytical Modeling Engine",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .border(1.dp, EmeraldAccent.copy(0.3f), RoundedCornerShape(8.dp))
                .background(EmeraldAccent.copy(0.05f))
                .padding(8.dp, 4.dp)
        ) {
            Text(
                text = "QSI SECURE v1.2",
                color = EmeraldAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 1: KNOWLEDGE EXPLORER
// -------------------------------------------------------------
@Composable
fun QSIKBExplorerScreen(viewModel: QSIViewModel) {
    val activeSurahIndex by viewModel.selectedSurahIndex.collectAsState()
    val matrix by viewModel.activeMatrix.collectAsState()
    val metrics by viewModel.activeMetrics.collectAsState()
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Surah Select Slider List
        Text(
            text = "TRANSFORMATIONAL OPERATOR (114 SURAHS)",
            color = EmeraldAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(QuranicDatabase.surahs) { surah ->
                val isSelected = surah.index == activeSurahIndex
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) EmeraldAccent.copy(0.12f) else ObsidianSurface,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) EmeraldAccent else ObsidianBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.loadSurahMatrix(surah.index) }
                        .padding(14.dp, 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${surah.index}. ${surah.nameEnglish}",
                            color = if (isSelected) Color.White else Color.LightGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        Text(
                            text = surah.nameArabic,
                            color = if (isSelected) EmeraldAccent else Color.Gray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active Surah Meta Card
        val curSurah = QuranicDatabase.getSurah(activeSurahIndex)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "${curSurah.index}. Surah ${curSurah.nameEnglish}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "English Meaning: ${curSurah.englishMeaning}",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = curSurah.nameArabic,
                        color = EmeraldAccent,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(color = ObsidianBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("TOTAL VERSES", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${curSurah.totalVerses}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("REVELATION ORDER", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${curSurah.revelationOrder}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("CHRONOLOGICAL TYPE", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = curSurah.type,
                            color = if (curSurah.type == "Meccan") EmeraldAccent else AmberAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Spectral Grid Map
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .background(ObsidianSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MATRIX HEATMAP (28×28 OPERATOR)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                var hoveredCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

                Box(
                    modifier = Modifier
                        .size(196.dp)
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val cellW = size.width / 28f
                                    val cellH = size.height / 28f
                                    val c = (offset.x / cellW).toInt().coerceIn(0, 27)
                                    val r = (offset.y / cellH).toInt().coerceIn(0, 27)
                                    hoveredCell = Pair(r, c)
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cellW = size.width / 28f
                        val cellH = size.height / 28f
                        
                        for (r in 0 until 28) {
                            for (c in 0 until 28) {
                                if (matrix.isNotEmpty() && r < matrix.size && c < matrix[r].size) {
                                    val value = matrix[r][c]
                                    if (value > 0.0) {
                                        // Brighter neon emerald for larger numbers
                                        val intensity = (value / 250.0).toFloat().coerceIn(0.1f, 1f)
                                        drawRect(
                                            color = EmeraldAccent.copy(alpha = intensity),
                                            topLeft = Offset(c * cellW, r * cellH),
                                            size = Size(cellW - 0.4f, cellH - 0.4f)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Draw Hover indicator
                        hoveredCell?.let { cell ->
                            drawRect(
                                color = CyanAccent,
                                topLeft = Offset(cell.second * cellW, cell.first * cellH),
                                size = Size(cellW, cellH),
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Row / Col", color = Color.Gray, fontSize = 10.sp)
                    Text(
                        text = hoveredCell?.let { "[${it.first}, ${it.second}] val: ${if (matrix.isNotEmpty() && it.first < matrix.size && it.second < matrix[it.first].size) matrix[it.first][it.second].toInt() else 0}" } ?: "Tap pixel to inspect",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Right: Math metrics
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .background(ObsidianSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SPECTRAL PROPERTIES",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                MetricRow("Rank", "${metrics.rank} / 28")
                MetricRow("Trace", String.format("%.2f", metrics.trace))
                MetricRow("Sparsity", String.format("%.1f %%", metrics.sparsity * 100))
                MetricRow("F-Norm", String.format("%.2f", metrics.frobeniusNorm))
                MetricRow("Entropy", String.format("%.3f sp", metrics.spectralEntropy))
                val condStr = if (metrics.conditionNumber > 1e8) "Infinite" else String.format("%.1f", metrics.conditionNumber)
                MetricRow("Condition No", condStr)
            }
        }
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(10.dp, 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = EmeraldAccent, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

// -------------------------------------------------------------
// TAB 2: CONNECTIVITY ATTENTION GRAPH
// -------------------------------------------------------------
@Composable
fun QSIAttentionScreen(viewModel: QSIViewModel) {
    val loading by viewModel.loadingNetwork.collectAsState()
    val similarity by viewModel.similarityMatrix.collectAsState()
    val coords by viewModel.surahCoordinates.collectAsState()
    val activeSurahIndex by viewModel.selectedSurahIndex.collectAsState()

    var activeNodeDetails by remember { mutableStateOf<String?>(null) }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = CyanAccent)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Precomputing spectral projection map...", color = Color.Gray, fontSize = 13.sp)
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "LOW-DIMENSIONAL CORRELATION PROJECTION",
                color = CyanAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Each Surah (1-114) projected using Power Iteration PCA of Frobenius similarity. Click nodes to focus.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Drawing the Node Plot Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                    .pointerInput(coords) {
                        detectTapGestures(
                            onTap = { offset ->
                                val w = size.width
                                val h = size.height
                                var closestIdx = -1
                                var minDist = 45f
                                for (i in coords.indices) {
                                    val mappedX = w / 2f + coords[i].first * (w / 2.3f)
                                    val mappedY = h / 2f + coords[i].second * (h / 2.3f)
                                    val dist = sqrt((offset.x - mappedX) * (offset.x - mappedX) + (offset.y - mappedY) * (offset.y - mappedY))
                                    if (dist < minDist) {
                                        minDist = dist
                                        closestIdx = i + 1
                                    }
                                }
                                if (closestIdx != -1) {
                                    viewModel.loadSurahMatrix(closestIdx)
                                    val surah = QuranicDatabase.getSurah(closestIdx)
                                    activeNodeDetails = "${surah.index}. Surah ${surah.nameEnglish} (Verses: ${surah.totalVerses}, Type: ${surah.type})"
                                }
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    // Draw grid lines
                    for (i in 1..4) {
                        val gx = w * (i / 5f)
                        drawLine(color = ObsidianBorder, start = Offset(gx, 0f), end = Offset(gx, h), strokeWidth = 1f)
                        val gy = h * (i / 5f)
                        drawLine(color = ObsidianBorder, start = Offset(0f, gy), end = Offset(w, gy), strokeWidth = 1f)
                    }

                    // Plot Surah nodes
                    for (i in coords.indices) {
                        val sIndex = i + 1
                        val cx = w / 2f + coords[i].first * (w / 2.3f)
                        val cy = h / 2f + coords[i].second * (h / 2.3f)
                        val isSelected = sIndex == activeSurahIndex
                        val sur = QuranicDatabase.getSurah(sIndex)
                        
                        val nodeColor = if (isSelected) {
                            CyanAccent
                        } else if (sur.type == "Meccan") {
                            EmeraldAccent.copy(alpha = 0.55f)
                        } else {
                            AmberAccent.copy(alpha = 0.55f)
                        }
                        
                        drawCircle(
                            color = nodeColor,
                            radius = if (isSelected) 8f else 4.5f,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(EmeraldAccent, RoundedCornerShape(10.dp)))
                Text(" Meccan Nodes", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(end = 12.dp))
                Box(modifier = Modifier.size(10.dp).background(AmberAccent, RoundedCornerShape(10.dp)))
                Text(" Medinan Nodes", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(end = 12.dp))
                Box(modifier = Modifier.size(10.dp).background(CyanAccent, RoundedCornerShape(10.dp)))
                Text(" Focused", color = Color.Gray, fontSize = 11.sp)
            }

            HorizontalDivider(color = ObsidianBorder, modifier = Modifier.padding(vertical = 16.dp))

            // Sub info block
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FOCUSED NODE COUPLING PROFILE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = activeNodeDetails ?: "Tap any dot above to inspect mathematical Surah coupling.",
                        color = if (activeNodeDetails == null) Color.Gray else CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: OFFLINE REASONING CHAINS
// -------------------------------------------------------------
@Composable
fun QSIReasoningEngineScreen(viewModel: QSIViewModel) {
    val chain by viewModel.reasoningChainState.collectAsState()
    
    var surahI by remember { mutableStateOf(1) }
    var surahJ by remember { mutableStateOf(36) }
    var surahK by remember { mutableStateOf(112) }
    var inputType by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "OFFLINE MATHEMATICAL PROPAGATION CHAINS",
            color = EmeraldAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Observe 28x28 mathematical projections as multi-step transformation operators: x -> Mi -> Mj -> Mk -> y",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dropdown inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InputNumberWheel("M_i Index", surahI, { surahI = it.coerceIn(1, 114) }, Modifier.weight(1f))
            InputNumberWheel("M_j Index", surahJ, { surahJ = it.coerceIn(1, 114) }, Modifier.weight(1f))
            InputNumberWheel("M_k Index", surahK, { surahK = it.coerceIn(1, 114) }, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vector Input State Type Sel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { inputType = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = if (inputType == 0) EmeraldAccent else ObsidianSurface),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.weight(1f)
            ) {
                Text("Sine Wave", color = Color.White, fontSize = 11.sp)
            }
            Button(
                onClick = { inputType = 1 },
                colors = ButtonDefaults.buttonColors(containerColor = if (inputType == 1) EmeraldAccent else ObsidianSurface),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.weight(1f)
            ) {
                Text("Alternating Pulse", color = Color.White, fontSize = 11.sp)
            }
            Button(
                onClick = { inputType = 2 },
                colors = ButtonDefaults.buttonColors(containerColor = if (inputType == 2) EmeraldAccent else ObsidianSurface),
                border = BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.weight(1f)
            ) {
                Text("White Noise", color = Color.White, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.executeReasoningChain(surahI, surahJ, surahK, inputType) },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("PROPAGATE STATE COUPLING", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Coordinates propagation vector line graph
        Text(
            text = "COORDINATE EVOLUTION PATHWAYS",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp))
                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Draw central axis
                drawLine(color = ObsidianBorder, start = Offset(0f, midY), end = Offset(w, midY), strokeWidth = 2f)

                // Plot trajectories
                plotProjectionCurve(chain.inputVector, Color.Gray, w, midY, h)
                plotProjectionCurve(chain.step1Vector, EmeraldAccent, w, midY, h)
                plotProjectionCurve(chain.step2Vector, CyanAccent, w, midY, h)
                plotProjectionCurve(chain.step3Vector, AmberAccent, w, midY, h)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendBadge("Input Vector (x0)", Color.Gray)
            LegendBadge("Transform 1 (x1)", EmeraldAccent)
            LegendBadge("Transform 2 (x2)", CyanAccent)
            LegendBadge("Transform 3 (x3)", AmberAccent)
        }
    }
}

private fun DrawScope.plotProjectionCurve(vector: DoubleArray, color: Color, w: Float, midY: Float, h: Float) {
    if (vector.all { it == 0.0 }) return
    val path = Path()
    val maxVal = vector.map { abs(it) }.maxOrNull() ?: 1.0
    val scale = if (maxVal < 1e-9) 1.0f else (h / 2.3f) / maxVal.toFloat()

    val stepX = w / 27f
    path.moveTo(0f, midY - vector[0].toFloat() * scale)
    for (i in 1 until 28) {
        path.lineTo(i * stepX, midY - vector[i].toFloat() * scale)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )
}

@Composable
fun LegendBadge(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(8.dp)))
        Text("  $label", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InputNumberWheel(label: String, value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(ObsidianSurface, RoundedCornerShape(12.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = { onValueChange(value - 1) }, modifier = Modifier.size(24.dp)) {
                Text("-", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
            Text(text = "$value", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onValueChange(value + 1) }, modifier = Modifier.size(24.dp)) {
                Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: LEARNABLE OPERATORS LAB
// -------------------------------------------------------------
@Composable
fun QSILearningLabScreen(viewModel: QSIViewModel) {
    val state by viewModel.operatorLabState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "EIGEN-COEFFICIENT LINEAR OPTIMIZATION",
            color = AmberAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Solve least-squares modular mappings on fixed Quranic Operators using interactive offline Gradient Descent.",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { viewModel.runGradientDescentTraining() },
            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
            enabled = !state.isTraining,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (state.isTraining) "OPTIMIZING ADAPTIVE OPERATORS..." else "EXECUTE GRADIENT DESCENT (EPOCHS = 80)",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rendering Loss Descent Path
        if (state.lossHistory.isNotEmpty()) {
            Text(
                text = "TRAINING LOSS INTEGRATION PROFILE",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    val maxLoss = state.lossHistory.maxOrNull() ?: 1.0f
                    val minLoss = state.lossHistory.minOrNull() ?: 0.0f
                    val diff = if (maxLoss - minLoss < 1e-5f) 1.0f else maxLoss - minLoss

                    val stepX = w / (state.lossHistory.size - 1).toFloat()
                    val path = Path()
                    path.moveTo(0f, h - ((state.lossHistory[0] - minLoss) / diff) * (h - 10f))
                    for (i in 1 until state.lossHistory.size) {
                        val ly = h - ((state.lossHistory[i] - minLoss) / diff) * (h - 10f)
                        path.lineTo(i * stepX, ly)
                    }

                    drawPath(
                        path = path,
                        color = AmberAccent,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Epoch 0 (Loss: ${String.format("%.1f", state.lossHistory.first())})", color = Color.Gray, fontSize = 9.sp)
                Text("Epoch 80 (Loss: ${String.format("%.2f", state.lossHistory.last())})", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ADAPTIVE SPECTRAL COEFFICIENTS (SELECTED TOP ALPHA)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Show standard sliders for top surahs in the subset
        val subset = listOf(1, 2, 3, 30, 36, 55, 112, 114)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            for (surId in subset) {
                val sur = QuranicDatabase.getSurah(surId)
                val weight = state.weights[surId] ?: 0.0f
                Column(
                    modifier = Modifier
                        .background(ObsidianSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp, 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${sur.index}. ${sur.nameEnglish}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(String.format("α: %.2f", weight), color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Slider(
                        value = weight,
                        onValueChange = { viewModel.updateOperatorWeight(surId, it) },
                        valueRange = -5f..5f,
                        colors = SliderDefaults.colors(
                            thumbColor = AmberAccent,
                            activeTrackColor = AmberAccent.copy(0.4f),
                            inactiveTrackColor = ObsidianBorder
                        )
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: NUMBER THEORY LAB
// -------------------------------------------------------------
@Composable
fun QSINumberTheoryScreen(viewModel: QSIViewModel) {
    val state by viewModel.numberTheoryState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    
    var textInput by remember { mutableStateOf("19") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "NUMBER THEORETIC MODULAR SCANNER",
            color = CyanAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Map arbitrary prime/composite numbers N to 114D Quranic eigenvalue projection spectra profile Φ(N).",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Enter Number N", color = Color.Gray, fontSize = 13.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedContainerColor = ObsidianSurface,
                    unfocusedContainerColor = ObsidianSurface
                )
            )

            Button(
                onClick = {
                    viewModel.analyzeNumberStructure(textInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                modifier = Modifier.height(56.dp)
            ) {
                Text("SCAN N", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Spectral Scan Result Card
        Card(
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SCAN ANALYSIS FOR N = ${state.currentNumber}",
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("PRIMALITY (TRUE MATH)", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (state.isPrimeNumber) "PRIME (أوّلي)" else "COMPOSITE (مركّب)",
                            color = if (state.isPrimeNumber) EmeraldAccent else Color.LightGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("PRIME ARITHMETIC FACTORS", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(state.factorsString)) },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanAccent, modifier = Modifier.size(12.dp))
                            }
                        }
                        Text(
                            text = state.factorsString,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = ObsidianBorder, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("SPECTRAL CLASSIFIER PREDICTION", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (state.predictedPrimality) "Prime Profile Match" else "Composite Profile Pattern",
                            color = if (state.predictedPrimality) CyanAccent else Color.LightGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text("PATTERN CONFIDENCE", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = String.format("%.1f %%", state.confidenceScore * 100f),
                            color = CyanAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vector Φ(N) Spectrum bar chart
        Text(
            text = "114D SPECTRUM Φ(N) NORM GRAPH",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Color.Black.copy(0.4f), RoundedCornerShape(16.dp))
                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                val count = state.spectrumVector.size
                val stepX = w / count.toFloat()
                
                val maxVal = state.spectrumVector.maxOrNull() ?: 1.0
                val scale = if (maxVal < 1e-9) 1.0f else (h - 10f) / maxVal.toFloat()

                for (i in 0 until count) {
                    val barH = state.spectrumVector[i].toFloat() * scale
                    drawRect(
                        color = CyanAccent,
                        topLeft = Offset(i * stepX, h - barH),
                        size = Size(stepX - 0.5f, barH)
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Surah 1 (Al-Fatihah)", color = Color.Gray, fontSize = 9.sp)
            Text("Surah 114 (An-Nas)", color = Color.Gray, fontSize = 9.sp)
        }
    }
}
