package com.example.hotelapp.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelapp.R
import com.example.hotelapp.domain.local.model.FilterState

/** Связывает ключевое слово фильтра удобства (ищется в тексте amenity) с его подписью. */
private data class FacilityOption(val keyword: String, val label: String)

/**
 * Нижний лист фильтров поиска: страна, сортировка, диапазон цены, рейтинг, удобства и тип
 * размещения. Локально хранит выбор и возвращает собранный [FilterState] через [onApply].
 *
 * @param currentFilter текущее состояние фильтров (для предзаполнения)
 * @param onApply вызывается с новым [FilterState] при нажатии «Применить»
 * @param onReset сброс фильтров к значениям по умолчанию
 */
@Composable
fun FilterBottomSheet(
    currentFilter: FilterState = FilterState(),
    countries: List<String> = emptyList(),
    accommodationTypes: List<String> = emptyList(),
    priceCeiling: Float = 1000f,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit
) {
    var selectedCountry by remember { mutableStateOf(currentFilter.country) }
    var selectedSort by remember { mutableStateOf(currentFilter.sort) }
    var priceRange by remember { mutableStateOf(currentFilter.priceRange) }
    var selectedRating by remember { mutableStateOf(currentFilter.starRating) }
    var selectedFacilities by remember { mutableStateOf(currentFilter.facilities) }
    var selectedAccommodationTypes by remember { mutableStateOf(currentFilter.accommodationTypes) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.filter_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        CountrySection(
            countries = countries,
            selected = selectedCountry,
            onSelect = { selectedCountry = it }
        )
        SortSection(
            selected = selectedSort,
            onSelect = { selectedSort = it }
        )
        PriceRangeSection(
            range = priceRange,
            ceiling = priceCeiling,
            onRangeChange = { priceRange = it }
        )
        StarRatingSection(
            selected = selectedRating,
            onSelect = { selectedRating = it }
        )
        FacilitiesSection(
            selected = selectedFacilities,
            onToggle = { keyword, checked ->
                selectedFacilities = if (checked) selectedFacilities + keyword else selectedFacilities - keyword
            }
        )
        AccommodationSection(
            types = accommodationTypes,
            selected = selectedAccommodationTypes,
            onToggle = { type, checked ->
                selectedAccommodationTypes = if (checked) selectedAccommodationTypes + type else selectedAccommodationTypes - type
            }
        )

        Spacer(Modifier.height(24.dp))

        BottomActions(
            onReset = onReset,
            onApply = {
                onApply(
                    FilterState(
                        country = selectedCountry,
                        sort = selectedSort,
                        priceRange = priceRange,
                        starRating = selectedRating,
                        facilities = selectedFacilities,
                        accommodationTypes = selectedAccommodationTypes
                    )
                )
            }
        )
    }
}

/** Секция выбора страны чипами (скрывается, если стран нет). */
@Composable
private fun CountrySection(
    countries: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    if (countries.isEmpty()) return

    SectionHeader(stringResource(R.string.filter_country))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        countries.forEach { country ->
            FilterChip(
                selected = country == selected,
                onClick = { onSelect(if (selected == country) null else country) },
                label = { Text(localizedCountryName(country)) }
            )
        }
    }

    Spacer(Modifier.height(16.dp))
}

/** Секция выбора сортировки (популярность, цена по убыванию/возрастанию). */
@Composable
private fun SortSection(
    selected: String?,
    onSelect: (String?) -> Unit
) {
    SectionHeader(stringResource(R.string.filter_sort))

    val sorts = listOf(
        "Highest Popularity" to stringResource(R.string.sort_popularity),
        "Highest Price" to stringResource(R.string.sort_price_high),
        "Lowest Price" to stringResource(R.string.sort_price_low)
    )
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        sorts.forEach { (value, label) ->
            FilterChip(
                selected = value == selected,
                onClick = { onSelect(if (selected == value) null else value) },
                label = { Text(label) }
            )
        }
    }

    Spacer(Modifier.height(16.dp))
}

/** Секция диапазона цены: двойной слайдер и подписи минимума/максимума. */
@Composable
private fun PriceRangeSection(
    range: ClosedFloatingPointRange<Float>,
    ceiling: Float,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    SectionHeader(stringResource(R.string.filter_price_range))

    RangeSlider(
        value = range,
        onValueChange = onRangeChange,
        valueRange = 0f..ceiling,
        steps = 9
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        PriceChip("$${range.start.toInt()}")
        PriceChip("$${range.endInclusive.toInt()}")
    }

    Spacer(Modifier.height(16.dp))
}

