package com.latinka.tinkawork.shared.data.services

import android.net.Uri

import com.google.android.gms.tasks.Task

interface FirebaseStorageService {

    suspend fun uploadImage(fileUri: Uri): Task<Uri>
}