package com.example.hotelapp.data

import com.example.hotelapp.R
import com.example.hotelapp.domain.local.model.Hotel

// Offline fallback shown when the backend is unreachable. Themed around the same
// cities the dashboard groups by, so the carousels still populate without a server.
val sampleHotels = listOf(
    Hotel(
        id = 1,
        name = "Grand Palace Hotel",
        city = "Istanbul",
        location = "Istanbul, Turkey",
        pricePerNight = "343",
        rating = "4.5",
        imageRes = R.drawable.hotel330841_1280,
        description = "A magnificent palace hotel on the Bosphorus, blending Ottoman grandeur with modern comfort. World-class dining, a luxurious hammam, and elegantly appointed rooms.",
        accommodationType = "Hotels"
    ),
    Hotel(
        id = 2,
        name = "Sea View Resort",
        city = "Antalya",
        location = "Antalya, Turkey",
        pricePerNight = "412",
        rating = "4.7",
        imageRes = R.drawable.hotel461615_1280,
        description = "Perched on the Turkish Riviera, this resort offers direct beach access and panoramic Mediterranean views. Features an infinity pool, private cabanas, and a seafood restaurant.",
        accommodationType = "Resorts"
    ),
    Hotel(
        id = 3,
        name = "Old Town Inn",
        city = "Antalya",
        location = "Antalya, Turkey",
        pricePerNight = "299",
        rating = "4.3",
        imageRes = R.drawable.hotel2799682_1280,
        description = "A cosy retreat in the historic Kaleiçi quarter. Steps from the marina and ancient walls, with a courtyard garden and rooftop breakfast terrace.",
        accommodationType = "Villas"
    ),
    Hotel(
        id = 4,
        name = "City Central Hotel",
        city = "Dubai",
        location = "Dubai, UAE",
        pricePerNight = "255",
        rating = "4.1",
        imageRes = R.drawable.building66789_1280,
        description = "Ideally located in Downtown Dubai, steps from the Burj Khalifa and Dubai Mall. Modern rooms with skyline views, a rooftop bar, and 24-hour concierge service.",
        accommodationType = "Hotels"
    ),
    Hotel(
        id = 5,
        name = "Riverside Boulevard Hotel",
        city = "Bangkok",
        location = "Bangkok, Thailand",
        pricePerNight = "380",
        rating = "4.6",
        imageRes = R.drawable.grandhotel2178413_1280,
        description = "A glamorous hotel on the banks of the Chao Phraya River. Features a riverside pool, a rooftop terrace, and a restaurant helmed by an award-winning chef.",
        accommodationType = "Apartments"
    ),
    Hotel(
        id = 6,
        name = "Han River Lodge",
        city = "Seoul",
        location = "Seoul, South Korea",
        pricePerNight = "330",
        rating = "4.4",
        imageRes = R.drawable.hotel2626098_1280,
        description = "A refined lodge along the banks of the Han River. Combines Korean hospitality with sleek design. Enjoy river views, a wellness centre, and proximity to Gangnam.",
        accommodationType = "Apartments"
    ),
    Hotel(
        id = 7,
        name = "The Royal Garden",
        city = "Tokyo",
        location = "Tokyo, Japan",
        pricePerNight = "410",
        rating = "4.8",
        imageRes = R.drawable.mountainhotel1567013_1280,
        description = "An exquisite blend of traditional Japanese aesthetics and contemporary luxury in the heart of Tokyo. Features a zen garden, kaiseki restaurant, onsen bath, and skyline views.",
        accommodationType = "Villas"
    ),
    Hotel(
        id = 8,
        name = "Red Sea View Hotel",
        city = "Hurghada",
        location = "Hurghada, Egypt",
        pricePerNight = "370",
        rating = "4.5",
        imageRes = R.drawable.lounge2930070_1280,
        description = "Overlooking the turquoise Red Sea, this resort offers a private beach and house reef. Enjoy the rooftop pool, diving excursions, and fresh Mediterranean cuisine.",
        accommodationType = "Hotels"
    ),
    Hotel(
        id = 9,
        name = "Marina Retreat",
        city = "Hurghada",
        location = "Hurghada, Egypt",
        pricePerNight = "290",
        rating = "4.3",
        imageRes = R.drawable.building66789_1280,
        description = "A charming retreat by the Hurghada Marina. Sun-soaked terraces, snorkelling trips, and lively evening promenades make this the perfect Red Sea getaway.",
        accommodationType = "Villas"
    ),
    Hotel(
        id = 10,
        name = "Desert Oasis Hotel",
        city = "Dubai",
        location = "Dubai, UAE",
        pricePerNight = "460",
        rating = "4.7",
        imageRes = R.drawable.hotel461615_1280,
        description = "A spectacular oasis in the Dubai desert featuring private pools, luxury tents, camel trekking, and stargazing sessions. Arabian heritage meets five-star indulgence.",
        accommodationType = "Hotels"
    ),
    Hotel(
        id = 11,
        name = "The Galata Inn",
        city = "Istanbul",
        location = "Istanbul, Turkey",
        pricePerNight = "270",
        rating = "4.2",
        imageRes = R.drawable.victoriafalls2646993_1280,
        description = "A beautifully restored townhouse steps from the Galata Tower. Period features meet modern amenities, with a tea lounge, Turkish breakfasts, and guided old-city tours.",
        accommodationType = "Apartments"
    ),
    Hotel(
        id = 12,
        name = "Riverside Breeze Resort",
        city = "Bangkok",
        location = "Bangkok, Thailand",
        pricePerNight = "340",
        rating = "4.6",
        imageRes = R.drawable.hotelimage,
        description = "A serene resort surrounded by lush tropical gardens near the river. Features an infinity pool, traditional Thai spa treatments, yoga pavilions, and authentic cuisine.",
        accommodationType = "Resorts"
    )
)
