package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityMovieDetailBinding
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import ca.sfu.cmpt362.group4.streamline.room.databases.MovieDatabase
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var movie: Movie

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var movieDao: MovieDao
    private lateinit var moviesRepository: MoviesRepository

    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable the back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.back_arrow)

        supportActionBar?.title = ""
        formatLayout()

        movieDao = MovieDatabase.getInstance(this).movieDao
        moviesRepository = MoviesRepository(movieDao)
        moviesViewModel = ViewModelProvider(this, MoviesViewModelFactory(moviesRepository))[MoviesViewModel::class.java]

        movie = intent.getParcelableExtra<Movie>("movie")!!

        movie?.let {

            //supportActionBar?.title = it.title

            binding.textTitle.text = it.title
            binding.textOverview.text = it.overview
            Log.d("Details", "${it.poster_path}")
            Log.d("Details", "${it.backdrop_path}")
            Log.d("Details", "${it.overview}")

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.poster_path}")
                .into(binding.imagePoster)

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${it.backdrop_path}")
                .into(binding.imageBackdrop)
        }
        handleAddButton()
        handleRatingBar()
    }

    fun formatLayout(){
        val previousTitle = intent.getStringExtra("previousPageTitle")
        if (previousTitle == "Home") {
            // Coming from HomeFragment
            Log.d("MovieDetailsActivity", "Coming from Home")
            binding.buttonAdd.visibility = View.GONE
            binding.buttonDelete.visibility = View.VISIBLE

            //layout rating bar relative to delete button
            val params = binding.ratingBar.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.button_delete)
            binding.ratingBar.layoutParams = params

            handleDeleteButton()
        } else {
            // Not coming from HomeFragment
            Log.d("MovieDetailsActivity", "Not coming from Home")
            binding.buttonAdd.visibility = View.VISIBLE
            binding.buttonDelete.visibility = View.GONE

            handleAddButton()
        }
    }

    fun handleRatingBar(){
        ratingBar = binding.ratingBar

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                movie?.let {
                    moviesViewModel.updateMovieRating(it.databaseId, rating)
                }
            }
        }

    }

    fun handleAddButton(){
        val buttonAdd = binding.buttonAdd
        buttonAdd.setOnClickListener {
            movie?.let {
                // Using api service id which is unique to movie
                Log.d("MovieDetailActivity", "${movie.id}")
                if (!moviesViewModel.isMovieInList(movie.id)) {
                    moviesViewModel.insertMovie(movie)
                    Toast.makeText(this, "Movie added to your list", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Movie is already in your list", Toast.LENGTH_SHORT).show()
                }

                finish()
            }
        }
    }

    fun handleDeleteButton(){
        val buttonDelete = binding.buttonDelete

        binding.buttonDelete.setOnClickListener {
            movie?.let {
                // Delete the movie from database
                moviesViewModel.deleteMovie(it)

                // Display a message
                Toast.makeText(this, "Movie deleted from your list", Toast.LENGTH_SHORT).show()
                finish()
            }
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