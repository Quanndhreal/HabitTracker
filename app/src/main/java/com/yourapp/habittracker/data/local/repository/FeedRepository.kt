package com.yourapp.habittracker.data.local.repository  // ← SỬA PACKAGE (bỏ "local")

import com.yourapp.habittracker.data.local.dao.PostDao
import com.yourapp.habittracker.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject

class FeedRepository(private val postDao: PostDao) {

    fun getPostsByVisibility(visibility: String): Flow<List<PostEntity>> {
        return postDao.getPostsByVisibility(visibility)
    }

    fun getAllPosts(): Flow<List<PostEntity>> {
        return postDao.getAllPosts()
    }

    suspend fun createPost(post: PostEntity): Long {
        return postDao.insertPost(post)
    }

    suspend fun addReaction(postId: Long, reactionKey: String) {
        // Lấy TẤT CẢ posts dưới dạng Flow, rồi lấy phần tử đầu tiên
        val postsFlow = postDao.getAllPosts()
        val posts = postsFlow.firstOrNull()  // ← SỬA CÁCH GỌI: postsFlow.firstOrNull()

        // Tìm post và cập nhật reaction
        posts?.find { it.id == postId }?.let { post ->
            val reactions = try {
                JSONObject(post.reactions)
            } catch (e: Exception) {
                JSONObject()
            }
            val currentCount = reactions.optInt(reactionKey, 0)
            reactions.put(reactionKey, currentCount + 1)
            postDao.updateReactions(postId, reactions.toString())
        }
    }

    suspend fun shareHabitProgress(
        habitName: String,
        streakDay: Int,
        description: String,
        imageUrl: String?
    ) {
        val post = PostEntity(
            username = "You",
            countryEmoji = "🇻🇳",
            habitName = habitName,
            streakDay = streakDay,
            totalDays = 66,
            description = description,
            imageUrl = imageUrl,
            reactions = """{"👏":0,"🔥":0,"💪":0,"❤️":0}""",
            visibility = "public",
            timeAgo = "now"
        )
        postDao.insertPost(post)
    }
}