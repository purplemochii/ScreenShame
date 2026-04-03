package com.example.screenshame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screenshame.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    fun validate(): Boolean {
        var valid = true
        if (!email.contains("@") || !email.contains(".")) {
            emailError = "valid email ..?"
            valid = false
        } //else emailError = ""
        if (password.length < 8) {
            passwordError = "min 8 characters for your password cmon"
            valid = false
        } //else passwordError = ""
        return valid
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // app icon from default icon pack
        Surface (
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = black
        ) {
            Box (contentAlignment = Alignment.Center) {
                Icon (
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "app icon",
                    tint = white,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // title
        Text (
            text = "ScreenShame",
            style = MaterialTheme.typography.displayLarge,
            color = black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text (
            text = "because you won't do it yourself.",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // email fiels
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            placeholder = { Text("you@example.com") },
            isError = emailError.isNotEmpty(),
            supportingText = if (emailError.isNotEmpty()) {{ Text(emailError, color = red) }}
                                else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = black,
                unfocusedBorderColor = border,
                focusedLabelColor = black,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            isError = passwordError.isNotEmpty(),
            supportingText = if (passwordError.isNotEmpty()) {{ Text(passwordError, color = red) }}
                                else null,
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                    else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = true}) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Visibility
                                        else Icons.Outlined.VisibilityOff,
                        contentDescription = if (passwordVisible) "hide passoword"
                                                else "show password",
                        tint = textSecondary
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = black,
                unfocusedBorderColor = border,
                focusedLabelColor = black,
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // login button
        Button (
            onClick = { if (validate()) onLoginSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = white
            )
        ) {
            Text(
                text = "unlock dashboard",
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

