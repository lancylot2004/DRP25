package dev.lancy.drp25.data

import androidx.compose.runtime.mutableStateOf
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Recipe(
    @PrimaryKey
    var id: ObjectId = ObjectId(),
    /**
     * The name of the recipe.
     */
    var name: String = "",
    /**
     * Time taken to cook the recipe, in minutes.
     */
    var cookingTime: Int = 0,
    /**
     * Time taken to clean up after cooking the recipe, in minutes.
     */
    var cleanupTime: Int? = null,
    /**
     * The number of portions the recipe serves.
     */
    var portions: Int = 1,
    /**
     * User-rating of the recipe, from 0.0 to 5.0.
     */
    var rating: Float = 0f,
    /**
     * The diet of the recipe. None if not applicable.
     */
    var diet: String? = null,
    /**
     * The cuisine of the recipe. None if not applicable.
     */
    var cuisine: String? = null,
    /**
     * How much energy the recipe provides, in kcal.
     */
    var energy: Int? = null,
    /**
     * The vertical format image for this recipe.
     *
     * TODO: Specify format.
     */
    var cardImage: String = "",
    /**
     * The horizontal format image for this recipe.
     */
    var smallImage: String = "",
    /**
     * The video tutorial for this recipe.
     */
    var video: String? = null,
    /**
     * The ingredients required to cook this recipe.
     */
    var ingredients: RealmList<Ingredient> = realmListOf(),
    /**
     * The steps to cook this recipe.
     */
    var steps: RealmList<Step> = realmListOf(),
): RealmObject {
    constructor() : this(ObjectId())
}

class Ingredient(
    var name: String = "",
    var amount: String? = null,
) : EmbeddedRealmObject {
    constructor() : this("")
}

class Step(
    var description: String = "",
    var videoTimestamp: Int? = null,
) : EmbeddedRealmObject {
    constructor() : this("")
}
