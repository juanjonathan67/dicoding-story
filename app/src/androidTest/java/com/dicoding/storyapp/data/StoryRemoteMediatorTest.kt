package com.dicoding.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dicoding.storyapp.data.local.database.StoryDatabase
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.StoriesResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.dicoding.storyapp.data.remote.retrofit.ApiStoryService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class QuoteRemoteMediatorTest {

    private var mockApi: ApiStoryService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}
class FakeApiService : ApiStoryService {
    override suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): ErrorResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(page: Int, size: Int): StoriesResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "content://com.dicoding.storyapp/photo$i",
                "$i$i-$i-${i}T$i:$i$i.${i}Z",
                "Author $i",
                "Description $i",
                i.toDouble(),
                "$i",
                i.toDouble()
            )
            items.add(story)
        }
        return StoriesResponse(
            error = false,
            message = "Success",
            listStory = items.subList((page - 1) * size, (page - 1) * size + size)
        )
    }

    override suspend fun getStoriesWithLocation(location: Int): StoriesResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStoryDetail(id: String): StoryResponse {
        TODO("Not yet implemented")
    }
}
