package com.example.hotelbookingapp1

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

data class User(
    val name: String,
    val email: String,
    val password: String,
    val isLoggedIn: Boolean = false
)

class MultiUserPreferencesManager(private val context: Context) {

    private val fileName = "users_data.json"

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    private fun userToJson(user: User): JSONObject {
        return JSONObject().apply {
            put("name", user.name)
            put("email", user.email)
            put("password", user.password)
            put("isLoggedIn", user.isLoggedIn)
        }
    }

    private fun jsonToUser(jsonObject: JSONObject): User {
        return User(
            name = jsonObject.getString("name"),
            email = jsonObject.getString("email"),
            password = jsonObject.getString("password"),
            isLoggedIn = jsonObject.optBoolean("isLoggedIn", false)
        )
    }

    private suspend fun readUsersData(): Pair<MutableList<User>, String?> {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile()
                if (!file.exists()) {
                    return@withContext Pair(mutableListOf(), null)
                }

                val jsonString = file.readText()
                if (jsonString.isEmpty()) {
                    return@withContext Pair(mutableListOf(), null)
                }

                val jsonObject = JSONObject(jsonString)
                val usersArray = jsonObject.optJSONArray("users") ?: JSONArray()
                val currentLoggedInUser = jsonObject.optString("currentLoggedInUser", null)
                    .takeIf { it.isNotEmpty() }

                val usersList = mutableListOf<User>()
                for (i in 0 until usersArray.length()) {
                    val userJson = usersArray.getJSONObject(i)
                    usersList.add(jsonToUser(userJson))
                }

                Pair(usersList, currentLoggedInUser)
            } catch (e: Exception) {
                Log.e("PreferencesManager", "Error reading user data", e)
                Pair(mutableListOf(), null)
            }
        }
    }

    private suspend fun writeUsersData(users: List<User>, currentLoggedInUser: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile()
                val jsonObject = JSONObject()
                val usersArray = JSONArray()

                users.forEach { user ->
                    usersArray.put(userToJson(user))
                }

                jsonObject.put("users", usersArray)
                if (currentLoggedInUser != null) {
                    jsonObject.put("currentLoggedInUser", currentLoggedInUser)
                } else {
                    jsonObject.put("currentLoggedInUser", JSONObject.NULL)
                }

                file.writeText(jsonObject.toString(2))
                true
            } catch (e: IOException) {
                Log.e("PreferencesManager", "IO Error writing user data", e)
                false
            } catch (e: JSONException) {
                Log.e("PreferencesManager", "JSON Error writing user data", e)
                false
            }
        }
    }

    suspend fun isAnyUserLoggedIn(): Boolean {
        val (users, currentLoggedInUser) = readUsersData()
        return currentLoggedInUser != null && users.any { it.email == currentLoggedInUser && it.isLoggedIn }
    }

    suspend fun getCurrentUser(): User? {
        val (users, currentLoggedInUser) = readUsersData()
        return currentLoggedInUser?.let { email ->
            users.find { it.email == email }
        }
    }

    suspend fun getCurrentUserName(): String {
        return getCurrentUser()?.name ?: ""
    }

    suspend fun getCurrentUserEmail(): String {
        return getCurrentUser()?.email ?: ""
    }

    suspend fun saveUserData(name: String, email: String, password: String): Boolean {
        return try {
            val (users, _) = readUsersData()

            if (users.any { it.email == email }) {
                return false
            }

            val newUser = User(name, email, password, true)
            users.add(newUser)

            writeUsersData(users, email)
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error saving user data", e)
            false
        }
    }

    suspend fun validateLogin(email: String, password: String): Boolean {
        return try {
            val (users, _) = readUsersData()
            val user = users.find { it.email == email }

            if (user != null && user.password == password) {
                val updatedUsers = users.map {
                    if (it.email == email) it.copy(isLoggedIn = true) else it
                }
                writeUsersData(updatedUsers, email)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error validating login", e)
            false
        }
    }

    suspend fun userExists(email: String): Boolean {
        return try {
            val (users, _) = readUsersData()
            users.any { it.email == email }
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error checking user exists", e)
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val (users, currentLoggedInUser) = readUsersData()

            if (currentLoggedInUser != null) {
                val updatedUsers = users.map { user ->
                    if (user.isLoggedIn) user.copy(isLoggedIn = false) else user
                }
                writeUsersData(updatedUsers, null)
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error during logout", e)
            false
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val (users, _) = readUsersData()
            users.toList()
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error getting all users", e)
            emptyList()
        }
    }

    suspend fun switchUser(email: String): Boolean {
        return try {
            val (users, _) = readUsersData()
            val user = users.find { it.email == email }

            if (user != null) {
                val updatedUsers = users.map { it.copy(isLoggedIn = false) }
                val finalUsers = updatedUsers.map {
                    if (it.email == email) it.copy(isLoggedIn = true) else it
                }
                writeUsersData(finalUsers, email)
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error switching user", e)
            false
        }
    }

    suspend fun updateUser(email: String, newName: String? = null, newPassword: String? = null): Boolean {
        return try {
            val (users, currentLoggedInUser) = readUsersData()
            val userIndex = users.indexOfFirst { it.email == email }

            if (userIndex != -1) {
                val currentUser = users[userIndex]
                val updatedUser = currentUser.copy(
                    name = newName ?: currentUser.name,
                    password = newPassword ?: currentUser.password
                )
                users[userIndex] = updatedUser
                writeUsersData(users, currentLoggedInUser)
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Error updating user", e)
            false
        }
    }
}