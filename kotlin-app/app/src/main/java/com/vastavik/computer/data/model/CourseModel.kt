package com.vastavik.computer.data.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlinx.serialization.Serializable

@Serializable
data class CourseModel(
    val id: String = "",
    val title: String = "",
    val iconName: String = "code",
    val color: Long = 0xFF4F46E5L,
    val description: String = "",
    val order: Int = 0,
    val catalogEnabled: Boolean = true,
    val createdAt: String = "",
    val language: String = "",
    val thumbnailUrl: String = "",
    val isPublished: Boolean = true,
    val createdBy: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "iconName" to iconName,
        "color" to color,
        "description" to description,
        "order" to order,
        "catalogEnabled" to catalogEnabled,
        "createdAt" to if (createdAt.isEmpty()) FieldValue.serverTimestamp() else createdAt,
        "language" to language,
        "thumbnailUrl" to thumbnailUrl,
        "isPublished" to isPublished,
        "createdBy" to createdBy
    )

    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): CourseModel {
            val data = doc.data ?: return CourseModel(id = doc.id)
            return CourseModel(
                id = doc.id,
                title = data["title"] as? String ?: "",
                iconName = data["iconName"] as? String ?: "code",
                color = (data["color"] as? Number)?.toLong() ?: 0xFF4F46E5L,
                description = data["description"] as? String ?: "",
                order = (data["order"] as? Number)?.toInt() ?: 0,
                catalogEnabled = data["catalogEnabled"] as? Boolean ?: true,
                createdAt = data["createdAt"]?.toString() ?: "",
                language = data["language"] as? String ?: "",
                thumbnailUrl = data["thumbnailUrl"] as? String ?: "",
                isPublished = data["isPublished"] as? Boolean ?: true,
                createdBy = data["createdBy"] as? String ?: ""
            )
        }
    }
}

@Serializable
data class PartModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val order: Int = 0,
    val createdAt: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "description" to description,
        "order" to order,
        "createdAt" to if (createdAt.isEmpty()) FieldValue.serverTimestamp() else createdAt
    )

    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): PartModel {
            val data = doc.data ?: return PartModel(id = doc.id)
            return PartModel(
                id = doc.id,
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                order = (data["order"] as? Number)?.toInt() ?: 0,
                createdAt = data["createdAt"]?.toString() ?: ""
            )
        }
    }
}

@Serializable
data class SubpartModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val order: Int = 0,
    val createdAt: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "description" to description,
        "order" to order,
        "createdAt" to if (createdAt.isEmpty()) FieldValue.serverTimestamp() else createdAt
    )

    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): SubpartModel {
            val data = doc.data ?: return SubpartModel(id = doc.id)
            return SubpartModel(
                id = doc.id,
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                order = (data["order"] as? Number)?.toInt() ?: 0,
                createdAt = data["createdAt"]?.toString() ?: ""
            )
        }
    }
}

@Serializable
data class LessonModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val youtubeUrl: String = "",
    val duration: String = "",
    val youtubePositionSec: Int = 0,
    val whiteboardImageUrl: String = "",
    val codeSample: String = "",
    val notes: String = "",
    val order: Int = 0,
    val createdAt: String = ""
) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "description" to description,
        "youtubeUrl" to youtubeUrl,
        "duration" to duration,
        "youtubePositionSec" to youtubePositionSec,
        "whiteboardImageUrl" to whiteboardImageUrl,
        "codeSample" to codeSample,
        "notes" to notes,
        "order" to order,
        "createdAt" to if (createdAt.isEmpty()) FieldValue.serverTimestamp() else createdAt
    )

    fun toNavigationMap(): Map<String, Any> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "youtubeUrl" to youtubeUrl,
        "duration" to duration,
        "youtubePositionSec" to youtubePositionSec,
        "whiteboardImageUrl" to whiteboardImageUrl,
        "codeSample" to codeSample,
        "notes" to notes
    )

    companion object {
        fun fromSnapshot(doc: DocumentSnapshot): LessonModel {
            val data = doc.data ?: return LessonModel(id = doc.id)
            return LessonModel(
                id = doc.id,
                title = data["title"] as? String ?: "",
                description = data["description"] as? String ?: "",
                youtubeUrl = data["youtubeUrl"] as? String ?: "",
                duration = data["duration"] as? String ?: "",
                youtubePositionSec = (data["youtubePositionSec"] as? Number)?.toInt() ?: 0,
                whiteboardImageUrl = data["whiteboardImageUrl"] as? String ?: "",
                codeSample = data["codeSample"] as? String ?: "",
                notes = data["notes"] as? String ?: "",
                order = (data["order"] as? Number)?.toInt() ?: 0,
                createdAt = data["createdAt"]?.toString() ?: ""
            )
        }
    }
}
