package com.vastavik.computer.data.repository

import com.vastavik.computer.data.api.VastavikApiService
import com.vastavik.computer.data.api.toModel
import com.vastavik.computer.data.model.BannerModel
import com.vastavik.computer.data.model.CourseModel
import com.vastavik.computer.data.model.LessonModel
import com.vastavik.computer.data.model.PartModel
import com.vastavik.computer.data.model.SubpartModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backend-backed repository. All reads go through Express API (Firebase Admin + HMAC).
 * FirestoreRepository remains as offline/cache fallback — callers can try API first, fallback to Firestore on failure.
 */
@Singleton
class VastavikApiRepository @Inject constructor(
    private val api: VastavikApiService
) {
    suspend fun getCourses(limit: Int = 20): List<CourseModel> =
        api.getCourses(limit).data.map { it.toModel() }

    suspend fun getCourse(courseId: String): CourseModel =
        api.getCourse(courseId).data.toModel()

    suspend fun getParts(courseId: String): List<PartModel> =
        api.getParts(courseId).data.map { it.toModel() }

    suspend fun getSubparts(courseId: String, partId: String): List<SubpartModel> =
        api.getSubparts(courseId, partId).data.map { it.toModel() }

    suspend fun getLessons(courseId: String, partId: String, subpartId: String): List<LessonModel> =
        api.getLessons(courseId, partId, subpartId).data.map { it.toModel() }

    suspend fun getLesson(lessonId: String): LessonModel =
        api.getLesson(lessonId).data.toModel()

    suspend fun getBanners(): List<BannerModel> =
        api.getBanners().data.map { it.toModel() }

    // Health check
    suspend fun health(): Boolean = try {
        api.health(); true
    } catch (_: Exception) { false }
}
