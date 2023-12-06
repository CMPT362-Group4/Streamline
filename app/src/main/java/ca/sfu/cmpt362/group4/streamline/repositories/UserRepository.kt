package ca.sfu.cmpt362.group4.streamline.repositories

import ca.sfu.cmpt362.group4.streamline.data_models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class UserRepository(private val firebaseDatabase: FirebaseDatabase) {

    private val usersRef = firebaseDatabase.reference.child("users")


    fun storeUserInFirebase(uid: String, name: String, email: String) {
        val userNode = usersRef.child(uid)
        userNode.setValue(User(uid, name, email))
    }


    fun getValueOfField(uid: String, fieldName: String): String? {
        val userRef = usersRef.child(uid)
        var value = ""
        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                value = dataSnapshot.child("name").value.toString()
            }
        }
        return value
    }


    fun setValueOfField(uid: String, fieldName: String, newValue: String) {
        val userRef = usersRef.child(uid)
        userRef.child(fieldName).setValue(newValue)
    }

    // Query the list of users matching a given field
    fun queryUsersByField(fieldName: String, value: String): List<User> {
        val query = usersRef.orderByChild(fieldName).equalTo(value)

        val users = mutableListOf<User>()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { users.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return users
    }
}