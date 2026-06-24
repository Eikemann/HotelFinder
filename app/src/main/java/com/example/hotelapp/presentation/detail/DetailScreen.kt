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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hotelapp.R
import com.example.hotelapp.data.auth.TokenManager
import com.example.hotelapp.domain.remote.model.review.ReviewResponse
import com.example.hotelapp.navigation.Route
import com.example.hotelapp.presentation.bottomSheets.BookingBottomSheet
import com.example.hotelapp.presentation.components.BookingNowButton
import com.example.hotelapp.presentation.components.FeatureChip
import com.example.hotelapp.presentation.components.HotelImage
import com.example.hotelapp.presentation.components.PreviewCard
import com.example.hotelapp.presentation.components.RatingChip
import com.example.hotelapp.presentation.detail.components.DetailItem
import com.example.hotelapp.presentation.detail.components.DetailScreenTopBar
import com.example.hotelapp.presentation.detail.components.FacilityItem
import com.example.hotelapp.presentation.detail.components.ReviewItem


/**
 * Экран деталей отеля: фото, характеристики, описание, удобства, отзывы и кнопка брони.
 * Бронь и написание отзыва требуют входа — иначе пользователь уходит в граф авторизации.
 *
 * @param hotelId идентификатор отеля (строкой, как пришёл из навигации)
 */
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
    var showReviewDialog by remember { mutableStateOf(false) }

    // После успешной отправки отзыва закрываем форму и сбрасываем флаг.
    LaunchedEffect(detailViewModel.reviewJustSubmitted) {
        if (detailViewModel.reviewJustSubmitted) {
            showReviewDialog = false
            detailViewModel.consumeReviewSubmitted()
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            BookingBottomSheet(
                propertyId = hotelId.toIntOrNull() ?: 0,
                onClose = { showSheet = false }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                pricePerNight = hotel?.pricePerNight ?: "",
                onClick = {
                    if (TokenManager.isLoggedIn) {
                        showSheet = true
                    } else {
                        navController.navigate(Route.Auth.route)
                    }
                },
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
                Text(stringResource(R.string.detail_not_found), style = MaterialTheme.typography.titleMedium)
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
                    // HD-изображение в полном разрешении (targetSize = null => ORIGINAL,
                    // ARGB_8888) со спиннером на время загрузки.
                    HotelImage(
                        data = hotel.imageUrl ?: hotel.imageRes,
                        contentDescription = "",
                        targetSize = null,
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
            item { FacilitiesSection(amenities = hotel.amenities) }
            item { PreviewSection(imageList) }

            item {
                ReviewSection(
                    reviews = detailViewModel.reviews,
                    onWriteReview = {
                        if (TokenManager.isLoggedIn) {
                            showReviewDialog = true
                        } else {
                            navController.navigate(Route.Auth.route)
                        }
                    }
                )
            }
        }

    }}

        // Рисуется в том же окне (не Dialog/bottom-sheet), чтобы IME сохраняла область
        // ввода — это нужно для корректной работы не-латинских клавиатур (например, русской).
        if (showReviewDialog) {
            WriteReviewOverlay(
                isSubmitting = detailViewModel.isSubmittingReview,
                error = detailViewModel.reviewError,
                onDismiss = {
                    showReviewDialog = false
                    detailViewModel.consumeReviewError()
                },
                onSubmit = { rating, title, comment ->
                    hotelId.toIntOrNull()?.let {
                        detailViewModel.submitReview(it, rating, title, comment)
                    }
                }
            )
        }
    }
}

/** Ряд чипов с особенностями отеля и чипом рейтинга. */
@Composable
private fun FeatureSection(modifier: Modifier = Modifier, rating: String) {
    LazyRow(modifier = modifier.padding(12.dp)) {
        item {
            FeatureChip(
                icon = Icons.Default.Notifications,
                text = stringResource(R.string.detail_feature)
            )
        }
        item {
            FeatureChip(
                icon = Icons.Default.Notifications,
                text = stringResource(R.string.detail_feature)
            )
        }
        item {
            FeatureChip(
                icon = Icons.Default.Notifications,
                text = stringResource(R.string.detail_feature)
            )
        }
        item {
            RatingChip(
                rating = rating
            )
        }
    }
}

