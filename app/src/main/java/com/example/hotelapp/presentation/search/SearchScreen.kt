package com.example.hotelapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hotelapp.R
import com.example.hotelapp.navigation.Route
import com.example.hotelapp.presentation.components.HotelCard
import com.example.hotelapp.presentation.search.components.FilterChips
import com.example.hotelapp.presentation.search.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel = viewModel(),
    navController: NavController,
    initialCity: String = ""
) {
    val allHotelsLabel = stringResource(R.string.chip_all_hotel)
    var selectedFilter by remember { mutableStateOf(allHotelsLabel) }

    LaunchedEffect(Unit) {
        viewModel.loadHotels()
    }

    // Restrict results to the city passed from a dashboard "See all" tap.
    LaunchedEffect(initialCity) {
        viewModel.applyCityFilter(initialCity)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // After a filter/search/city change the result set shrinks, so snap back to the top
    // instead of leaving the user stranded mid-list.
    val listState = rememberLazyListState()
    LaunchedEffect(viewModel.filterState, viewModel.searchQuery, viewModel.cityFilter) {
        listState.scrollToItem(0)
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            FilterBottomSheet(
                currentFilter = viewModel.filterState,
                countries = viewModel.availableCountries,
                accommodationTypes = viewModel.availableTypes,
                priceCeiling = viewModel.priceCeiling,
                onApply = { newFilter ->
                    viewModel.applyFilter(newFilter)
                    showSheet = false
                },
                onReset = {
                    viewModel.resetFilters()
                    showSheet = false
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // When Search is opened from a dashboard "See all" tap it carries a city
        // filter and the shared top bar is hidden, so show a back arrow + city
        // header to return to the dashboard. The plain Search tab has no city.
        if (initialCity.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 8.dp, end = 16.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back)
                    )
                }
                Text(
                    text = stringResource(R.string.dashboard_places_in, initialCity),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        SearchBar(
            query = viewModel.searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onFilterIconClick = { showSheet = true }
        )

        FilterChips(
            selected = selectedFilter,
            onSelected = { selectedFilter = it }
        )

        viewModel.errorMessage?.let { error ->
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

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // filteredHotels is a computed property — read it once per composition.
            val results = viewModel.filteredHotels

            ResultHeader(results.size)

            LazyColumn(state = listState) {
                items(results, key = { it.id }) { hotel ->
                    HotelCard(
                        hotel = hotel,
                        onCardClick = {
                            navController.navigate(Route.HotelDetail.create(hotel.id.toString()))
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ResultHeader(resultCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.search_recommended, resultCount),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
