package ru.netology.nmedia.dao.postDao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.AttachmentEmbeddable
import ru.netology.nmedia.entity.CoordinatesEmbeddable
import ru.netology.nmedia.entity.PostEntity

interface PostDao<T: PostEntity> {
    fun pagingSource(): PagingSource<Int, T>
    suspend fun getLatest(count:Int): List<AllPostEntity>

    suspend fun isEmpty(): Boolean
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: T)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listPost: List<T>)
    @Update
    suspend fun update(post:T)
    @Delete
    suspend fun remove(post: T)

    suspend fun removeAll()
}

suspend  inline  fun <reified T : PostEntity> PostDao<T>.insert(post: Post) {
    this.insert(
        post.run {
            T::class.constructors.iterator().next().call(id, authorId, author, authorAvatar, authorJob, content, published, CoordinatesEmbeddable.fromDto(coords), link, likeOwnerIds, mentionIds, mentionedMe, likedByMe, AttachmentEmbeddable.fromDto(attachment), ownedByMe, users)
        }
    )
}