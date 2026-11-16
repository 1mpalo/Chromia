package com.impalo.features.projects

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DatastoreProjectsRepositoryImpl(context : Context) : ProjectsRepository {

    private val dataStore = context.applicationContext.projectsDataStore
    private val projectsKey = stringPreferencesKey("recent_projects")

    override val savedProjects: Flow<List<SavedProject>> = dataStore.data.map { preferences ->
        readProjects(preferences).sortedByDescending { it.updatedAtMs }
    }

    override suspend fun saveProject(project: SavedProject) {
        dataStore.edit { preferences ->
            val current = readProjects(preferences)
            val updated = current.filterNot { it.id == project.id } + project

            preferences[projectsKey] = Json.encodeToString(updated)
        }
    }

    override suspend fun getProject(id: String): SavedProject? {
        return readProjects(dataStore.data.first()).find { it.id == id }
    }

    private fun readProjects(preferences: Preferences): List<SavedProject> {
        val stored = preferences[projectsKey] ?: return emptyList()
        return Json.decodeFromString<List<SavedProject>>(stored)
    }
}

private val Context.projectsDataStore by preferencesDataStore(name = "chromia_projects")