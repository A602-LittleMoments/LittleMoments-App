package com.a602.commonproject.feature.baby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.flatMapLatest


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val sharedMediaRepository: SharedMediaRepository,
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    private val _isError = MutableStateFlow(false)
    private val _selectedBabyIndex = MutableStateFlow(0)
    private val _selectedYear = MutableStateFlow<Int?>(null) // null means All

    // ... (Existing Paging Stream)
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val mediaPagingFlow: kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<SharedMedia>> =
        combine(
            babyRepository.getBabyStream(),
            _selectedBabyIndex,
            _selectedYear
        ) { babies, index, year ->
            val babyId = babies.getOrNull(index)?.babyId
            Pair(babyId, year)
        }.flatMapLatest { (babyId, year) ->
            sharedMediaRepository.getSharedAlbumPagingStream(babyId, year)
        }.cachedIn(viewModelScope)

    fun selectYear(yearOffset: Int) {
        if (yearOffset == 0) {
            _selectedYear.value = null
        } else {
            val currentYear = java.time.LocalDate.now().year
            _selectedYear.value = currentYear - yearOffset
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        babyRepository.getBabyStream(),
        _collections,
        _isError,
        _selectedBabyIndex
    ) { babies, collections, isError, selectedIndex ->
        if (isError) {
            HomeUiState.Error("데이터를 불러오지 못했습니다. 네트워크를 확인해주세요.")
        } else {
            HomeUiState.Success(
                babies = babies,
                selectedBabyIndex = selectedIndex,
                collections = collections,
            )
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )

    fun updateSelectedBaby(index: Int) {
        _selectedBabyIndex.value = index
    }

    init {
        fetchCollections()
    }
    
    fun logout(){
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    private fun fetchCollections() {
        viewModelScope.launch {
            collectionRepository.getCollections()
                .onSuccess {
                    _isError.value = false
                    _collections.value = it
                }
                .onFailure {
                    // _isError.value = true
                }
        }
    }

    // [New] Baby CRUD Operations
    fun addBaby(name: String, birthDate: String, gender: String, imageUri: android.net.Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            val imageFile = imageUri?.let { uriToFile(it) }
            val formattedBirthDate = formatDate(birthDate)
            val genderEnum = if (gender == "MALE") Baby.Gender.MALE else Baby.Gender.FEMALE
            
            babyRepository.addBaby(name, formattedBirthDate, genderEnum, imageFile)
                .onSuccess { onComplete() }
                .onFailure { 
                    // TODO: Handle error
                    onComplete() // Proceed for now or show error
                }
        }
    }

    fun updateBaby(babyId: String, name: String, birthDate: String, gender: String, imageUri: android.net.Uri?, onComplete: () -> Unit) {
        viewModelScope.launch {
            val imageFile = imageUri?.let { uriToFile(it) }
            val formattedBirthDate = formatDate(birthDate)
            val genderEnum = if (gender == "MALE") Baby.Gender.MALE else Baby.Gender.FEMALE

            babyRepository.updateBaby(babyId, name, formattedBirthDate, genderEnum, imageFile)
                .onSuccess { onComplete() }
                .onFailure {
                     // TODO: Handle error
                     onComplete()
                }
        }
    }

    fun deleteBaby(babyId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val groupId = userRepository.getCurrentGroupId()
            if (groupId != null) {
                babyRepository.deleteBaby(groupId, babyId)
                    .onSuccess { onComplete() }
            } else {
                // Error: No Group ID
                onComplete()
            }
        }
    }

    private fun formatDate(input: String): String {
        // Input: "YYYYMMDD" -> Output: "YYYY-MM-DD"
        if (input.length == 8) {
            return "${input.substring(0, 4)}-${input.substring(4, 6)}-${input.substring(6, 8)}"
        }
        return input // Fallback
    }

    private fun uriToFile(uri: android.net.Uri): java.io.File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = java.io.File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = java.io.FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val babies: List<Baby>,
        val selectedBabyIndex: Int = 0,
        val collections: List<Collection> = emptyList(),
        val hasNotifications: Boolean = false
    ) : HomeUiState {
        val currentBaby: Baby?
            get() = babies.getOrNull(selectedBabyIndex)
    }
    data class Error(
        val errorMessage : String?
    ) : HomeUiState
}
