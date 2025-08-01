package com.example.khizana.presentation.feature.priceRules.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.khizana.R
import com.example.khizana.domain.model.DiscountCodeDomain
import com.example.khizana.presentation.feature.priceRules.view.components.DiscountCodeCard
import com.example.khizana.presentation.feature.priceRules.view.components.DiscountCodeInputDialog
import com.example.khizana.presentation.feature.priceRules.viewModel.PriceRuleViewModel
import com.example.khizana.utilis.ConfirmationDialog
import com.example.khizana.utilis.CustomLoadingIndicator
import com.example.khizana.utilis.Response


@Composable
fun DiscountCodeScreen(
    priceRuleViewModel: PriceRuleViewModel = hiltViewModel(),
    priceRuleId: String,
    snackBarHostState: SnackbarHostState,
    navigationController: NavHostController
) {

    val discountCodes = priceRuleViewModel.discountCodes.collectAsStateWithLifecycle().value
    val showDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val code = remember { mutableStateOf("") }
    val codeId = remember { mutableStateOf("") }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.code))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = 1f,
        restartOnPlay = false
    )
    LaunchedEffect(Unit) {
        priceRuleViewModel.getDiscountCodes(priceRuleId)
        priceRuleViewModel.message.collect {
            snackBarHostState.showSnackbar(it)
        }
    }
    LazyColumn(
        Modifier
            .padding(top = 64.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    navigationController.popBackStack()
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_icon),
                        tint = Color.Black.copy(0.7f),
                    )
                }
                IconButton(onClick = {
                    showDialog.value = true
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discount Codes",
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        when (discountCodes) {
            is Response.Success<*> -> {
                discountCodes as Response.Success<DiscountCodeDomain>

                if (discountCodes.result?.discountCodes?.isEmpty() == true) {
                    item {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    LocalConfiguration.current.screenHeightDp.dp / 2
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LottieAnimation(
                                composition = composition,
                                progress = progress,
                                modifier = Modifier.size(
                                    150.dp
                                )
                            )
                            Text(
                                text = "This price rule is waiting for its discount code. Create one now.", style = TextStyle(
                                    fontSize = 18.sp,
                                    color = Color.Black.copy(0.5f),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                } else {
                    items(discountCodes.result?.discountCodes?.size ?: 0,
                        key = { discountCodes.result?.discountCodes?.get(it)?.id ?: "" }) {
                        DiscountCodeCard(
                            modifier = Modifier.animateItem(),
                            code = discountCodes.result?.discountCodes?.get(it)?.code ?: "",
                            usageCount = discountCodes.result?.discountCodes?.get(it)?.usageCount
                                ?: 0,
                            onDeleteClick = {

                                codeId.value =
                                    discountCodes.result?.discountCodes?.get(it)?.id ?: ""
                                showDeleteDialog.value = true

                            },
                            onEditClick = {
                                code.value =
                                    discountCodes.result?.discountCodes?.get(it)?.code ?: ""
                                codeId.value =
                                    discountCodes.result?.discountCodes?.get(it)?.id ?: ""
                                showDialog.value = true
                            }
                        )
                    }
                }

            }

            is Response.Failure -> {
                item {
                    Text(text = discountCodes.exception)
                }
            }

            is Response.Loading -> {
                item {
                    CustomLoadingIndicator(modifier = Modifier.fillParentMaxSize())
                }
            }
        }
    }

    ConfirmationDialog(
        text = "Are you sure you want to delete this discount code?",
        showDialog = showDeleteDialog.value,
        onConfirm = {
            priceRuleViewModel.deleteDiscountCode(
                priceRuleId = priceRuleId,
                discountCodeId = codeId.value
            )
        },
        onDismiss = {
            showDeleteDialog.value = false
        }
    )
    DiscountCodeInputDialog(
        showDialog = showDialog,
        onConfirm = {
            if (code.value.isEmpty()) {
                priceRuleViewModel.createDiscountCode(priceRuleId, it)
            } else {
                priceRuleViewModel.editDiscountCode(priceRuleId, codeId.value, it)
            }
        },
        code = code
    )
}




