package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityMovieDetailBinding
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var movie: Movie

    private val moviesViewModel: MoviesViewModel by viewModels {
        MoviesViewModelFactory(this)
    }

    private lateinit var ratingBar: RatingBar

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid


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
        //handleAddButton()
        handleRatingBar()
    }

    fun formatLayout(){
        val previousTitle = intent.getStringExtra("previousPageTitle")

        // Coming from HomeMoviesFragment
        if (previousTitle == "HomeMovies") {
            Log.d("MovieDetailsActivity", "Coming from Home")
            binding.buttonAdd.visibility = View.GONE
            binding.buttonDelete.visibility = View.VISIBLE

            //layout rating bar relative to delete button
            val params = binding.ratingBar.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.button_delete)
            binding.ratingBar.layoutParams = params

            handleDeleteButton()
        }
        // Coming from Movies Fragment
        else if(previousTitle == "Movies"){
            Log.d("MovieDetailsActivity", "Not coming from Home")
            binding.buttonAdd.visibility = View.VISIBLE
            binding.buttonDelete.visibility = View.GONE

            handleAddButton()
        }
        // Coming from UserListDisplay
        else {
            binding.buttonAdd.visibility = View.GONE
            binding.buttonDelete.visibility = View.GONE
        }
    }

    fun handleRatingBar(){
        ratingBar = binding.ratingBar

        ratingBar.rating = movie.rating

        ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                movie?.let {
                    moviesViewModel.updateMovieRating(it.id, rating)
                    moviesViewModel.updateMovieFieldInFirebase(it.id, "rating", rating)
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
                    movie.rating = ratingBar.rating
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