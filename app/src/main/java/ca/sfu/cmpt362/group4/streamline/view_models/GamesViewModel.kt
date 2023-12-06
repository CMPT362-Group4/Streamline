package ca.sfu.cmpt362.group4.streamline.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.sfu.cmpt362.group4.streamline.data_models.SpecialGames
import ca.sfu.cmpt362.group4.streamline.repositories.SteamRepository
import kotlinx.coroutines.launch

class GamesViewModel(private val repository: SteamRepository): ViewModel() {
    val games = MutableLiveData<List<SpecialGames>?>()

    fun fetchGames() {
        viewModelScope.launch {
            val gameList = repository.getSpecialGames()
            games.postValue(gameList)
        }
    }
}

class GamesViewModelFactory(private val repository: SteamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GamesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GamesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}