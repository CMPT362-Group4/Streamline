package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.data_models.User
import com.bumptech.glide.Glide

class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val textTitle: TextView = view.findViewById(R.id.text_title)
    private val textReleaseDate: TextView = view.findViewById(R.id.text_release_date)
    private val imagePoster: ImageView = view.findViewById(R.id.image_poster)
    private val ratingBar: RatingBar? = view.findViewById(R.id.rating_bar)


    //update UI
    fun bind(movie: Movie) {
        textTitle.text = movie.title
        val year = movie.release_date.split("-").firstOrNull()
        textReleaseDate.text = year

        Glide.with(imagePoster.context)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(imagePoster)

        Log.d("MoviesAdapter", "${movie.poster_path}")
        Log.d("MoviesAdapter", "${movie.backdrop_path}")

        ratingBar?.rating = movie.rating

    }
}

class MoviesAdapter(private var movies: List<Movie>,
                    private val clickListener: (Movie) -> Unit,
                    private val layoutResId: Int
) : RecyclerView.Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(layoutResId, parent, false)
        return MovieViewHolder(view)
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


class SearchMovieAdapter : RecyclerView.Adapter<SearchMovieAdapter.MovieViewHolder>() {

    private var movies: List<Movie> = emptyList()
    private var onItemClickListener: ((Movie) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_search, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movies.size

    fun submitList(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Movie) -> Unit) {
        onItemClickListener = listener
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val searchResultImage: ImageView = itemView.findViewById(R.id.searchResultImage)
        private val searchResultName: TextView = itemView.findViewById(R.id.searchResultName)
        private val searchResultReleaseDate: TextView =
            itemView.findViewById(R.id.searchResultReleaseDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = movies[position]
                    onItemClickListener?.invoke(movie)
                }
            }
        }

        fun bind(movie: Movie) {
            // Load the image using Glide
            Glide.with(searchResultImage.context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(searchResultImage)

            searchResultName.text = movie.title
            searchResultReleaseDate.text = movie.release_date
        }
    }
}