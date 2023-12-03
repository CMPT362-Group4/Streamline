package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.databinding.ItemMovieBinding
import com.bumptech.glide.Glide

class MovieViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie) {
        binding.textTitle.text = movie.title
        val year = movie.release_date.split("-").firstOrNull() // Assumes format is "YYYY-MM-DD"
        binding.textReleaseDate.text = year

        Log.d("Adapter", "${movie.poster_path}")
        Log.d("Adapter", "${movie.backdrop_path}")


        Glide.with(binding.imagePoster.context)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(binding.imagePoster)
    }
}

class MoviesAdapter(private var movies: List<Movie>,
                    private val clickListener: (Movie) -> Unit
) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMovieBinding.inflate(layoutInflater, parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        println("movies in LiveData: $movies")
        holder.bind(movie)
        holder.itemView.setOnClickListener { clickListener(movie) }
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}