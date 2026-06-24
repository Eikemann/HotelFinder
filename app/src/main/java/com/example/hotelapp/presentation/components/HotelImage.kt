package com.example.hotelapp.presentation.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import coil.size.Size

// Общие размеры декодирования (в px), чтобы запросы на экране и предзагрузчики в ViewModel
// давали одинаковые ключи кэша Coil. Держим их как единственный источник истины —
// несовпадение вызывает повторное декодирование (и сводит предзагрузку на нет).
val HotelThumbSize = Size(320, 224)       // карточки карусели на главном (~260x180dp)
val HotelSearchThumbSize = Size(228, 280) // карточки списка поиска (~114dp ширина)
val HotelScheduleThumbSize = Size(150, 150)
val HotelBookingDetailSize = Size(640, 400)

/**
 * Строит запрос Coil, используемый для всех изображений отелей.
 *
 * [targetSize] `null` означает HD: декодировать оригинал в полном качестве (ARGB_8888) —
 * используется на экране деталей. Ненулевой размер означает сжатую миниатюру
 * (RGB_565, ~вдвое меньше памяти), уменьшенную до этого размера в пикселях — для
 * списков/каруселей, где качеством жертвуют ради плавности прокрутки.
 */
fun hotelImageRequest(context: Context, data: Any?, targetSize: Size?): ImageRequest =
    ImageRequest.Builder(context)
        .data(data)
        .apply {
            if (targetSize != null) {
                size(targetSize)
                scale(Scale.FILL)
                allowRgb565(true)
                // Без crossfade на миниатюрах: он анимируется при каждом появлении
                // (включая попадания в кэш памяти), вызывая перерисовку кадров
                // при прокрутке. Оставляем его только для большого HD-изображения деталей.
                crossfade(false)
            } else {
                size(Size.ORIGINAL)
                crossfade(true)
            }
        }
        .build()

/**
 * Изображение отеля со спиннером загрузки и заглушкой при ошибке.
 *
 * Использует [rememberAsyncImagePainter] (а не SubcomposeAsyncImage), чтобы избежать
 * субкомпозиции на каждую карточку. Этот painter НЕ определяет размер вью автоматически,
 * поэтому для миниатюр вызывающий код ОБЯЗАН передать [targetSize]; только HD-изображение
 * деталей передаёт `null`. Вызывающий должен задать [modifier] ограниченный размер.
 */
@Composable
fun HotelImage(
    data: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    targetSize: Size? = null,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val request = remember(data, targetSize) { hotelImageRequest(context, data, targetSize) }
    val painter = rememberAsyncImagePainter(model = request)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            contentScale = contentScale,
        )
        when (painter.state) {
            is AsyncImagePainter.State.Loading ->
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
            is AsyncImagePainter.State.Error ->
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            else -> Unit
        }
    }
}
