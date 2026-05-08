package com.univeloued.rico.data.repository

import com.univeloued.rico.data.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor() : FamilyRepository {
    private val _familyMembers = MutableStateFlow<List<FamilyMember>>(emptyList())
    override val familyMembers: StateFlow<List<FamilyMember>> = _familyMembers.asStateFlow()

    override fun addFamilyMember(member: FamilyMember) {
        _familyMembers.value += member.copy(id = UUID.randomUUID().toString())
    }
}
