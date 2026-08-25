package com.vastavik.computer.data.api

import com.vastavik.computer.data.model.CourseModel
import com.vastavik.computer.data.model.LessonModel
import com.vastavik.computer.data.model.PartModel
import com.vastavik.computer.data.model.SubpartModel
import com.vastavik.computer.data.model.BannerModel

fun CourseDto.toModel() = CourseModel(
    id = id,
    title = title,
    iconName = iconName,
    color = color,
    description = description,
    order = order,
    catalogEnabled = catalogEnabled,
    createdAt = createdAt?.toString() ?: "",
    language = language,
    thumbnailUrl = thumbnailUrl ?: "",
    isPublished = isPublished,
    createdBy = createdBy ?: ""
)

fun PartDto.toModel() = PartModel(
    id = id,
    title = title,
    description = description ?: "",
    order = order,
    createdAt = createdAt?.toString() ?: ""
)

fun SubpartDto.toModel() = SubpartModel(
    id = id,
    title = title,
    description = description ?: "",
    order = order,
    createdAt = createdAt?.toString() ?: ""
)

fun LessonDto.toModel() = LessonModel(
    id = id,
    title = title,
    description = description,
    youtubeUrl = youtubeUrl,
    duration = duration ?: "",
    youtubePositionSec = youtubePositionSec ?: 0,
    whiteboardImageUrl = whiteboardImageUrl ?: "",
    codeSample = codeSample ?: "",
    notes = notes ?: "",
    order = order,
    createdAt = createdAt?.toString() ?: "",
    // extended fields
    youtubeVideoId = youtubeVideoId ?: HmacUtil.extractVideoId(youtubeUrl) ?: "",
    durationSec = durationSec ?: 0,
    isPremium = isPremium,
    isPublished = isPublished,
    videoFormat = videoFormat
)

fun BannerDto.toModel() = BannerModel(
    id = id,
    title = title,
    subtitle = "",
    actionLink = "",
    color = 0xFF4F46E5L,
    order = order,
    imageUrl = imageUrl,
    createdAt = ""
)
