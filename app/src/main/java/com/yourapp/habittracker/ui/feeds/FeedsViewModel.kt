package com.yourapp.habittracker.ui.feeds

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.habittracker.HabitApplication
import com.yourapp.habittracker.data.local.entity.PostEntity
import com.yourapp.habittracker.data.local.repository.FeedRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FeedsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as HabitApplication).database
    private val repository = FeedRepository(database.postDao())

    // Tab hiện tại
    private val _currentTab = MutableStateFlow("public")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Posts - tự động cập nhật khi đổi tab
    val posts: StateFlow<List<PostEntity>> = _currentTab
        .flatMapLatest { tab -> repository.getPostsByVisibility(tab) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun addReaction(postId: Long, reaction: String) {
        viewModelScope.launch {
            repository.addReaction(postId, reaction)
        }
    }
}