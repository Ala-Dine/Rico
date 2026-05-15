package com.univeloued.rico.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import com.univeloued.rico.domain.repository.ReminderRepository
import com.univeloued.rico.domain.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val reminderRepository: ReminderRepository,
    private val familyRepository: FamilyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.length < 2) return@flatMapLatest flowOf(emptyList<SearchResult>())
            
            combine(
                medicalRecordRepository.getMedicalRecords(),
                reminderRepository.getReminders(),
                familyRepository.getFamilyMembers()
            ) { records, reminders, family ->
                val results = mutableListOf<SearchResult>()
                
                records.filter { it.fileName.contains(query, true) || it.recordFor.contains(query, true) }
                    .forEach { results.add(SearchResult.MedicalRecord(it.fileName, it.recordFor)) }
                    
                reminders.filter { it.medicineName.contains(query, true) }
                    .forEach { results.add(SearchResult.Reminder(it.medicineName, it.time)) }
                    
                family.filter { it.name.contains(query, true) }
                    .forEach { results.add(SearchResult.FamilyMember(it.name, it.relationship)) }
                    
                results
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}

sealed class SearchResult(val title: String, val subtitle: String, val type: String) {
    class MedicalRecord(title: String, subtitle: String) : SearchResult(title, subtitle, "Medical Record")
    class Reminder(title: String, subtitle: String) : SearchResult(title, subtitle, "Reminder")
    class FamilyMember(title: String, subtitle: String) : SearchResult(title, subtitle, "Family Member")
}
