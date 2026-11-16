package com.impalo.features.projects

import kotlinx.coroutines.flow.Flow

interface ProjectsRepository {
    val savedProjects: Flow<List<SavedProject>>

    suspend fun getProject(id: String): SavedProject?
    suspend fun saveProject(project: SavedProject)
}