package com.remote.ghibli.usecases.models

import com.remote.ghibli.repository.models.FilterType

data class FilterUiModel(
    val id: String,
    val name: String,
    val isSelected: Boolean,
    val type: FilterType,
)
