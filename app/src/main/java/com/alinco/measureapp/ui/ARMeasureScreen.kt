package com.alinco.measureapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.SphereNode
import java.util.Locale
import kotlin.math.sqrt

@Composable
fun ARMeasureScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required for AR measuring.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant permission")
                }
            }
        }
        return
    }

    var placedAnchors by remember { mutableStateOf(listOf<AnchorNode>()) }
    var distanceMeters by remember { mutableStateOf<Float?>(null) }
    var statusText by remember { mutableStateOf("Move your phone to detect a surface, then tap two points") }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val sceneView = ARSceneView(ctx)

                sceneView.onSessionUpdated = { _, frame -> }

                sceneView.onTapAr = { hitResult, _ ->
                    val anchor = hitResult.createAnchor()
                    val anchorNode = AnchorNode(sceneView.engine, anchor)

                    val marker = SphereNode(
                        engine = sceneView.engine,
                        radius = 0.01f,
                        center = Position(0f, 0f, 0f)
                    )
                    anchorNode.addChildNode(marker)
                    sceneView.addChildNode(anchorNode)

                    val updated = (placedAnchors + anchorNode).takeLast(2)
                    placedAnchors = updated

                    if (updated.size == 2) {
                        val p1 = updated[0].worldPosition
                        val p2 = updated[1].worldPosition
                        val dx = p1.x - p2.x
                        val dy = p1.y - p2.y
                        val dz = p1.z - p2.z
                        distanceMeters = sqrt(dx * dx + dy * dy + dz * dz)
                        statusText = "Tap again to start a new measurement"
                    } else {
                        distanceMeters = null
                        statusText = "Point 1 placed — tap a second point"
                    }
                }

                sceneView
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(statusText, style = MaterialTheme.typography.bodyMedium)
                    if (distanceMeters != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Distance: ${String.format(Locale.US, "%.3f", distanceMeters)} m " +
                                "(${String.format(Locale.US, "%.1f", distanceMeters!! * 100)} cm)",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        placedAnchors = emptyList()
                        distanceMeters = null
                        statusText = "Move your phone to detect a surface, then tap two points"
                    }) {
                        Text("Reset")
                    }
                }
            }
        }
    }
}
