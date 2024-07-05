package com.latinka.tinkawork.shared.data.services

import android.net.Uri

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.tasks.await

class FirebaseStorageServiceImpl : FirebaseStorageService {

    private val storage = FirebaseStorage.getInstance()

    override suspend fun uploadImage(fileUri: Uri): Task<Uri> {
        val storageReference = storage.reference.child("user_photos/${System.currentTimeMillis()}.jpg")
        val fileUpload = storageReference.putFile(fileUri).await()
        return fileUpload.storage.downloadUrl
    }
}