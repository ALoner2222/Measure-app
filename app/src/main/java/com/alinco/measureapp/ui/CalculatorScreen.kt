package com.alinco.measureapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.PI

private enum class ShapeType(val label: String) {
    RECTANGLE("Rectangle area"),
    CIRCLE("Circle area"),
    PIPE_VOLUME("Pipe internal volume"),
    CYLINDER_VOLUME("Cylinder volume")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    var shape by remember { mutableStateOf(ShapeType.PIPE_VOLUME) }
    var expanded by remember { mutableStateOf(false) }

    var dim1 by remember { mutableStateOf("") }
    var dim2 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Area / Volume Calculator", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                readOnly = true,
                value = shape.label,
                onValueChange = {},
                label = { Text("Calculation type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ShapeType.values().forEach {
                    DropdownMenuItem(text = { Text(it.label) }, onClick = {
                        shape = it
                        expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        when (shape) {
            ShapeType.RECTANGLE -> {
                OutlinedTextField(dim1, { dim1 = it }, label = { Text("Length (m)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(dim2, { dim2 = it }, label = { Text("Width (m)") }, modifier = Modifier.fillMaxWidth())
            }
            ShapeType.CIRCLE -> {
                OutlinedTextField(dim1, { dim1 = it }, label = { Text("Diameter (m)") }, modifier = Modifier.fillMaxWidth())
            }
            ShapeType.PIPE_VOLUME -> {
                OutlinedTextField(dim1, { dim1 = it }, label = { Text("Internal diameter (m)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(dim2, { dim2 = it }, label = { Text("Pipe length (m)") }, modifier = Modifier.fillMaxWidth())
            }
            ShapeType.CYLINDER_VOLUME -> {
                OutlinedTextField(dim1, { dim1 = it }, label = { Text("Diameter (m)") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(dim2, { dim2 = it }, label = { Text("Height / Length (m)") }, modifier = Modifier.fillMaxWidth())
            }
        }

        Spacer(Modifier.height(20.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        val d1 = dim1.toDoubleOrNull()
        val d2 = dim2.toDoubleOrNull()

        val (resultLabel, resultValue) = when (shape) {
            ShapeType.RECTANGLE -> "Area (m²)" to if (d1 != null && d2 != null) d1 * d2 else null
            ShapeType.CIRCLE -> "Area (m²)" to if (d1 != null) PI * (d1 / 2).let { it * it } else null
            ShapeType.PIPE_VOLUME -> "Volume (m³ / liters)" to if (d1 != null && d2 != null) PI * (d1 / 2).let { it * it } * d2 else null
            ShapeType.CYLINDER_VOLUME -> "Volume (m³)" to if (d1 != null && d2 != null) PI * (d1 / 2).let { it * it } * d2 else null
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(resultLabel, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (resultValue != null) String.format(Locale.US, "%.5f", resultValue) else "Enter values above",
                    style = MaterialTheme.typography.headlineSmall
                )
                if (shape == ShapeType.PIPE_VOLUME && resultValue != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "= ${String.format(Locale.US, "%.2f", resultValue * 1000)} liters",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
