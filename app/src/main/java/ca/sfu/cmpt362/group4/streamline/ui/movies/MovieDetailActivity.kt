package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityMovieDetailBinding
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentMoviesBinding
import ca.sfu.cmpt362.group4.streamline.databinding.ItemMovieBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val movie = intent.getParcelableExtra<Movie>("movie")

        movie?.let {

            binding.textTitle.text = it.title
            binding.textOverview.text = it.overview
            Log.d("Details", "${it.poster_path}")
            Log.d("Details", "${it.backdrop_path}")
            Log.d("Details", "${it.overview}")

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.poster_path}")
                .error(R.drawable.streamline)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        // Log error or handle the load failure
                        Log.e("MovieDetailActivity", "Error loading image", e)
                        return false // Return false to allow Glide to handle the error
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        // Image loaded successfully
                        return false // Return false to allow Glide to handle the resource
                    }
                })
                .into(binding.imagePoster)

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.backdrop_path}")
                .into(binding.imageBackdrop)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Close the current activity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}