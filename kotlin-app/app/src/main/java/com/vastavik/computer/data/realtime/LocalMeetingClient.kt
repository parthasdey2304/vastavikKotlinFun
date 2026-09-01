package com.vastavik.computer.data.realtime

import com.vastavik.computer.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocalMeetingClient : MeetingClient {
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _participants = MutableStateFlow<Map<String, Participant>>(emptyMap())
    override val participants: StateFlow<Map<String, Participant>> = _participants

    private val _chatMessages = MutableStateFlow<List<LiveChatMessage>>(emptyList())
    override val chatMessages: StateFlow<List<LiveChatMessage>> = _chatMessages

    private val _whiteboardState = MutableStateFlow(WhiteboardState())
    override val whiteboardState: StateFlow<WhiteboardState> = _whiteboardState

    private val _currentSession = MutableStateFlow<ClassSession?>(null)
    override val currentSession: StateFlow<ClassSession?> = _currentSession

    private val _localMicEnabled = MutableStateFlow(false)
    override val localMicEnabled: StateFlow<Boolean> = _localMicEnabled

    private val _localCameraEnabled = MutableStateFlow(false)
    override val localCameraEnabled: StateFlow<Boolean> = _localCameraEnabled

    private val _localHandRaised = MutableStateFlow(false)
    override val localHandRaised: StateFlow<Boolean> = _localHandRaised

    private val _localScreenSharing = MutableStateFlow(false)
    override val localScreenSharing: StateFlow<Boolean> = _localScreenSharing

    private var currentUserId: String? = null
    private var currentClassId: String? = null
    private var msgCounter = 0L

    override val currentUser: Participant?
        get() = currentUserId?.let { _participants.value[it] }

    override suspend fun joinClass(classId: String, userId: String, displayName: String): MeetingResult<ClassSession> {
        currentUserId = userId
        currentClassId = classId
        _connectionState.value = ConnectionState.CONNECTING
        val isAdmin = userId.startsWith("admin")
        val role = if (isAdmin) ParticipantRole.ADMIN else ParticipantRole.STUDENT
        val participant = Participant(userId = userId, displayName = displayName, role = role, micState = MediaState.OFF, cameraState = MediaState.OFF)
        val session = ClassSession(classId = classId, topic = "Live Class: $classId", adminId = "admin_$classId", participants = mapOf(userId to participant))
        _participants.value = session.participants
        _currentSession.value = session
        _connectionState.value = ConnectionState.CONNECTED
        _localMicEnabled.value = false
        _localCameraEnabled.value = false
        return MeetingResult.Success(session)
    }

    override suspend fun leaveClass(): MeetingResult<Unit> {
        _connectionState.value = ConnectionState.DISCONNECTED
        currentUserId = null
        currentClassId = null
        return MeetingResult.Success(Unit)
    }

    override suspend fun toggleMic(enabled: Boolean): MeetingResult<Unit> {
        updateLocal { it.copy(micState = if (enabled) MediaState.ON else MediaState.OFF) }
        _localMicEnabled.value = enabled
        return MeetingResult.Success(Unit)
    }

    override suspend fun toggleCamera(enabled: Boolean): MeetingResult<Unit> {
        updateLocal { it.copy(cameraState = if (enabled) MediaState.ON else MediaState.OFF) }
        _localCameraEnabled.value = enabled
        return MeetingResult.Success(Unit)
    }

    override suspend fun toggleHandRaise(raised: Boolean): MeetingResult<Unit> {
        updateLocal { it.copy(handRaised = raised) }
        _localHandRaised.value = raised
        return MeetingResult.Success(Unit)
    }

    override suspend fun toggleScreenShare(active: Boolean): MeetingResult<Unit> {
        updateLocal { it.copy(isScreenSharing = active) }
        _localScreenSharing.value = active
        return MeetingResult.Success(Unit)
    }

    override suspend fun requestScreenShare(): MeetingResult<Unit> = MeetingResult.Success(Unit)

    override suspend fun grantScreenShare(targetUserId: String): MeetingResult<Unit> {
        updateParticipant(targetUserId) { it.copy(hasScreenSharePermission = true) }
        return MeetingResult.Success(Unit)
    }

    override suspend fun revokeScreenShare(targetUserId: String): MeetingResult<Unit> {
        updateParticipant(targetUserId) { it.copy(hasScreenSharePermission = false, isScreenSharing = false) }
        return MeetingResult.Success(Unit)
    }

    override suspend fun sendChatMessage(text: String, replyTo: ReplyPreview?): MeetingResult<Unit> {
        val sender = currentUser ?: return MeetingResult.Failure("Not joined")
        val message = LiveChatMessage(id = "msg_${++msgCounter}", senderId = sender.userId, senderName = sender.displayName, senderRole = sender.role, text = text, replyTo = replyTo)
        _chatMessages.value = _chatMessages.value + message
        return MeetingResult.Success(Unit)
    }

    override suspend fun sendEmojiReaction(emoji: String): MeetingResult<Unit> = MeetingResult.Success(Unit)

    override suspend fun kickParticipant(targetUserId: String): MeetingResult<Unit> {
        updateParticipant(targetUserId) { it.copy(leftAt = System.currentTimeMillis()) }
        return MeetingResult.Success(Unit)
    }

    override suspend fun assignStarCast(targetUserId: String): MeetingResult<Unit> {
        _participants.value.values.firstOrNull { it.role == ParticipantRole.STARCAST }?.let { existing ->
            updateParticipant(existing.userId) { it.copy(role = ParticipantRole.STUDENT) }
        }
        updateParticipant(targetUserId) { it.copy(role = ParticipantRole.STARCAST) }
        return MeetingResult.Success(Unit)
    }

    override suspend fun revokeStarCast(targetUserId: String): MeetingResult<Unit> {
        updateParticipant(targetUserId) { it.copy(role = ParticipantRole.STUDENT) }
        return MeetingResult.Success(Unit)
    }

    override suspend fun toggleFeature(feature: DisabledFeature, enabled: Boolean): MeetingResult<Unit> {
        _currentSession.value?.let { session ->
            val updated = if (enabled) session.copy(disabledFeatures = session.disabledFeatures + feature) else session.copy(disabledFeatures = session.disabledFeatures - feature)
            _currentSession.value = updated
        }
        return MeetingResult.Success(Unit)
    }

    override suspend fun startRecording(): MeetingResult<Unit> {
        _currentSession.value = _currentSession.value?.copy(recording = true)
        return MeetingResult.Success(Unit)
    }

    override suspend fun stopRecording(): MeetingResult<Unit> {
        _currentSession.value = _currentSession.value?.copy(recording = false)
        return MeetingResult.Success(Unit)
    }

    override suspend fun updateWhiteboard(elements: List<WhiteboardElement>): MeetingResult<Unit> {
        _whiteboardState.value = WhiteboardState(elements = elements)
        return MeetingResult.Success(Unit)
    }

    override fun disconnect() {
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    private fun updateLocal(update: (Participant) -> Participant) {
        currentUserId?.let { uid -> _participants.value[uid]?.let { cur -> _participants.value = _participants.value + (uid to update(cur)) } }
    }

    private fun updateParticipant(userId: String, update: (Participant) -> Participant) {
        _participants.value[userId]?.let { cur -> _participants.value = _participants.value + (userId to update(cur)) }
    }
}