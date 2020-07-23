package com.ydhnwb.paperlessapp.ui.login

import android.content.Context
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.register.RegisterActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class RegisterActivityTest {
    @JvmField
    @Rule
    var mActivityRule: ActivityTestRule<RegisterActivity> = ActivityTestRule(RegisterActivity::class.java)

    private fun setTextToView(text: String, textInputEditText: TextInputEditText){
        mActivityRule.runOnUiThread {
            textInputEditText.setText(text)
        }
    }



    @Test
    fun testWithInvalidName(){
        val invalidName = "test"
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_name)!!
        setTextToView(invalidName, etName)
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_name))
            .check(ViewAssertions.matches(
                hasTextInputLayoutErrorText("Nama setidaknya lima karakter")))
    }

    @Test
    fun testWithValidName(){
        val validName = "Prieyudha Akadita S"
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_name)!!
        setTextToView(validName, etName)
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_name))
            .check(ViewAssertions.matches(hasNoErrorText()))
    }


    @Test
    fun testWithInvalidEmail(){
        val validName = "Prieyudha Akadita S"
        val wrongEmail = "invalidEmail"
        val etEmail = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_email)!!
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_name)!!
        setTextToView(wrongEmail, etEmail)
        setTextToView(validName, etName)
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_email))
            .check(ViewAssertions.matches(
                hasTextInputLayoutErrorText("Email tidak valid")))
    }
    @Test
    fun testWithValidEmail(){
        val validName = "Prieyudha Akadita S"
        val validEmail = "akaditasustono@gmail.com"
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_name)!!
        val etEmail = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_email)!!
        setTextToView(validEmail, etEmail)
        setTextToView(validName, etName)
        Espresso.onView(ViewMatchers.withId(R.id.btn_register))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_name))
            .check(ViewAssertions.matches(hasNoErrorText()))
        Espresso.onView(ViewMatchers.withId(R.id.in_email))
            .check(ViewAssertions.matches(hasNoErrorText()))
    }



    @Test
    fun testWithInvalidPassword(){
        val validName = "Prieyudha Akadita S"
        val validEmail = "akaditasustono@gmail.com"
        val wrongPassword  = "1"
        val etName = mActivityRule.activity.findViewById<TextInputEditText>(R.id.et_name)!!
        val etEmail = mActivityRule.activity.findViewById<TextInputEditText>(R.id.et_email)!!
        val etPassword = mActivityRule.activity.findViewById<TextInputEditText>(R.id.et_password)!!
        setTextToView(validName, etName)
        setTextToView(validEmail, etEmail)
        setTextToView(wrongPassword, etPassword)
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_name)).check(ViewAssertions.matches(hasNoErrorText()))
        Espresso.onView(ViewMatchers.withId(R.id.in_email)).check(ViewAssertions.matches(hasNoErrorText()))
        Espresso.onView(ViewMatchers.withId(R.id.in_password))
            .check(ViewAssertions.matches(hasTextInputLayoutErrorText("Password tidak valid")))
    }

    @Test
    fun testValidPassword(){
        val validName = "Prieyudha Akadita S"
        val validEmail = "akaditasustono@gmail.com"
        val wrongPassword  = "12345678"
        val etName = mActivityRule.activity.findViewById<TextInputEditText>(R.id.et_name)!!
        val etEmail = mActivityRule.activity.findViewById<TextInputEditText>(R.id.et_email)!!
        val etPassword = mActivityRule.activity.findViewById<TextInputEditText>(R.id.et_password)!!
        setTextToView(validName, etName)
        setTextToView(validEmail, etEmail)
        setTextToView(wrongPassword, etPassword)
        Espresso.onView(ViewMatchers.withId(R.id.btn_register)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_name)).check(ViewAssertions.matches(hasNoErrorText()))
        Espresso.onView(ViewMatchers.withId(R.id.in_email)).check(ViewAssertions.matches(hasNoErrorText()))
        Espresso.onView(ViewMatchers.withId(R.id.in_password)).check(ViewAssertions.matches(hasNoErrorText()))
    }

    private fun hasTextInputLayoutErrorText(expectedErrorText: String): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(item: View?): Boolean {
                if (item !is TextInputLayout) {
                    return false
                }
                val error = item.error ?: return false
                val hint = error.toString()
                return expectedErrorText == hint
            }

            override fun describeTo(description: Description?) {}
        }
    }

    private fun hasNoErrorText(): Matcher<View?>? {
        return object : BoundedMatcher<View?, TextInputLayout>(TextInputLayout::class.java) {
            override fun describeTo(description: Description) {}
            override fun matchesSafely(view: TextInputLayout): Boolean {
                return view.error == null
            }
        }
    }

}