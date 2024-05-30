package com.dicoding.storyapp.ui.landing

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.utils.isValidEmail

class CustomTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var startIcon: Drawable
    private var endIcon: Drawable
    private var mode: Int

    init {
        val styleableMode: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextField)
        mode = styleableMode.getInt(R.styleable.CustomTextField_mode, 0)
        startIcon = ContextCompat.getDrawable(context, StartIconDrawables[mode]) as Drawable
        endIcon = ContextCompat.getDrawable(context, EndIconDrawables[mode]) as Drawable

        if(mode == MODE.PASSWORD.ordinal) {
            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        styleableMode.recycle()
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if ((mode == MODE.PASSWORD.ordinal) and (s.toString().length < 8) and (count > 0)) {
                    resources.getString(R.string.password_error)
                } else if ((mode == MODE.EMAIL.ordinal) and !s.isValidEmail() and (count > 0)){
                    resources.getString(R.string.email_error)
                } else {
                    null
                }
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = resources.getString(Hints[mode])
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        setButtonDrawables(startOfTheText = startIcon, endOfTheText = endIcon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
        compoundDrawablePadding = 24
    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isEndIconClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (endIcon.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isEndIconClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - endIcon.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isEndIconClicked = true
                }
            }
            if (isEndIconClicked) {
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        if(mode == (MODE.TEXT.ordinal or MODE.EMAIL.ordinal)) {
                            endIcon = ContextCompat.getDrawable(context, EndIconDrawables[mode]) as Drawable
                            when {
                                text != null -> text?.clear()
                            }
                        } else {
                            if (transformationMethod.javaClass.simpleName.equals("PasswordTransformationMethod")) {
                                transformationMethod = HideReturnsTransformationMethod.getInstance()
                                endIcon = ContextCompat.getDrawable(context, EndIconDrawables[mode]) as Drawable
                            } else {
                                transformationMethod = PasswordTransformationMethod.getInstance()
                                endIcon = ContextCompat.getDrawable(context, EndIconDrawables[mode + 1]) as Drawable
                            }
                            setSelection(length())
                        }
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    companion object {
        enum class MODE {
            TEXT, EMAIL, PASSWORD
        }

        val StartIconDrawables = intArrayOf(
            R.drawable.ic_person_24dp,
            R.drawable.ic_email_24dp,
            R.drawable.ic_password_24dp
        )

        val EndIconDrawables = intArrayOf(
            R.drawable.ic_close_24dp,
            R.drawable.ic_close_24dp,
            R.drawable.ic_eye_24dp,
            R.drawable.ic_eye_slash_24dp
        )

        val Hints = arrayOf(
            R.string.name,
            R.string.email,
            R.string.password
        )
    }
}