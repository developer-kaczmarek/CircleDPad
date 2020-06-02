# CircleDPad
<a href='https://bintray.com/developer-kaczmarek/CircleDPad/io.github.kaczmarek.circledpad/_latestVersion'><img src='https://api.bintray.com/packages/developer-kaczmarek/CircleDPad/io.github.kaczmarek.circledpad/images/download.svg'></a>
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

CircleDPad is a library which help add virtual DPad for Android Application. 

## Install
First of all, include the dependency in your **app** build.gradle:

`implementation 'io.github.kaczmarek.circledpad:circledpad:$latest_version'`

## Declaring the view
After you have the Library setup, just declare the CircleDPad in your xml (default style):

```
<io.github.kaczmarek.circledpad.CircleDPad
        android:id="@+id/cdp"
        android:layout_width="300dp"
        android:layout_height="300dp" />
```
## Attributes
`dividersWidth` (dimension) - sets the width of the deviders on the DPad

`dividersColor` (color) - sets the color of the deviders on the DPad

`leftButtonColor` (reference) 

`topButtonColor` (reference)

`rightButtonColor` (reference)

`bottomButtonColor` (reference)

`centerButtonColor` (reference) - drawable resource for left button

`leftButtonDrawable` (integer) - drawable resource for left button

`topButtonDrawable` (integer) - drawable resource for top button

`rightButtonDrawable` (integer) - drawable resource for right button

`bottomButtonDrawable` (integer) - drawable resource for bottom button

`centerButtonPercentage` (float)

*Warning: you can leave any of these attributes unspecified if you don't want this functionality.*        

## Events
This library will trigger a click event. You should set an OnClickCircleDPadListener:

```Kotlin
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
```

## Example
The example is on `app` module

## Contribute :sparkles:
This library is still in its very early stage, so feel free to contribute. I will review any Pull Request.

## Bugs / Questions / Suggestions
📧 [Write and I will correct / answer / add as soon as possible](mailto:developer.kaczmarek@yandex.ru)
