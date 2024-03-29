package com.saxomoose.frontend

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.saxomoose.frontend.ui.home.catalogue.CatalogueFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionFragmentInstrumentedTest {

    @Test
    fun addItemToTransaction() {

        val fragmentArgs = bundleOf("eventId" to 1)
        val catalogueScenario = launchFragmentInContainer<CatalogueFragment>(fragmentArgs)

        catalogueScenario.onFragment { fragment ->
            onView(withId(R.id.button)).perform(click())

        }

        // Verify that performing a click changes the NavController’s state
//        onView(ViewMatchers.withId(R.id.play_btn)).perform(ViewActions.click())
//        assertThat(navController.currentDestination?.id).isEqualTo(R.id.in_game)


    }
}