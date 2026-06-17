package com.yourapp.habittracker.data.local.dao

import androidx.room.*
import com.yourapp.habittracker.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts WHERE visibility = :visibility ORDER BY createdAt DESC")
    fun getPostsByVisibility(visibility: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Query("UPDATE posts SET reactions = :reactions WHERE id = :postId")
    suspend fun updateReactions(postId: Long, reactions: String)
}