/** Секция выбора минимального рейтинга (от 1 до 5 звёзд). */
@Composable
private fun StarRatingSection(
    selected: Int?,
    onSelect: (Int?) -> Unit
) {
    SectionHeader(stringResource(R.string.filter_star_rating))

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (5 downTo 1).forEach { star ->
            FilterChip(
                selected = star == selected,
                onClick = { onSelect(if (selected == star) null else star) },
                label = {
                    Row {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp))
                        Text(" $star")
                    }
                }
            )
        }
    }

    Spacer(Modifier.height(16.dp))
}

/** Секция фильтра по удобствам (Wi-Fi, бассейн, парковка, завтрак) чекбоксами. */
@Composable
private fun FacilitiesSection(
    selected: Set<String>,
    onToggle: (String, Boolean) -> Unit
) {
    // keyword сопоставляется (подстрока без учёта регистра) с текстом удобств отеля.
    val facilities = listOf(
        FacilityOption("wi-fi", stringResource(R.string.fac_wifi)),
        FacilityOption("pool", stringResource(R.string.fac_pool)),
        FacilityOption("parking", stringResource(R.string.fac_parking)),
        FacilityOption("breakfast", stringResource(R.string.fac_breakfast))
    )

    SectionHeader(stringResource(R.string.filter_facilities))

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        facilities.forEach { facility ->
            FacilityCheckItem(
                label = facility.label,
                checked = facility.keyword in selected,
                onCheckChange = { checked -> onToggle(facility.keyword, checked) }
            )
        }
    }

    Spacer(Modifier.height(16.dp))
}

/** Секция фильтра по типу размещения (скрывается, если типов нет). */
@Composable
private fun AccommodationSection(
    types: List<String>,
    selected: Set<String>,
    onToggle: (String, Boolean) -> Unit
) {
    if (types.isEmpty()) return

    SectionHeader(stringResource(R.string.filter_accommodation))

    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        types.forEach { type ->
            FacilityCheckItem(
                label = accommodationLabel(type),
                checked = type in selected,
                onCheckChange = { checked -> onToggle(type, checked) }
            )
        }
    }
}

/** Возвращает локализованное название страны по её имени с бэкенда (иначе — как есть). */
@Composable
private fun localizedCountryName(country: String): String = when (country.trim().lowercase()) {
    "turkey" -> stringResource(R.string.country_turkey)
    "france" -> stringResource(R.string.country_france)
    "italy", "italia" -> stringResource(R.string.country_italia)
    "germany" -> stringResource(R.string.country_germany)
    "uae", "united arab emirates" -> stringResource(R.string.country_uae)
    "thailand" -> stringResource(R.string.country_thailand)
    "south korea", "korea" -> stringResource(R.string.country_south_korea)
    "japan" -> stringResource(R.string.country_japan)
    "egypt" -> stringResource(R.string.country_egypt)
    "spain" -> stringResource(R.string.country_spain)
    "greece" -> stringResource(R.string.country_greece)
    "usa", "united states", "us" -> stringResource(R.string.country_usa)
    "georgia" -> stringResource(R.string.country_georgia)
    "india" -> stringResource(R.string.country_india)
    "vietnam" -> stringResource(R.string.country_vietnam)
    else -> country
}

/** Локализованная подпись для значения enum типа размещения с бэкенда (HOTEL, APARTMENT, ...). */
@Composable
private fun accommodationLabel(type: String): String = when (type.uppercase()) {
    "HOTEL" -> stringResource(R.string.acc_hotels)
    "APARTMENT" -> stringResource(R.string.acc_apartments)
    "VILLA" -> stringResource(R.string.acc_villas)
    "HOUSE" -> stringResource(R.string.acc_houses)
    else -> type.lowercase().replaceFirstChar { it.uppercase() }
}

/** Строка «чекбокс + подпись» для одного варианта удобства/типа. */
@Composable
private fun FacilityCheckItem(label: String, checked: Boolean, onCheckChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckChange)
        Text(label)
    }
}

/** Нижние кнопки листа фильтров: «Сбросить» и «Применить». */
@Composable
private fun BottomActions(onReset: () -> Unit, onApply: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(modifier = Modifier.weight(1f), onClick = onReset) {
            Text(stringResource(R.string.filter_reset))
        }
        Button(modifier = Modifier.weight(1f), onClick = onApply) {
            Text(stringResource(R.string.filter_apply))
        }
    }
}

/** Заголовок секции фильтра с необязательной кнопкой-действием справа. */
@Composable
private fun SectionHeader(title: String, action: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        action?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

/** Маленький чип с подписью цены (концы диапазона). */
@Composable
private fun PriceChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
