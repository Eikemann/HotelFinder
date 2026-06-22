package com.example.hotelapp.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hotelapp.R
import com.example.hotelapp.domain.local.model.Filters
import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.navigation.Route
import com.example.hotelapp.presentation.components.FilterCard


@Composable
fun DashboardScreen(
    viewModel: DashboardScreenViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var selectedFilterId by remember { mutableStateOf<Int?>(null) }

    val filters = listOf(
        Filters(id = 1, filterName = stringResource(R.string.filter_hotels), filterIcon = R.drawable.icon),
        Filters(id = 2, filterName = stringResource(R.string.filter_condos), filterIcon = R.drawable.icon),
        Filters(id = 3, filterName = stringResource(R.string.filter_houses), filterIcon = R.drawable.icon),
        Filters(id = 4, filterName = stringResource(R.string.filter_villas), filterIcon = R.drawable.icon),
        Filters(id = 5, filterName = stringResource(R.string.filter_apartments), filterIcon = R.drawable.icon)
    )
    // Maps a backend city name to its localized display label. Cities without an
    // entry fall back to the raw name returned by the API.
    val cityLabels = mapOf(
        "Antalya" to stringResource(R.string.city_antalya),
        "Istanbul" to stringResource(R.string.city_istanbul),
        "Dubai" to stringResource(R.string.city_dubai),
        "Hurghada" to stringResource(R.string.city_hurghada),
        "Bangkok" to stringResource(R.string.city_bangkok),
        "Tokyo" to stringResource(R.string.city_tokyo),
        "Seoul" to stringResource(R.string.city_seoul),
    )

    LaunchedEffect(Unit) {
        viewModel.loadHotels()
    }

    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        viewModel.errorMessage?.let { error ->
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
            FilterSection(
                filterList = filters,
                selectedFilterId = selectedFilterId,
                onFilterClicked = { filterId -> selectedFilterId = filterId },
            )
        }

        items(viewModel.hotelsByCity, key = { (city, _) -> city }) { (city, cityHotels) ->
            // Display the localized label, but pass the backend city name to search.
            val label = cityLabels[city] ?: city
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 16.dp, start = 24.dp, end = 24.dp, top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.dashboard_places_in, label),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                )
                Text(
                    stringResource(R.string.common_see_all),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate(Route.Search.createWithCity(city))
                    }
                )
            }

            CarouselSection(
                hotelList = cityHotels,
                onRedHeartClick = {
                    Toast.makeText(context, context.getString(R.string.toast_heart), Toast.LENGTH_SHORT).show()
                },
                onHotelCardClick = { hotelId ->
                    navController.navigate(Route.HotelDetail.create(hotelId))
                }
            )
        }
    }
}


@Composable
private fun FilterSection(
    filterList: List<Filters>,
    selectedFilterId: Int?,
    onFilterClicked: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
    ) {
        items(filterList) { filter ->
            FilterCard(
                filterName = filter.filterName,
                filterIcon = filter.filterIcon,
                isActive = filter.id == selectedFilterId,
                onClick = { onFilterClicked(filter.id) },
            )
        }
    }
}

@Composable
private fun CarouselSection(
    hotelList: List<Hotel>,
    onRedHeartClick: () -> Unit,
    onHotelCardClick: (String) -> Unit
) {
    LazyRow(modifier = Modifier.padding(end = 12.dp)) {
        items(hotelList, key = { it.id }) { hotel ->
            CaruselHotelCard(
                hotel = hotel,
                onFavoriteClick = onRedHeartClick,
                onHotelCardClick = { onHotelCardClick(hotel.id.toString()) }
            )
        }
    }
}

@Composable
private fun CaruselHotelCard(
    modifier: Modifier = Modifier,
    hotel: Hotel,
    isFavorite: Boolean = true,
    onFavoriteClick: () -> Unit,
    onHotelCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .width(260.dp)
            .aspectRatio(0.83f)
            .padding(start = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onHotelCardClick,
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Box {
                AsyncImage(
                    model = hotel.imageUrl ?: hotel.imageRes,
                    contentDescription = hotel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White, CircleShape)
                        .clickable { onFavoriteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = stringResource(R.string.cd_favorite),
                        tint = Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxHeight()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = hotel.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = hotel.rating,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = hotel.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${hotel.pricePerNight}",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.night_suffix),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


