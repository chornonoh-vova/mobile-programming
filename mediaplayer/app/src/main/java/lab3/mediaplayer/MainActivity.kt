package lab3.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import lab3.mediaplayer.media.library.BrowseLibrary
import lab3.mediaplayer.media.library.LocalMusicSource

class MainActivity : AppCompatActivity() {
    lateinit var musicSource: LocalMusicSource
    lateinit var browseLibrary: BrowseLibrary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        musicSource = LocalMusicSource(this)
        browseLibrary = BrowseLibrary(musicSource)

        println(browseLibrary)

    }
}
