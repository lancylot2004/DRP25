package dev.lancy.drp25.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import dev.lancy.drp25.ui.shared.NavTarget
import dev.lancy.drp25.utilities.rememberPreferredTemperatureSystemManager
import dev.lancy.drp25.utilities.rememberPreferredVolumeSystemManager
import dev.lancy.drp25.utilities.rememberPreferredWeightSystemManager
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Parcelize
data class Recipe(
    val id: String = "",
    val name: String,
    val description: String,
    val rating: Float = 0f,
    val numRatings: Int = 0,
    val portions: Int = 1,
    val cookingTime: Int,
    val cleanupTime: Int? = null,
    val calories: Int? = null,
    val macros: Map<String, Double> = mapOf(),
    val ingredients: List<Ingredient>,
    val keyIngredients: List<String>,
    val diet: Diet? = null,
    val cuisine: Cuisine? = null,
    val mealType: MealType? = null,
    val utensils: List<Utensil>,
    val steps: List<Step>,
    val cardImage: String = "",
    val smallImage: String = "",
    val video: String? = null,
) : NavTarget,
    Parcelable

@Serializable
@Parcelize
class Step(
    var description: String = "",
    var videoTimestamp: Int? = null,
) : Parcelable
