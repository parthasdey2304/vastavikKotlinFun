package com.vastavik.computer.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Generic paginated wrapper matching backend: { data: [...], nextCursor: string|null }
data class ApiListResponse<T>(
    @SerializedName("data") val data: List<T>,
    @SerializedName("nextCursor") val nextCursor: String? = null
)
data class ApiSingleResponse<T>(
    @SerializedName("data") val data: T
)

// DTOs mirror Firestore docs (keep lenient — missing fields default)
data class CourseDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("iconName") val iconName: String = "code",
    @SerializedName("color") val color: Long = 0xFF4F46E5L,
    @SerializedName("description") val description: String = "",
    @SerializedName("order") val order: Int = 0,
    @SerializedName("catalogEnabled") val catalogEnabled: Boolean = true,
    @SerializedName("language") val language: String = "",
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("isPublished") val isPublished: Boolean = true,
    @SerializedName("createdBy") val createdBy: String? = null,
    @SerializedName("createdAt") val createdAt: Any? = null
)

data class PartDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("order") val order: Int = 0,
    @SerializedName("createdAt") val createdAt: Any? = null
)

data class SubpartDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("order") val order: Int = 0,
    @SerializedName("createdAt") val createdAt: Any? = null
)

data class LessonDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("youtubeUrl") val youtubeUrl: String = "",
    @SerializedName("youtubeVideoId") val youtubeVideoId: String? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("durationSec") val durationSec: Int? = null,
    @SerializedName("youtubePositionSec") val youtubePositionSec: Int? = null,
    @SerializedName("whiteboardImageUrl") val whiteboardImageUrl: String? = null,
    @SerializedName("codeSample") val codeSample: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("order") val order: Int = 0,
    @SerializedName("isPremium") val isPremium: Boolean = false,
    @SerializedName("isPublished") val isPublished: Boolean = true,
    @SerializedName("videoFormat") val videoFormat: String = "vscode",
    @SerializedName("path") val path: String? = null,
    @SerializedName("createdAt") val createdAt: Any? = null
)

data class BannerDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("imageUrl") val imageUrl: String = "",
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("order") val order: Int = 0
)

interface VastavikApiService {

    @GET("health")
    suspend fun health(): Map<String, Any>

    // Courses
    @GET("api/v1/courses")
    suspend fun getCourses(@Query("limit") limit: Int = 20): ApiListResponse<CourseDto>

    @GET("api/v1/courses/{courseId}")
    suspend fun getCourse(@Path("courseId") courseId: String): ApiSingleResponse<CourseDto>

    @GET("api/v1/courses/{courseId}/parts")
    suspend fun getParts(@Path("courseId") courseId: String): ApiListResponse<PartDto>

    @GET("api/v1/courses/{courseId}/parts/{partId}/subparts")
    suspend fun getSubparts(
        @Path("courseId") courseId: String,
        @Path("partId") partId: String
    ): ApiListResponse<SubpartDto>

    @GET("api/v1/courses/{courseId}/parts/{partId}/subparts/{subpartId}/lessons")
    suspend fun getLessons(
        @Path("courseId") courseId: String,
        @Path("partId") partId: String,
        @Path("subpartId") subpartId: String,
        @Query("limit") limit: Int = 50
    ): ApiListResponse<LessonDto>

    // Alternative global lessons with query (fallback)
    @GET("api/v1/lessons")
    suspend fun getLessonsGlobal(
        @Query("courseId") courseId: String? = null,
        @Query("partId") partId: String? = null,
        @Query("subpartId") subpartId: String? = null,
        @Query("limit") limit: Int = 20
    ): ApiListResponse<LessonDto>

    @GET("api/v1/lessons/{lessonId}")
    suspend fun getLesson(@Path("lessonId") lessonId: String): ApiSingleResponse<LessonDto>

    @GET("api/v1/banners")
    suspend fun getBanners(): ApiListResponse<BannerDto>
}
