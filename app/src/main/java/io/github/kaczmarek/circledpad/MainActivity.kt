package io.github.kaczmarek.circledpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.github.kaczmarek.circledpad.CircleDPad.Companion.BOTTOM_BUTTON
import io.github.kaczmarek.circledpad.CircleDPad.Companion.CENTER_BUTTON
import io.github.kaczmarek.circledpad.CircleDPad.Companion.LEFT_BUTTON
import io.github.kaczmarek.circledpad.CircleDPad.Companion.RIGHT_BUTTON
import io.github.kaczmarek.circledpad.CircleDPad.Companion.TOP_BUTTON
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CircleDPad.OnClickCircleDPadListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cdp.apply {
            listener = this@MainActivity
        }
    }

    override fun onClickButton(button: Int) {
        when (button) {
            LEFT_BUTTON -> Toast.makeText(this, "LEFT_BUTTON", Toast.LENGTH_SHORT).show()
            TOP_BUTTON -> Toast.makeText(this, "TOP_BUTTON", Toast.LENGTH_SHORT).show()
            RIGHT_BUTTON -> Toast.makeText(this, "RIGHT_BUTTON", Toast.LENGTH_SHORT).show()
            BOTTOM_BUTTON -> Toast.makeText(this, "BOTTOM_BUTTON", Toast.LENGTH_SHORT).show()
            CENTER_BUTTON -> Toast.makeText(this, "CENTER_BUTTON", Toast.LENGTH_SHORT).show()
        }
    }
}