package com.vastavik.computer.data.realtime

import com.vastavik.computer.data.model.*
import kotlinx.coroutines.flow.StateFlow

interface MeetingClient {
    val connectionState: StateFlow<ConnectionState>
    val participants: StateFlow<Map<String, Participant>>
    val chatMessages: StateFlow<List<LiveChatMessage>>
    val whiteboardState: StateFlow<WhiteboardState>
    val currentSession: StateFlow<ClassSession?>
    val localMicEnabled: StateFlow<Boolean>
    val localCameraEnabled: StateFlow<Boolean>
    val localHandRaised: StateFlow<Boolean>
    val localScreenSharing: StateFlow<Boolean>
    val currentUser: Participant?

    suspend fun joinClass(classId: String, userId: String, displayName: String): MeetingResult<ClassSession>
    suspend fun leaveClass(): MeetingResult<Unit>
    suspend fun toggleMic(enabled: Boolean): MeetingResult<Unit>
    suspend fun toggleCamera(enabled: Boolean): MeetingResult<Unit>
    suspend fun toggleHandRaise(raised: Boolean): MeetingResult<Unit>
    suspend fun toggleScreenShare(active: Boolean): MeetingResult<Unit>
    suspend fun requestScreenShare(): MeetingResult<Unit>
    suspend fun grantScreenShare(targetUserId: String): MeetingResult<Unit>
    suspend fun revokeScreenShare(targetUserId: String): MeetingResult<Unit>
    suspend fun sendChatMessage(text: String, replyTo: ReplyPreview?): MeetingResult<Unit>
    suspend fun sendEmojiReaction(emoji: String): MeetingResult<Unit>
    suspend fun kickParticipant(targetUserId: String): MeetingResult<Unit>
    suspend fun assignStarCast(targetUserId: String): MeetingResult<Unit>
    suspend fun revokeStarCast(targetUserId: String): MeetingResult<Unit>
    suspend fun toggleFeature(feature: DisabledFeature, enabled: Boolean): MeetingResult<Unit>
    suspend fun startRecording(): MeetingResult<Unit>
    suspend fun stopRecording(): MeetingResult<Unit>
    suspend fun updateWhiteboard(elements: List<WhiteboardElement>): MeetingResult<Unit>
    fun disconnect()
}

enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED, RECONNECTING, FAILED }

sealed interface MeetingResult<out T> {
    data class Success<T>(val value: T) : MeetingResult<T>
    data class Failure(val error: String, val code: String? = null) : MeetingResult<Nothing>
}