package com.alinco.measureapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

private val units = linkedMapOf(
    "Millimeter (mm)" to 0.001,
    "Centimeter (cm)" to 0.01,
    "Meter (m)" to 1.0,
    "Inch (in)" to 0.0254,
    "Foot (ft)" to 0.3048,
    "Yard (yd)" to 0.9144
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen() {
    var inputValue by remember { mutableStateOf("1") }
    var fromUnit by remember { mutableStateOf("Meter (m)") }
    var fromExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Length Unit Converter", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Value") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = fromExpanded,
            onExpandedChange = { fromExpanded = !fromExpanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = fromUnit,
                onValueChange = {},
                label = { Text("From unit") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = fromExpanded, onDismissRequest = { fromExpanded = false }) {
                units.keys.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit) },
                        onClick = {
                            fromUnit = unit
                            fromExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Divider()
        Spacer(Modifier.height(12.dp))

        Text("Converted values", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        val valueMeters = inputValue.toDoubleOrNull()?.times(units[fromUnit] ?: 1.0)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                units.forEach { (unitName, factor) ->
                    val result = valueMeters?.div(factor)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(unitName)
                        Text(
                            if (result != null) String.format(Locale.US, "%.4f", result) else "-",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
