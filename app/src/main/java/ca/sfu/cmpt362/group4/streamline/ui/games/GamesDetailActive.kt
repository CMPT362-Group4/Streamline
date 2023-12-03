package ca.sfu.cmpt362.group4.streamline.ui.games

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityGameDetailBinding

class GamesDetailActive : AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.back_arrow)

        val id = intent.getStringExtra("id")
        val url = "https://store.steampowered.com/app/${id}"

        webView = binding.wrapContent
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}