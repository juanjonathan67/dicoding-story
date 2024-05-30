package com.dicoding.storyapp.ui.landing

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LandingActivityTest{

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLogin() {
        val scenario = launchFragmentInContainer<>()
    }
}