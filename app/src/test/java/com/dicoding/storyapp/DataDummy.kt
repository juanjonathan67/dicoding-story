package com.dicoding.storyapp

import com.dicoding.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
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
        return items
    }
}