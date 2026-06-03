package com.example.hotelapp.domain.remote.model.review

/** Mirrors backend `ReviewResponse`. Rating is on a 1-10 scale. */
data class ReviewResponse(
    val id: Long,
    val propertyId: Long?,
    val userId: Long?,
    val userFullName: String?,
    val rating: Int?,
    val title: String?,
    val comment: String?,
    val createdAt: String?
)

/** Mirrors backend `CreateReviewRequest`. */
data class CreateReviewRequest(
    val propertyId: Long,
    val rating: Int,
    val title: String,
    val comment: String?
)
