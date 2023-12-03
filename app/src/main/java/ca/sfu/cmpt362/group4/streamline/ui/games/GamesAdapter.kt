package ca.sfu.cmpt362.group4.streamline.ui.games

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.data_models.SpecialGames
import ca.sfu.cmpt362.group4.streamline.databinding.ItemGamesBinding
import com.bumptech.glide.Glide


class GamesViewHolder(private val binding: ItemGamesBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(specialGames: SpecialGames) {
        binding.textTitle.text = specialGames.name

        val discount = "-" + specialGames.discount_percent.toString() + "%"
        binding.discountPercentage.text = discount

        val originalPrice = specialGames.original_price.toString()
        val originalPriceFormatted = if (originalPrice.length > 2) {
            specialGames.currency + " " + originalPrice.substring(
                0,
                originalPrice.length - 2
            ) + "." + originalPrice.substring(originalPrice.length - 2)
        } else {
            specialGames.currency + " 0." + originalPrice.padStart(2, '0')
        }

        binding.originalPrice.apply {
            text = originalPriceFormatted
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        val finalPrice = (specialGames.final_price.toString())
        val finalPriceText = specialGames.currency + " " + finalPrice.substring(
            0,
            finalPrice.length - 2
        ) + "." + finalPrice.substring(finalPrice.length - 2)
        binding.finalPrice.text = finalPriceText

        Glide.with(binding.imagePoster.context)
            .load(specialGames.large_capsule_image)
            .into(binding.imagePoster)
    }
}

class GamesAdapter(
    private var games: List<SpecialGames>,
    private val clickListener: (SpecialGames) -> Unit
) : RecyclerView.Adapter<GamesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGamesBinding.inflate(layoutInflater, parent, false)
        return GamesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        println("position: $position")
        println("Games in LiveData: ${(games[position] as Map<*, *>)}")

        val listGame = (games[position] as Map<*, *>)
        val id = listGame["id"] as Double
        val type = listGame["type"] as Double
        val name = listGame["name"] as String
        val discounted = listGame["discounted"] as Boolean
        val discount_percent = listGame["discount_percent"] as Double
        val original_price = listGame["original_price"] as Double
        val final_price = listGame["final_price"] as Double
        val currency = listGame["currency"] as String
        val large_capsule_image = listGame["large_capsule_image"] as String
        val small_capsule_image = listGame["small_capsule_image"] as String
        val windows_available = listGame["windows_available"] as Boolean
        val linux_available = listGame["linux_available"] as Boolean
        val streamingvideo_available = listGame["streamingvideo_available"] as Boolean
        val discount_expiration = listGame["discount_expiration"] as Double
        val header_image = listGame["header_image"] as String
        val controller_support = "N/A "
        val game = SpecialGames(
            id.toInt(),
            type.toInt(),
            name,
            discounted,
            discount_percent.toInt(),
            original_price.toInt(),
            final_price.toInt(),
            currency,
            large_capsule_image,
            small_capsule_image,
            windows_available,
            linux_available,
            streamingvideo_available,
            discount_expiration.toLong(),
            header_image,
            controller_support
        )
        holder.bind(game)
        holder.itemView.setOnClickListener { clickListener(game) }
    }

    override fun getItemCount() = games.size

    fun updateGames(newGames: List<SpecialGames>) {
        games = newGames
        notifyDataSetChanged()
    }


}