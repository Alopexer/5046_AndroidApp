package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.RunningPlan
import com.example.myapplication.data.RunningPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    runningPlanViewModel: RunningPlanViewModel,
    userEmail: String,
    userName: String
) {
    val latestPlan = remember { mutableStateOf<RunningPlan?>(null) }

    LaunchedEffect(userEmail) {
        runningPlanViewModel.getLatestPlanByEmail(userEmail) { plan ->
            latestPlan.value = plan
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Welcome, $userName") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            HealthSummarySection(latestPlan.value)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "News",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PlanRecommendationCards(navController)
        }
    }
}




@Composable
fun HealthSummarySection(latestPlan: RunningPlan?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "Last Running Record",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (latestPlan != null) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    HealthBox("Distance", "${"%.2f".format(latestPlan.distance)} km", Color(0xFFE3F2FD), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    HealthBox("Calories", "${latestPlan.calories} kcal", Color(0xFFFFEBEE), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    HealthBox("Duration", "${latestPlan.duration} min", Color(0xFFE8F5E9), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    HealthBox("Pace", "${latestPlan.pace} ", Color(0xFFFFF3E0), Modifier.weight(1f))
                }
            } else {
                Text("No running record found.", color = Color.Gray)
            }
        }
    }
}




@Composable
fun HealthBox(title: String, value: String, backgroundColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}





@Composable
fun PlanRecommendationCards(navController: NavController) {
    Column {
        PlanCard(
            title = "Beginner Running Plan",
            subtitle = "15 minutes a day, easy to start",
            description = "Great for new runners!",
            imageRes = R.drawable.running_beginner,
            newsId = "1",
            navController = navController
        )
        PlanCard(
            title = "5KM Training",
            subtitle = "Build up gradually",
            description = "Improve endurance week by week.",
            imageRes = R.drawable.training_5km,
            newsId = "2",
            navController = navController
        )
        PlanCard(
            title = "Fat Loss + Running",
            subtitle = "Cardio meets clean eating",
            description = "Follow for real results.",
            imageRes = R.drawable.fatburn_run,
            newsId = "3",
            navController = navController
        )
    }
}



@Composable
fun PlanCard(
    title: String,
    subtitle: String,
    description: String,
    imageRes: Int,
    newsId: String,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("newsDetail/$newsId")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
