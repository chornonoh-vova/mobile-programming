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
import lab3.mediaplayer.ui.fragments.AlbumsFragment
import lab3.mediaplayer.ui.fragments.ArtistsFragment
import lab3.mediaplayer.ui.fragments.SongsFragment

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

        adapter.addFragment(SongsFragment(), getString(R.string.songs_fragment_title))
        adapter.addFragment(ArtistsFragment(), getString(R.string.artists_fragment_title))
        adapter.addFragment(AlbumsFragment(), getString(R.string.albums_fragment_title))

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
//        mediaBrowser.subscribe(BrowseLibrary.SONGS, browseAllSongs)
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

