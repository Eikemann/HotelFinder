package com.example.hotelapp.presentation.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hotelapp.R
import com.example.hotelapp.navigation.Route
import com.example.hotelapp.presentation.bottomSheets.BookingBottomSheet
import com.example.hotelapp.presentation.components.BookingNowButton
import com.example.hotelapp.presentation.components.FeatureChip
import com.example.hotelapp.presentation.components.PreviewCard
import com.example.hotelapp.presentation.components.RatingChip
import com.example.hotelapp.presentation.detail.components.DetailItem
import com.example.hotelapp.presentation.detail.components.DetailScreenTopBar
import com.example.hotelapp.presentation.detail.components.FacilityItem
import com.example.hotelapp.presentation.detail.components.ReviewItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController, hotelId: String
) {

    val detailViewModel: DetailScreenViewModel = viewModel()
    val hotel = detailViewModel.hotel

    LaunchedEffect(hotelId) {
        hotelId.toIntOrNull()?.let { id ->
            detailViewModel.loadHotelById(id)
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            BookingBottomSheet(
                onClose = { showSheet = false }
            )
        }
    }

    Scaffold(
        topBar = {
            DetailScreenTopBar(
                onBackButtonClick = { navController.navigate(Route.Dashboard.route) },
                scrollBehavior = scrollBehavior,
                onDetailsButtonClick = {}
            )

        },
        bottomBar = {
            BookingNowButton(
                onClick = { showSheet = true },
            )
        }
    ) { paddingValues ->
        if (detailViewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (hotel == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hotel not found.", style = MaterialTheme.typography.titleMedium)
            }
        } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            detailViewModel.errorMessage?.let { error ->
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.513f)
                        .padding(16.dp)
                ) {
                    AsyncImage(
                        model = hotel.imageUrl ?: hotel.imageRes,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
            item { FeatureSection(rating = hotel.rating) }
            item {
                HotelHeadline(
                    hotelName = hotel.name,
                    pricePerNight = hotel.pricePerNight,
                    location = hotel.location
                )
            }
            item { DetailSection() }
            item {
                HotelDescription(
                    description = hotel.description
                )
            }
            item { FacilitiesSection() }
            item { PreviewSection(imageList) }

            item { ReviewSection() }
        }

    }}
}

@Composable
private fun FeatureSection(modifier: Modifier = Modifier, rating: String) {
    LazyRow(modifier = modifier.padding(12.dp)) {
        item {
            FeatureChip(
                icon = Icons.Default.Notifications,
                text = "chip"
            )
        }
        item {
            FeatureChip(
                icon = Icons.Default.Notifications,
                text = "chip"
            )
        }
        item {
            FeatureChip(
                icon = Icons.Default.Notifications,
                text = "chip"
            )
        }
        item {
            RatingChip(
                rating = rating
            )
        }
    }
}

@Composable
private fun HotelHeadline(
    hotelName: String,
    pricePerNight: String,
    location: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = hotelName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = pricePerNight,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " /night",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailSection() {
    Text(
        text = "Details",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 28.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DetailItem("Hotels", Icons.Default.Home)
        DetailItem("4 Bedrooms", Icons.Default.Home)
        DetailItem("2 Bathrooms", Icons.Default.LocationOn)
        DetailItem("4000 sqft", Icons.Default.Star)
    }
}

@Composable
private fun HotelDescription(
    description: String,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 3,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.animateContentSize(),
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (isExpanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isExpanded) "Read Less" else "Read More",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
        )
    }
}

@Composable
fun PreviewSection(
    images: List<Int>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(images) { image ->
                PreviewCard(image)
            }
        }
    }
}

@Composable
private fun FacilitiesSection() {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {

        Text("Facilities", fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 4,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FacilityItem("Pool", Icons.Default.Person)
            FacilityItem("Wi-Fi", Icons.Default.Build)
            FacilityItem("Restaurant", Icons.Default.Home)
            FacilityItem("Parking", Icons.Default.LocationOn)
            FacilityItem("Parking", Icons.Default.LocationOn)
            FacilityItem("Parking", Icons.Default.LocationOn)
            FacilityItem("Parking", Icons.Default.LocationOn)
            FacilityItem("Parking", Icons.Default.LocationOn)
            FacilityItem("Parking", Icons.Default.LocationOn)
        }
    }
}

@Composable
private fun ReviewSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Review", fontWeight = FontWeight.Medium)
            Text("See All", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        ReviewItem(
            name = "Jenny Wilson",
            rating = "5.0",
            comment = "Very nice and comfortable hotel."
        )
    }
}

val imageList = listOf(
    R.drawable.hotelimage,
    R.drawable.hotelimage,
    R.drawable.hotelimage,
    R.drawable.hotelimage,
    R.drawable.hotelimage
)