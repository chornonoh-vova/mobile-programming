package lab3.camera2you

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lab3.camera2you.fragments.PhotoFragment
import lab3.camera2you.fragments.VideoFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentByTag(PhotoFragment::class.java.name) != null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    supportFragmentManager.findFragmentByTag(PhotoFragment::class.java.name)!!,
                    PhotoFragment::class.java.name
                )
                .commit()

        } else if (supportFragmentManager.findFragmentByTag(VideoFragment::class.java.name) != null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    supportFragmentManager.findFragmentByTag(VideoFragment::class.java.name)!!,
                    VideoFragment::class.java.name
                )
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PhotoFragment(), PhotoFragment::class.java.name)
                .commit()
        }
    }

    fun showVideoFragment() {
        supportFragmentManager.beginTransaction()
            .remove(supportFragmentManager.findFragmentByTag(PhotoFragment::class.java.name)!!)
            .commitNow()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, VideoFragment(), VideoFragment::class.java.name)
            .commit()
    }

    fun showPhotoFragment() {
        supportFragmentManager.beginTransaction()
            .remove(supportFragmentManager.findFragmentByTag(VideoFragment::class.java.name)!!)
            .commitNow()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PhotoFragment(), PhotoFragment::class.java.name)
            .commit()
    }
}
