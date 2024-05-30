package com.dicoding.storyapp.ui.landing.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.dicoding.storyapp.R
import com.dicoding.storyapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginFragmentTest{

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
        val scenario = launchFragmentInContainer<LoginFragment>()
        onView(withId(R.id.ed_login_email)).perform(click())
        onView(withId(R.id.ed_login_email)).perform(typeText("juanjonathan67"))
        onView(withId(R.id.ed_login_password)).perform(click())
        onView(withId(R.id.ed_login_password)).perform(typeText("123Juan123;'"))
        onView(withId(R.id.btLogin)).perform(click())
    }
}