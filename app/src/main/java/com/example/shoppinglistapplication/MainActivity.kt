package com.example.shoppinglistapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.core.view.ViewCompat
import kotlinx.coroutines.launch
import androidx.core.view.WindowInsetsCompat.Type
import android.util.Log


class MainActivity : ComponentActivity() {

    private val _isKeyboardVisible = mutableStateOf(false)
    val isKeyboardVisible: State<Boolean> get() = _isKeyboardVisible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { v, insets ->
            val imeVisible = insets.isVisible(Type.ime())
            _isKeyboardVisible.value = imeVisible
            insets
        }

        setContent {
            ShoppingListScreen(isKeyboardVisible = isKeyboardVisible.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(isKeyboardVisible: Boolean) {
    val items = remember { mutableStateListOf("蘋果", "香蕉", "橘子", "牛奶", "麵包", "蛋糕", "起司") }
    val checkedStates = remember { mutableStateListOf(false, false, false, false, false, false, false) }
    val newItemText = remember { mutableStateOf("") }
    var isAddNewItem by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isKeyboardVisible, isAddNewItem) {
        // 有加入新東西且確定已關閉鍵盤在滾到底部
        if (!isKeyboardVisible && isAddNewItem) {
            listState.animateScrollToItem(items.size - 1)
            isAddNewItem = false
        }
    }

    // 加入商品
    val addItem = {
        val trimmed = newItemText.value.trim()
        if (trimmed.isNotEmpty()) {
            items.add(trimmed)
            checkedStates.add(false)
            newItemText.value = ""

            // 關閉鍵盤
            keyboardController?.hide()

            // 確認有加入新東西
            isAddNewItem = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("購物清單") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(items.size) { i ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checkedStates[i],
                                onCheckedChange = { checkedStates[i] = it },
                                modifier = Modifier.scale(0.8f)
                            )
                            Text(text = items[i])
                        }

                        IconButton(onClick = {
                            items.removeAt(i)
                            checkedStates.removeAt(i)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }

            // 底部輸入與加入按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newItemText.value,
                    onValueChange = { newItemText.value = it },
                    placeholder = { Text("請輸入您的商品") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addItem() })
                )

                Button(onClick = { addItem() }) {
                    Text("加入")
                }
            }
        }
    }
}

