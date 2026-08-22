package com.vastavik.computer.ui.screens.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vastavik.computer.data.model.LessonModel
import com.vastavik.computer.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoLessonViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _lessonData = MutableStateFlow<LessonModel?>(null)
    val lessonData = _lessonData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadLesson(courseId: String, partId: String, subpartId: String, lessonId: String) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                firestoreRepository.streamLessons(courseId, partId, subpartId).collect { lessons ->
                    _lessonData.value = lessons.find { it.id == lessonId }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "Failed to load lesson"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
