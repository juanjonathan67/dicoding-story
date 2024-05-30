package com.dicoding.storyapp.ui.main.addStory

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.R
import com.dicoding.storyapp.util.EspressoIdlingResource
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryFragmentTest{

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        launchFragmentInContainer<AddStoryFragment>(themeResId = R.style.Theme_StoryApp)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addStoryTest() {
        onView(withId(R.id.rbImageUrl)).perform(scrollTo(), click())

        onView(allOf(
            isDescendantOfA(withId(R.id.tfImageUrl)),
            withClassName(endsWith("EditText"))
        )).perform(scrollTo(), replaceText("https://t3.ftcdn.net/jpg/00/92/53/56/360_F_92535664_IvFsQeHjBzfE6sD4VHdO8u5OHUSc6yHF.jpg"))

        onView(allOf(
            isDescendantOfA(withId(R.id.tfStoryDescription)),
            withClassName(endsWith("EditText"))
        )).perform(scrollTo(), replaceText("Espresso testing"))

        onView(withId(R.id.button_add)).perform(scrollTo(), click())
    }
}