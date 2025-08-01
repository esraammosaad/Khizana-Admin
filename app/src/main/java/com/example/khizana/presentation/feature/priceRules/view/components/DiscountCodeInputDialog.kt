package com.example.khizana.presentation.feature.priceRules.view.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.khizana.R
import com.example.khizana.domain.model.DiscountCodeRequestDomain
import com.example.khizana.domain.model.DiscountCodeDomainRequest
import com.example.khizana.ui.theme.primaryColor
import com.example.khizana.ui.theme.secondaryColor

@Composable
fun DiscountCodeInputDialog(
    showDialog: MutableState<Boolean>,
    onConfirm: (DiscountCodeRequestDomain) -> Unit,
    code: MutableState<String>,
) {
    val discountCode = remember { mutableStateOf("") }
    val error = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(code.value) {
        if (code.value.isNotEmpty()) {
            discountCode.value = code.value
        }
    }
    if (showDialog.value) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = {
                showDialog.value = false
                discountCode.value = ""
                code.value = ""
            },
            title = {

                Text(stringResource(R.string.add_discount_code))
            },
            text = {
                Column {
                    InputDiscountCodeCard(
                        code = discountCode
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (error.value)
                        Text(
                            errorText.value,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (discountCode.value.isEmpty()) {
                            error.value = true
                            errorText.value =
                                context.getString(R.string.please_enter_a_discount_code)
                            return@Button
                        }
                        if (discountCode.value.isNotEmpty()) {
                            error.value = false
                            errorText.value = ""
                            onConfirm(
                                DiscountCodeRequestDomain(
                                    DiscountCodeDomainRequest(
                                        discountCode.value
                                    )
                                )
                            )
                            showDialog.value = false
                            discountCode.value = ""
                            code.value = ""
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = secondaryColor,
                        containerColor = primaryColor
                    )
                ) {
                    Text(
                        stringResource(R.string.save)
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDialog.value = false
                        discountCode.value = ""
                        code.value = ""

                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = primaryColor,
                        containerColor = secondaryColor
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

