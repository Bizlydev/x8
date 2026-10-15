package com.x8.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection

private val X8Navy = Color(0xFF101828)
private val X8Blue = Color(0xFF2F6FED)
private val X8Background = Color(0xFFF5F7FB)

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        LocalLayoutDirection provides LayoutDirection.Rtl
                    ) {
                        DashboardScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "X8",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = X8Blue
            )
            Text(
                text = "مدیریت حساب و کتاب مغازه",
                style = MaterialTheme.typography.titleMedium,
                color = X8Navy
            )
            Text(
                text = "نسخهٔ اولیه · هنوز اطلاعاتی ثبت نشده است",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF667085)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = X8Navy)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        text = "وضعیت حساب‌ها",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "هنوز حسابی ثبت نشده",
                    color = Color(0xFFD0D5DD),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "بخش‌های برنامه",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        FeatureCard(
            icon = { Icon(Icons.Filled.People, contentDescription = null, tint = X8Blue) },
            title = "مشتری‌ها",
            subtitle = "ثبت و مدیریت اطلاعات مشتریان"
        )
        FeatureCard(
            icon = { Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = X8Blue) },
            title = "نسیه و پرداخت‌ها",
            subtitle = "ثبت رویدادهای مالی و مشاهدهٔ مانده‌حساب"
        )

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "این صفحه فقط اسکلت اولیه است؛ هنوز ذخیره‌سازی و عملیات مالی فعال نشده‌اند.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF667085),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun FeatureCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF667085))
            }
        }
    }
}
