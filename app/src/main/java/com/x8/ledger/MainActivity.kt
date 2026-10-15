package com.x8.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import com.x8.ledger.data.LedgerRepository
import com.x8.ledger.data.local.CustomerEntity
import com.x8.ledger.data.local.LedgerEntryEntity
import com.x8.ledger.data.local.X8Database
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val X8Navy = Color(0xFF101828)
private val X8Blue = Color(0xFF2F6FED)
private val X8Background = Color(0xFFF5F7FB)
private val Muted = Color(0xFF667085)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = X8Blue,
                    background = X8Background,
                    surface = Color.White,
                    onBackground = X8Navy,
                    onSurface = X8Navy
                )
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    Surface(modifier = Modifier.fillMaxSize(), color = X8Background) {
                        X8App()
                    }
                }
            }
        }
    }
}

@Composable
private fun X8App() {
    val context = LocalContext.current
    val repository = remember {
        LedgerRepository(X8Database.getInstance(context.applicationContext))
    }
    val customers by repository.observeCustomers().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }
    var showAddCustomer by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val selectedCustomer = customers.firstOrNull { it.id == selectedCustomerId }

    if (selectedCustomerId != null && selectedCustomer != null) {
        CustomerDetailScreen(
            customer = selectedCustomer,
            repository = repository,
            onBack = { selectedCustomerId = null },
            onMessage = { message = it }
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("X8", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = X8Blue)
                    Text("مدیریت حساب و کتاب مغازه", style = MaterialTheme.typography.titleMedium, color = X8Navy)
                    Text("اطلاعات روی همین گوشی ذخیره می‌شود", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = X8Blue)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = X8Navy)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.People, contentDescription = null, tint = Color.White)
                        Text("  مشتری‌ها", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                    Text("تعداد مشتریان ثبت‌شده: ${customers.size}", color = Color(0xFFD0D5DD))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("فهرست مشتری‌ها", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Button(onClick = { showAddCustomer = true }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Text(" مشتری جدید")
                }
            }

            if (customers.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.People, contentDescription = null, tint = X8Blue)
                        Text("هنوز مشتری‌ای ثبت نشده است", fontWeight = FontWeight.SemiBold)
                        Text("برای شروع، دکمهٔ «مشتری جدید» را بزن.", color = Muted, textAlign = TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(customers, key = { it.id }) { customer ->
                        CustomerCard(customer = customer, onClick = { selectedCustomerId = customer.id })
                    }
                }
            }
        }
    }

    if (showAddCustomer) {
        AddCustomerDialog(
            onDismiss = { showAddCustomer = false },
            onSave = { name, phone, notes ->
                scope.launch {
                    val result = repository.addCustomer(name, phone, notes)
                    message = result.fold(
                        onSuccess = { "مشتری با موفقیت ذخیره شد." },
                        onFailure = { it.message ?: "ذخیرهٔ مشتری ناموفق بود." }
                    )
                    if (result.isSuccess) showAddCustomer = false
                }
            }
        )
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = { message = null },
            title = { Text("پیام X8") },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = { message = null }) { Text("باشه") } }
        )
    }
}

@Composable
private fun CustomerCard(customer: CustomerEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Filled.People, contentDescription = null, tint = X8Blue)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(customer.phone.ifBlank { "شماره تماس ثبت نشده" }, style = MaterialTheme.typography.bodySmall, color = Muted)
            }
            Text("جزئیات ←", color = X8Blue, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت مشتری جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام مشتری *") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("شماره تماس") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("یادداشت") }, minLines = 2)
            }
        },
        confirmButton = { Button(onClick = { onSave(name, phone, notes) }, enabled = name.isNotBlank()) { Text("ذخیره") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun CustomerDetailScreen(
    customer: CustomerEntity,
    repository: LedgerRepository,
    onBack: () -> Unit,
    onMessage: (String) -> Unit
) {
    val entries by repository.observeEntries(customer.id).collectAsState(initial = emptyList())
    val balance by repository.observeBalance(customer.id).collectAsState(initial = 0L)
    val scope = rememberCoroutineScope()
    var entryType by remember { mutableStateOf(LedgerEntryEntity.CREDIT) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                Text(" بازگشت")
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(customer.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("حساب مشتری", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = X8Navy)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("مانده حساب", color = Color(0xFFD0D5DD))
                Text("${formatToman(balance)} تومان", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    when {
                        balance > 0L -> "مشتری به شما بدهکار است"
                        balance < 0L -> "حساب دارای بستانکاری است"
                        else -> "حساب تسویه است"
                    },
                    color = Color(0xFFD0D5DD)
                )
            }
        }

        Text("ثبت رویداد مالی", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (entryType == LedgerEntryEntity.CREDIT) {
                Button(onClick = { entryType = LedgerEntryEntity.CREDIT }, modifier = Modifier.weight(1f)) { Text("ثبت نسیه") }
            } else {
                OutlinedButton(onClick = { entryType = LedgerEntryEntity.CREDIT }, modifier = Modifier.weight(1f)) { Text("ثبت نسیه") }
            }
            if (entryType == LedgerEntryEntity.PAYMENT) {
                Button(onClick = { entryType = LedgerEntryEntity.PAYMENT }, modifier = Modifier.weight(1f)) { Text("ثبت پرداخت") }
            } else {
                OutlinedButton(onClick = { entryType = LedgerEntryEntity.PAYMENT }, modifier = Modifier.weight(1f)) { Text("ثبت پرداخت") }
            }
        }
        OutlinedTextField(
            value = amount,
            onValueChange = { value -> amount = value.filter { it.isDigit() }.take(18) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("مبلغ به تومان") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("توضیحات (اختیاری)") },
            singleLine = true
        )
        Button(
            onClick = {
                val parsedAmount = amount.toLongOrNull()
                if (parsedAmount == null || parsedAmount <= 0L) {
                    onMessage("مبلغ معتبر و بزرگ‌تر از صفر وارد کن.")
                } else {
                    scope.launch {
                        val selectedType = entryType
                        val result = if (selectedType == LedgerEntryEntity.CREDIT) {
                            repository.postCredit(customer.id, parsedAmount, note)
                        } else {
                            repository.postPayment(customer.id, parsedAmount, note)
                        }
                        result.fold(
                            onSuccess = {
                                amount = ""
                                note = ""
                                onMessage(if (selectedType == LedgerEntryEntity.CREDIT) "نسیه ثبت شد." else "پرداخت ثبت شد.")
                            },
                            onFailure = { onMessage(it.message ?: "ثبت رویداد مالی ناموفق بود.") }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.ReceiptLong, contentDescription = null)
            Text(if (entryType == LedgerEntryEntity.CREDIT) " ثبت نسیه" else " ثبت پرداخت")
        }

        Divider()
        Text("گردش حساب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (entries.isEmpty()) {
            Text("هنوز رویداد مالی ثبت نشده است.", color = Muted)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(if (entry.type == LedgerEntryEntity.CREDIT) "نسیه" else "پرداخت", fontWeight = FontWeight.SemiBold)
                                Text(entry.note.ifBlank { "بدون توضیحات" }, color = Muted, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                "${if (entry.type == LedgerEntryEntity.CREDIT) "+" else "−"}${formatToman(entry.amountToman)} تومان",
                                color = if (entry.type == LedgerEntryEntity.CREDIT) Color(0xFFB42318) else Color(0xFF027A48),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatToman(amount: Long): String =
    NumberFormat.getIntegerInstance(Locale("fa", "IR")).format(amount)
