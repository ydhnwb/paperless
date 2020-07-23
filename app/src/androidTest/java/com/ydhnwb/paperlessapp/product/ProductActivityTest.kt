package com.ydhnwb.paperlessapp.product

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.ydhnwb.paperlessapp.R
import com.ydhnwb.paperlessapp.ui.product.ProductActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class ProductActivityTest : KoinTest {
    @JvmField
    @Rule
    var mActivityRule: ActivityTestRule<ProductActivity> = ActivityTestRule(ProductActivity::class.java)

    private fun setTextToView(text: String, textInputEditText: TextInputEditText){
        mActivityRule.runOnUiThread {
            textInputEditText.setText(text)
        }
    }


    @Test
    fun testWithEmptyName(){
        val invalidName = ""
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_product_name)!!
        setTextToView(invalidName, etName)
        println(etName.text)
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_product_name))
            .check(
                ViewAssertions.matches(
                hasTextInputLayoutErrorText("Nama produk tidak boleh kosong")))


    }

    @Test
    fun testWithInvalidDescription(){
        val validName = "My product"
        val invalidDesc = ""
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_product_name)!!
        val etDesc = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_prodouct_desc)!!
        setTextToView(validName, etName)
        setTextToView(invalidDesc, etDesc)
        println(etName.text)
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_product_desc))
            .check(
                ViewAssertions.matches(
                    hasTextInputLayoutErrorText("Deskripsi tidak boleh kosong")))

    }

    @Test
    fun testWithInvalidPrice(){
        val validName = "My product"
        val validDesc = "My description"
        val invalidPrice = ""
        val etName = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_product_name)!!
        val etDesc = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_prodouct_desc)!!
        val etPrice = mActivityRule.activity?.findViewById<TextInputEditText>(R.id.et_prodouct_price)!!
        setTextToView(validName, etName)
        setTextToView(validDesc, etDesc)
        setTextToView(invalidPrice, etPrice)
        println(etName.text)
        Thread.sleep(3000)
        Espresso.onView(ViewMatchers.withId(R.id.btn_submit)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.in_product_price))
            .check(
                ViewAssertions.matches(
                    hasTextInputLayoutErrorText("Harga produk tidak boleh kosong atau nol")))
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