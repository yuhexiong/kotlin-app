package com.example.shoppinglistapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "購物清單")
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            // 商品清單
            Column(
                modifier = Modifier.weight(1f) // 元件撐滿空間
            ) {
                val itemCount = 10
                val checkedStates = remember { mutableStateListOf(*Array(itemCount) { false }) }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(itemCount) { i ->
                    // 迴圈加入商品
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checkedStates[i],
                                onCheckedChange = { checkedStates[i] = it },
                                modifier = Modifier.scale(0.8f)
                            )
                            Text(text = "商品 ${i + 1}")
                        }

                        // 右側刪除鍵
                         IconButton(onClick = { /* 刪除項目 */ }) {
                             Icon(Icons.Default.Delete, contentDescription = "Delete")
                         }
                    }}
                }
            }

            // 底層輸入框和加入按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 輸入框
                Text(text = "請輸入您的商品")

                Button(onClick = { /* Handle Add Action */ }) {
                    Text("加入")
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShoppingListScreen()
}
