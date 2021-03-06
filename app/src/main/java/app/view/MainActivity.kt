package app.view

import android.os.Bundle
import app.R
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.content_view, MainFragment(), null)
                    .commitAllowingStateLoss()
        }

    }
}
