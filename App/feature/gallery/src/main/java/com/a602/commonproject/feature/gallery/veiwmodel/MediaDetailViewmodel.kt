package com.a602.commonproject.feature.gallery.veiwmodel

import androidx.lifecycle.ViewModel
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


data class MediaDetailViewmodelUiState(
    val medias:,
    val isLoading: Boolean = false,
    val error: String? = null
)


