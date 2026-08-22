package com.vastavik.computer.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.vastavik.computer.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository @javax.inject.Inject constructor() {
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun getAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String) = auth.signInWithEmailAndPassword(email, password).await()

    suspend fun signUpWithEmail(email: String, password: String) = auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun signInWithGoogle(idToken: String) = auth.signInWithCredential(
        GoogleAuthProvider.getCredential(idToken, null)
    ).await()

    fun signOut() {
        auth.signOut()
        GoogleSignIn.getClient(
            com.vastavik.computer.VastavikApplication.instance,
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) = auth.sendPasswordResetEmail(email).await()

    suspend fun updateProfile(displayName: String? = null, photoUrl: String? = null) {
        val user = auth.currentUser ?: return
        val request = UserProfileChangeRequest.Builder()
        displayName?.let { request.setDisplayName(it) }
        photoUrl?.let { request.setPhotoUri(android.net.Uri.parse(it)) }
        user.updateProfile(request.build()).await()
    }

    suspend fun getIdToken(forceRefresh: Boolean = false): String? = auth.currentUser?.getIdToken(forceRefresh)?.await()?.token

    fun isEmailVerified(): Boolean = auth.currentUser?.isEmailVerified ?: false

    suspend fun sendEmailVerification() { auth.currentUser?.sendEmailVerification()?.await() }
}
