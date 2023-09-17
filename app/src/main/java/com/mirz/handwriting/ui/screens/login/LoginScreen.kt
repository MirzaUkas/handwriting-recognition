package com.mirz.handwriting.ui.screens.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mirz.handwriting.R
import com.mirz.handwriting.common.Response
import com.mirz.handwriting.ui.theme.HandwritingTheme
import com.mirz.handwriting.ui.theme.NeutralGrey
import com.mirz.handwriting.ui.theme.Purple30
import com.mirz.handwriting.ui.theme.Purple40
import com.mirz.handwriting.ui.theme.typography

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState

    Surface(
        color = Purple40
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Belajar Menulis",
                    style = typography.h3
                )
                Image(
                    painter = painterResource(id = R.drawable.il_people),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = "Login",
                        style = typography.h3.copy(color = Color.Black),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    OutlinedTextField(
                        value = uiState.email,
                        placeholder = { Text("Your Email Address", style = typography.body1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = "Password",
                                tint = colors.primary
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Purple30.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White,
                            textColor = Color.Black
                        ),
                        onValueChange = viewModel::onEmailChange,
                    )

                    OutlinedTextField(
                        value = uiState.password,
                        placeholder = { Text("Your Password", style = typography.body1) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Password",
                                tint = colors.primary
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = "Password",
                                tint = colors.primary
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Purple30.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White,
                            textColor = Color.Black
                        ),
                        onValueChange = viewModel::onPasswordChange,
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(16.dp),
                        onClick = viewModel::onLogin
                    ) {
                        Text("Login", style = typography.button)
                    }
                }

            }

            when (val data = uiState.signInWithGoogleResponse) {
                is Response.Success -> LaunchedEffect(Unit) { navigateToHome() }
                is Response.Loading -> CircularProgressIndicator(
                    backgroundColor = colors.primary,
                    modifier = Modifier
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .align(Alignment.Center)

                )

                is Response.Failure -> Toast.makeText(
                    LocalContext.current,
                    data.e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()

                else -> Unit
            }
        }
    }


}


@Composable
fun LoginOptions() {
    Column {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(16.dp),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_google),
                tint = Color.Unspecified,
                contentDescription = "Google Icon"
            )
            Spacer(Modifier.width(8.dp))
            Text("Login with Google", style = typography.button)
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF0056B2),
                contentColor = colors.onPrimary
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(16.dp),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_facebook),
                contentDescription = "Facebook Icon",
            )
            Spacer(Modifier.width(8.dp))
            Text("Login with Facebook", style = typography.button)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Divider(Modifier.weight(0.5f))
            Text("OR", style = typography.body1.copy(color = NeutralGrey))
            Divider(Modifier.weight(0.5f))
        }
    }
}
