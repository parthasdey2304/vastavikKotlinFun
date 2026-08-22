package com.vastavik.computer.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vastavik.computer.data.model.ChatMessage
import com.vastavik.computer.data.model.ChatSession
import com.vastavik.computer.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentChatId = MutableStateFlow("")
    val currentChatId = _currentChatId.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val chatSessions: StateFlow<List<ChatSession>> = if (uid.isNotEmpty()) {
        firestoreRepository.streamChatSessions(uid)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    } else {
        MutableStateFlow(emptyList())
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || uid.isEmpty()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true
        )
        _messages.value = _messages.value + userMessage
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val botMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = "Thinking...",
                    isUser = false
                )
                _messages.value = _messages.value + botMessage
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message ?: "Failed to send message"
            }
        }
    }

    fun createNewChat() {
        if (uid.isEmpty()) return
        val sessionId = UUID.randomUUID().toString()
        val session = ChatSession(
            id = sessionId,
            title = "New Chat",
            userId = uid
        )
        _currentChatId.value = sessionId
        _messages.value = emptyList()
        firestoreRepository.createChatSession(session)
    }

    fun loadChat(session: ChatSession) {
        _currentChatId.value = session.id
        _messages.value = session.messages
    }
}
