package com.vastavik.computer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vastavik.computer.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository @javax.inject.Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    // ============ USER ============
    fun createUserProfile(user: UserModel) = db.collection("users").document(user.uid).set(user.toMap())

    suspend fun getUserProfile(uid: String) = db.collection("users").document(uid).get().await()

    fun updateUserProfile(uid: String, data: Map<String, Any>) =
        db.collection("users").document(uid).set(data, com.google.firebase.firestore.SetOptions.merge())

    fun streamUserProfile(uid: String): Flow<UserModel?> = callbackFlow {
        val listener = db.collection("users").document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
            } else if (snapshot != null && snapshot.exists()) {
                trySend(UserModel.fromSnapshot(snapshot))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    // ============ COURSES ============
    fun streamCourses(): Flow<List<CourseModel>> = callbackFlow {
        val listener = db.collection("courses")
            .whereEqualTo("isPublished", true)
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                } else if (snapshot != null) {
                    val courses = snapshot.documents.map { CourseModel.fromSnapshot(it) }
                    trySend(courses)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCourse(courseId: String) = db.collection("courses").document(courseId).get().await()

    // ============ PARTS ============
    fun streamParts(courseId: String): Flow<List<PartModel>> = callbackFlow {
        val listener = db.collection("courses").document(courseId)
            .collection("parts")
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val parts = snapshot.documents.map { PartModel.fromSnapshot(it) }
                    trySend(parts)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ SUBPARTS ============
    fun streamSubparts(courseId: String, partId: String): Flow<List<SubpartModel>> = callbackFlow {
        val listener = db.collection("courses").document(courseId)
            .collection("parts").document(partId)
            .collection("subparts")
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val subparts = snapshot.documents.map { SubpartModel.fromSnapshot(it) }
                    trySend(subparts)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ LESSONS ============
    fun streamLessons(courseId: String, partId: String, subpartId: String): Flow<List<LessonModel>> = callbackFlow {
        val listener = db.collection("courses").document(courseId)
            .collection("parts").document(partId)
            .collection("subparts").document(subpartId)
            .collection("lessons")
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val lessons = snapshot.documents.map { LessonModel.fromSnapshot(it) }
                    trySend(lessons)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ POPULAR TOPICS ============
    fun streamPopularTopics(): Flow<List<PopularTopicModel>> = callbackFlow {
        val listener = db.collection("popularTopics")
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val topics = snapshot.documents.map { PopularTopicModel.fromSnapshot(it) }
                    trySend(topics)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ BANNERS ============
    fun streamBanners(): Flow<List<BannerModel>> = callbackFlow {
        val listener = db.collection("banners")
            .whereEqualTo("isActive", true)
            .orderBy("order")
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val banners = snapshot.documents.map { BannerModel.fromSnapshot(it) }
                    trySend(banners)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ STUDENT SELECTION ============
    fun selectCourse(uid: String, courseId: String, courseName: String) {
        val selection = StudentSelection(
            courseId = courseId,
            courseName = courseName,
            selectedAt = ""
        )
        db.collection("studentSelections").document(uid).set(selection.toMap(), com.google.firebase.firestore.SetOptions.merge())
    }

    suspend fun getStudentSelection(uid: String) = db.collection("studentSelections").document(uid).get().await()

    fun streamStudentSelection(uid: String): Flow<StudentSelection?> = callbackFlow {
        val listener = db.collection("studentSelections").document(uid).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                trySend(null)
            } else {
                trySend(StudentSelection.fromSnapshot(snapshot))
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun markPartVisited(uid: String, courseId: String, partId: String) {
        val entry = "$courseId::$partId"
        val ref = db.collection("studentSelections").document(uid)
        val doc = ref.get().await()
        val data = doc.data
        val visitedRaw = data?.get("visitedParts")
        val visited: List<String> = if (visitedRaw is List<*>) {
            visitedRaw.filterIsInstance<String>()
        } else {
            emptyList()
        }
        if (!visited.contains(entry)) {
            ref.update("visitedParts", com.google.firebase.firestore.FieldValue.arrayUnion(entry)).await()
        }
    }

    suspend fun restartCourse(uid: String, courseId: String) {
        val prefix = "$courseId::"
        val ref = db.collection("studentSelections").document(uid)
        val doc = ref.get().await()
        val data = doc.data
        val visitedRaw = data?.get("visitedParts")
        val visited: List<String> = if (visitedRaw is List<*>) {
            visitedRaw.filterIsInstance<String>()
        } else {
            emptyList()
        }
        val kept = visited.filter { !it.startsWith(prefix) }
        ref.update("visitedParts", kept).await()
    }

    // ============ QUIZZES ============
    fun streamQuizzes(): Flow<List<QuizModel>> = callbackFlow {
        val listener = db.collection("quizzes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val quizzes = snapshot.documents.map { QuizModel.fromSnapshot(it) }
                    trySend(quizzes)
                }
            }
        awaitClose { listener.remove() }
    }

    fun createQuiz(quiz: QuizModel) = db.collection("quizzes").add(quiz.toMap())

    // ============ CODING CHALLENGES ============
    fun streamCodingChallenges(): Flow<List<CodingChallenge>> = callbackFlow {
        val listener = db.collection("codingChallenges")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val challenges = snapshot.documents.map { CodingChallenge.fromSnapshot(it) }
                    trySend(challenges)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ PYQs ============
    fun streamPYQs(): Flow<List<PYQModel>> = callbackFlow {
        val listener = db.collection("pyqs")
            .orderBy("year", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val pyqs = snapshot.documents.map { PYQModel.fromSnapshot(it) }
                    trySend(pyqs)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ TRANSACTIONS ============
    fun addTransaction(transaction: TransactionModel) = db.collection("transactions").document(transaction.id).set(transaction.toMap())

    fun streamTransactions(uid: String): Flow<List<TransactionModel>> = callbackFlow {
        val listener = db.collection("transactions")
            .whereEqualTo("uid", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val transactions = snapshot.documents.map { TransactionModel.fromSnapshot(it) }
                    trySend(transactions)
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ SUBSCRIPTIONS ============
    fun createSubscription(subscription: SubscriptionModel) = db.collection("subscriptions").document(subscription.id).set(subscription.toMap())

    fun updateSubscription(id: String, data: Map<String, Any>) = db.collection("subscriptions").document(id).update(data)

    fun streamUserSubscription(uid: String): Flow<SubscriptionModel?> = callbackFlow {
        val listener = db.collection("subscriptions")
            .whereEqualTo("uid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(null)
                } else {
                    trySend(SubscriptionModel.fromSnapshot(snapshot.documents[0]))
                }
            }
        awaitClose { listener.remove() }
    }

    // ============ NOTES ============
    fun saveNote(note: NoteModel) = db.collection("users").document(note.userId)
        .collection("notes").document(note.id).set(note.toMap())

    fun streamNotes(uid: String): Flow<List<NoteModel>> = callbackFlow {
        val listener = db.collection("users").document(uid)
            .collection("notes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val notes = snapshot.documents.map { NoteModel.fromSnapshot(it) }
                    trySend(notes)
                }
            }
        awaitClose { listener.remove() }
    }

    fun deleteNote(uid: String, noteId: String) = db.collection("users").document(uid).collection("notes").document(noteId).delete()

    // ============ CHAT ============
    fun createChatSession(session: ChatSession) = db.collection("users").document(session.userId)
        .collection("ai_chats").document(session.id).set(session.toMap())

    fun updateChatSession(session: ChatSession) = db.collection("users").document(session.userId)
        .collection("ai_chats").document(session.id).set(session.toMap(), com.google.firebase.firestore.SetOptions.merge())

    fun streamChatSessions(uid: String): Flow<List<ChatSession>> = callbackFlow {
        val listener = db.collection("users").document(uid)
            .collection("ai_chats")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) trySend(emptyList())
                else if (snapshot != null) {
                    val sessions = snapshot.documents.map { doc ->
                        val data = doc.data!!
                        val messagesData = (data["messages"] as? List<Map<String, Any>>) ?: emptyList()
                        val messages = messagesData.map { m ->
                            ChatMessage(
                                id = "",
                                text = m["text"] as? String ?: "",
                                isUser = m["isUser"] as? Boolean ?: true,
                                timestamp = m["timestamp"]?.toString() ?: ""
                            )
                        }
                        ChatSession(
                            id = doc.id,
                            title = data["title"] as? String ?: "",
                            userId = uid,
                            messages = messages,
                            createdAt = data["createdAt"]?.toString() ?: "",
                            updatedAt = data["updatedAt"]?.toString() ?: ""
                        )
                    }
                    trySend(sessions)
                }
            }
        awaitClose { listener.remove() }
    }
}
