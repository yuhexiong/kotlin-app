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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import android.util.Log
import androidx.room.Room
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.shoppinglistapplication.data.dao.ShoppingItemDao
import com.example.shoppinglistapplication.data.database.AppDatabase
import com.example.shoppinglistapplication.data.entity.ShoppingItem
import com.example.shoppinglistapplication.ui.theme.TopAppBarWithBackground
import com.example.shoppinglistapplication.ui.theme.borderedItem


class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var dao: ShoppingItemDao


    private val _isKeyboardVisible = mutableStateOf(false)
    val isKeyboardVisible: State<Boolean> get() = _isKeyboardVisible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "shopping-db").build()
        dao = db.shoppingItemDao()

        val decorView = window.decorView
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { v, insets ->
            val imeVisible = insets.isVisible(Type.ime())
            _isKeyboardVisible.value = imeVisible
            insets
        }

        setContent {
            ShoppingListScreen(dao = dao, isKeyboardVisible = isKeyboardVisible.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(dao: ShoppingItemDao,isKeyboardVisible: Boolean) {

    val items by dao.getAllItems().collectAsState(initial = emptyList())  // 🔥 這裡轉成 List

    var newItemText by remember { mutableStateOf("") }
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
        val trimmed = newItemText.trim()
        if (trimmed.isNotEmpty()) {
            coroutineScope.launch {
                dao.insertItem(ShoppingItem(name = trimmed))
                newItemText = ""
                // 關閉鍵盤
                keyboardController?.hide()
                // 確認有加入新東西
                isAddNewItem = true
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBarWithBackground(titleText = "購物清單")
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
                    val item = items[i]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .borderedItem(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = item.isBought,
                                onCheckedChange = { checked ->
                                    coroutineScope.launch {
                                        dao.updateItem(item.copy(isBought = checked))
                                    }
                                },
                                modifier = Modifier.scale(0.8f)
                            )
                            Text(text = item.name)
                        }

                        IconButton(onClick = {
                            coroutineScope.launch {
                                dao.deleteItem(item)
                            }
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
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    placeholder = { Text("請輸入您的商品") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addItem() })
                )

                Button(onClick = addItem) {
                    Text("加入")
                }
            }
        }
    }
}

