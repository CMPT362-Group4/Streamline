package ca.sfu.cmpt362.group4.streamline.view_models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.sfu.cmpt362.group4.streamline.data_models.User
import ca.sfu.cmpt362.group4.streamline.repositories.UserRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _searchLiveData = MutableLiveData<List<User>>()
    val searchLiveData: LiveData<List<User>> get() = _searchLiveData


    fun storeUser(uid: String, name: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.storeUserInFirebase(uid, name, email)
        }
    }

    suspend fun getValueOfField(uid: String, fieldName: String): String? {
        val result = viewModelScope.async(Dispatchers.IO) {
            userRepository.getValueOfField(uid, fieldName)

        }
        return result.await()
    }

    fun setValueOfField(uid: String, fieldName: String, newValue: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.setValueOfField(uid, fieldName, newValue)
        }
    }

    fun queryUsersByEmail(email: String) {
        viewModelScope.launch {
            // Perform the database query in the background thread
            val users = userRepository.queryUsersByField("email", email)
            _searchLiveData.value = users
        }
    }
}

class UserViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val repository = UserRepository(FirebaseDatabase.getInstance())
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}