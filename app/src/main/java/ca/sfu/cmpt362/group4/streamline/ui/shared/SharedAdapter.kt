package ca.sfu.cmpt362.group4.streamline.ui.shared

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.data_models.SharedContent
import com.bumptech.glide.Glide

class SharedViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val textTitle: TextView = view.findViewById(R.id.text_title)
    private val textReleaseDate: TextView = view.findViewById(R.id.text_release_date)
    private val imagePoster: ImageView = view.findViewById(R.id.image_poster)
    private val ratingBar: RatingBar? = view.findViewById(R.id.rating_bar)

    //update UI
    fun bind(sharedContent: Movie) {
        textTitle.text = sharedContent.title
        val year = sharedContent.release_date.split("-").firstOrNull()
        textReleaseDate.text = year
        Glide.with(imagePoster.context)
            .load("https://image.tmdb.org/t/p/w500${sharedContent.poster_path}")
            .into(imagePoster)

        Log.d("MoviesAdapter", "${sharedContent.poster_path}")
        Log.d("MoviesAdapter", "${sharedContent.backdrop_path}")

        ratingBar?.rating = sharedContent.rating

    }
}

class SharedAdapter(private var sharedContent: List<Movie>,
                    private val clickListener: (Movie) -> Unit,
                    private val layoutResId: Int
) : RecyclerView.Adapter<SharedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(layoutResId, parent, false)
        return SharedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SharedViewHolder, position: Int) {
        val shared = sharedContent[position]
        println("shared content in LiveData: $sharedContent")
        holder.bind(shared)
        holder.itemView.setOnClickListener { clickListener(shared) }
    }

    override fun getItemCount() = sharedContent.size

    fun updateSharedContent(sharedContentList: List<Movie>) {
        sharedContent = sharedContentList
        notifyDataSetChanged()
    }
}