/** Заголовок отеля: название, цена за ночь и местоположение. */
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
                text = "$$pricePerNight",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.night_suffix),
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
                contentDescription = stringResource(R.string.cd_location),
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

/** Блок характеристик: тип, спальни, санузлы, площадь. */
@Composable
private fun DetailSection() {
    Text(
        text = stringResource(R.string.detail_details),
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
        DetailItem(stringResource(R.string.detail_type_hotels), Icons.Default.Home)
        DetailItem(stringResource(R.string.detail_bedrooms), Icons.Default.Home)
        DetailItem(stringResource(R.string.detail_bathrooms), Icons.Default.LocationOn)
        DetailItem(stringResource(R.string.detail_area), Icons.Default.Star)
    }
}

/** Описание отеля со сворачиванием: показывает несколько строк и кнопку «читать далее/свернуть». */
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
            text = stringResource(R.string.detail_description),
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
            text = if (isExpanded) stringResource(R.string.detail_read_less) else stringResource(R.string.detail_read_more),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
        )
    }
}

/** Горизонтальная лента превью-изображений отеля. */
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
            text = stringResource(R.string.detail_preview),
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

/** Сетка удобств отеля; при пустом списке показывает заглушку. */
@Composable
private fun FacilitiesSection(amenities: List<String>) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
    ) {

        Text(stringResource(R.string.detail_facilities), fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(12.dp))

        if (amenities.isEmpty()) {
            Text(
                text = stringResource(R.string.detail_no_amenities),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                amenities.forEach { amenity ->
                    FacilityItem(amenity, amenityIcon(amenity))
                }
            }
        }
    }
}

/** Подбирает иконку для удобства по ключевым словам в его названии. */
private fun amenityIcon(name: String): ImageVector {
    val n = name.lowercase()
    return when {
        "wifi" in n || "wi-fi" in n -> Icons.Default.Build
        "pool" in n || "beach" in n -> Icons.Default.Person
        "park" in n -> Icons.Default.LocationOn
        "restaurant" in n || "breakfast" in n || "bar" in n -> Icons.Default.Home
        else -> Icons.Default.Star
    }
}

/** Блок отзывов: заголовок со ссылкой «написать отзыв» и список отзывов (или заглушка). */
@Composable
private fun ReviewSection(
    reviews: List<ReviewResponse>,
    onWriteReview: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.detail_reviews), fontWeight = FontWeight.Medium)
            Text(
                text = stringResource(R.string.detail_write_review),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onWriteReview() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (reviews.isEmpty()) {
            Text(
                text = stringResource(R.string.detail_no_reviews),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            reviews.forEach { review ->
                ReviewItem(
                    name = review.userFullName ?: stringResource(R.string.review_guest),
                    rating = stringResource(R.string.rating_out_of_10, review.rating ?: 0),
                    comment = listOfNotNull(review.title, review.comment)
                        .joinToString(" — ")
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Форма написания отзыва поверх экрана: выбор оценки (1..10), заголовок и комментарий.
 * Рисуется как оверлей в том же окне (см. пояснение про IME выше).
 */
@Composable
private fun WriteReviewOverlay(
    isSubmitting: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, title: String, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(8) }
    var title by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    // Затемнение на весь экран; нажатие по нему закрывает форму.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            // Перехватываем нажатия по самой форме, чтобы они её не закрывали.
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
        Text(
            text = stringResource(R.string.review_dialog_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.review_rating_label, rating), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { if (rating > 1) rating-- }) { Text("-") }
            Text(
                text = rating.toString(),
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = { if (rating < 10) rating++ }) { Text("+") }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.review_field_title)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text(stringResource(R.string.review_field_comment)) },
            modifier = Modifier.fillMaxWidth()
        )
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSubmit(rating, title, comment) },
            enabled = !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSubmitting) stringResource(R.string.review_submitting) else stringResource(R.string.review_submit))
        }
        }
    }
    }
}

val imageList = listOf(
    R.drawable.hotelimage,
    R.drawable.hotelimage,
    R.drawable.hotelimage,
    R.drawable.hotelimage,
    R.drawable.hotelimage
)