package lab3.mediaplayer.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_music_library.*
import lab3.mediaplayer.R
import lab3.mediaplayer.media.library.BrowseLibrary

class MusicLibraryActivity : MusicPlayerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_library)

        toolbar.title = getString(R.string.music_library_title)

        setSupportActionBar(toolbar)

        setupViewPager(content_viewpager)

        tabs.setupWithViewPager(content_viewpager)

        if (intent.getBooleanExtra(EXPAND_PLAYER_KEY, false)) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.adapter = adapter
    }

    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitles = mutableListOf<String>()

        override fun getItem(position: Int) = fragmentList[position]

        override fun getCount() = fragmentList.size

        override fun getPageTitle(position: Int) = fragmentTitles[position]

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList += fragment
            fragmentTitles += title
        }
    }


    override fun mediaControlsInitialized() {
        mediaBrowser.subscribe(BrowseLibrary.SONGS, browseCallback)
    }

    companion object {
        const val EXPAND_PLAYER_KEY = "EXPAND_PLAYER"

        fun start(context: Context, expandPlayer: Boolean = false) {
            val intent = Intent(context, MusicLibraryActivity::class.java)
            intent.putExtra(EXPAND_PLAYER_KEY, expandPlayer)
            context.startActivity(intent)
        }
    }
}

