package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    var isEditing by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("David") }
    var age by remember { mutableStateOf("25") }
    var gender by remember { mutableStateOf("Male") }
    var email by remember { mutableStateOf("machine6657@gmail.com") }

    val genderOptions = listOf("Male", "Female", "Other")
    var genderExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 头像占位图
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Photo",
                modifier = Modifier.size(60.dp)
            )
        }

        // Email
        ProfileItem(label = "Email", value = email)

        // 昵称
        if(isEditing) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            ProfileItem(label = "Username", value = username)
        }

        // 年龄
        if (isEditing) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            ProfileItem(label = "Age", value = age)
        }

        // 性别选择
        if (isEditing) {
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("性别") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    genderOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                gender = it
                                genderExpanded = false
                            }
                        )
                    }
                }
            }
        } else {
            ProfileItem(label = "性别", value = gender)
        }


        // 编辑/保存 按钮
        if (isEditing) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        isEditing = false
                        // TODO：保存数据（如 DataStore / Room）
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.padding(end = 4.dp))
                    Text("Save")
                }

                OutlinedButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
            }
        } else {
            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = "编辑", modifier = Modifier.padding(end = 4.dp))
                Text("编辑资料")
            }
        }
    }
}


@Composable
fun ProfileItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
