package lab3.camera2you

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import lab3.camera2you.fragments.PhotoFragment
import lab3.camera2you.fragments.VideoFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (supportFragmentManager.findFragmentByTag(PhotoFragment::class.java.name) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PhotoFragment.newInstance(), PhotoFragment::class.java.name)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, supportFragmentManager.findFragmentByTag(PhotoFragment::class.java.name)!!, PhotoFragment::class.java.name)
                .commit()
        }
    }
}
