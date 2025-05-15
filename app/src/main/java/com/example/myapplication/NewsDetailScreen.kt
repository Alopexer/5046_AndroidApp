package com.example.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(newsId: String?, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (newsId) {
                "1" -> NewsContent(
                    title = "Beginner Running Plan",
                    tag = "6 Weeks Plan",
                    imageRes = R.drawable.running_beginner,
                    content = "Start your fitness journey with a beginner-friendly plan designed to build your running habit with just 15 minutes a day. This 6-week schedule eases you into running with a mix of walking and jogging sessions that increase in intensity gradually. It’s perfect for those who are new to running or returning after a break.\n" +
                            "\n" +
                            "The focus is on building consistency, confidence, and avoiding injury. You don’t need expensive gear or previous experience — just a pair of comfortable shoes and commitment. By the end of this program, you’ll be able to run continuously for 30 minutes, while improving your cardiovascular health, energy levels, and mood. This plan is not about speed or distance, but about showing up every day. Let's take the first step, together.\n"
                )
                "2" -> NewsContent(
                    title = "5KM Training",
                    tag = "Endurance Build",
                    imageRes = R.drawable.training_5km,
                    content = "Ready to run your first 5KM? This structured 8-week plan is designed for runners who want to build endurance and reach that milestone with confidence. Whether you're training for a fun run, a charity event, or personal accomplishment, this guide will walk (and run) you through weekly goals that gradually increase distance and stamina.\n" +
                            "\n" +
                            "Each week combines running, recovery walks, rest days, and optional strength exercises to support muscle balance. You’ll also get mental tips for staying motivated when the legs get tired. This isn’t just about covering 5 kilometres — it's about proving to yourself that you're capable of more than you think. Lace up, breathe steady, and let's go the distance.\n"
                )
                "3" -> NewsContent(
                    title = "Fat Loss + Running",
                    tag = "Fat Burn",
                    imageRes = R.drawable.fatburn_run,
                    content = "Looking to shed fat and feel energized? This program combines the proven power of cardio through running with mindful eating habits to maximize fat loss and boost metabolism. Instead of focusing solely on weight, this approach encourages sustainable changes in both movement and nutrition.\n" +
                            "\n" +
                            "You’ll follow a 4-week progressive run schedule focused on fat-burning zones — alternating between steady-state runs and fat-blasting intervals. Alongside this, we recommend high-protein, whole-food meals and hydration strategies to support muscle retention and recovery. With consistency, you'll notice improvements not just in your body shape, but also your sleep, energy, and self-confidence. Healthy fat loss isn't about extremes — it’s about smart, steady steps. Let’s get lean and strong, the right way.\n"
                )
                else -> Text("❓ Unknown News Item")
            }
        }
    }
}

@Composable
fun NewsContent(title: String, tag: String, imageRes: Int, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tag,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFF64B5F6), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )
        }
    }
}

