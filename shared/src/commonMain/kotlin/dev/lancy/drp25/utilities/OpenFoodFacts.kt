package dev.lancy.drp25.utilities

// Data classes for OpenFoodFacts API response
import dev.lancy.drp25.data.IngredientItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import dev.lancy.drp25.data.Ingredients

@Serializable
data class OpenFoodFactsResponse(
    val status: Int,
    val product: Product? = null
)

@Serializable
data class Product(
    @SerialName("product_name") val productName: String? = null,
    @SerialName("product_name_en") val productNameEn: String? = null,
    @SerialName("generic_name") val genericName: String? = null,
    @SerialName("quantity") val quantity: String? = null,
    @SerialName("brands") val brands: String? = null,
    @SerialName("categories") val categories: String? = null,
    @SerialName("categories_tags") val categoriesTags: List<String>? = null,
    @SerialName("serving_size") val servingSize: String? = null,
    @SerialName("net_weight") val netWeight: String? = null,
    @SerialName("product_quantity") val productQuantity: String? = null
)

// Sealed class for scan results
sealed class ScanResult {
    data class Success(
        val productName: String,
        val matchedIngredient: IngredientItem,
        val quantity: Double
    ) : ScanResult()

    data class Error(val message: String) : ScanResult()
}

// Helper class to match products to ingredients
object ProductMatcher {
    // Map of keywords to Ingredients enum values
    private val ingredientKeywords = mapOf(
        // Dairy
        "milk" to Ingredients.MILK,
        "butter" to Ingredients.BUTTER,
        "cheese" to Ingredients.CHEDDAR_CHEESE,
        "cheddar" to Ingredients.CHEDDAR_CHEESE,
        "parmesan" to Ingredients.PARMESAN_CHEESE,
        "yogurt" to Ingredients.YOGURT,
        "yoghurt" to Ingredients.YOGURT,
        "cream" to Ingredients.DOUBLE_CREAM,
        "ice cream" to Ingredients.ICE_CREAM,

        // Eggs and poultry
        "egg" to Ingredients.EGG,
        "eggs" to Ingredients.EGG,
        "chicken" to Ingredients.CHICKEN_MEAT,
        "beef" to Ingredients.BEEF_MEAT,
        "pork" to Ingredients.PORK_MEAT,
        "lamb" to Ingredients.LAMB,
        "turkey" to Ingredients.TURKEY_MEAT,
        "duck" to Ingredients.DUCK_MEAT,
        "fish" to Ingredients.FISH,
        "salmon" to Ingredients.SALMON,
        "tuna" to Ingredients.CANNED_TUNA,
        "lobster" to Ingredients.LOBSTER,
        "oyster" to Ingredients.OYSTER,
        "bacon" to Ingredients.BACON,
        "ham" to Ingredients.HAM,
        "sausage" to Ingredients.SAUSAGE,
        "salami" to Ingredients.SALAMI,
        "meatball" to Ingredients.MEATBALLS,

        // Vegetables
        "tomato" to Ingredients.TOMATO,
        "cherry tomato" to Ingredients.CHERRY_TOMATO,
        "potato" to Ingredients.POTATO,
        "sweet potato" to Ingredients.SWEET_POTATO,
        "onion" to Ingredients.ONION,
        "carrot" to Ingredients.CARROT,
        "lettuce" to Ingredients.GREEN_SALAD,
        "salad" to Ingredients.GREEN_SALAD,
        "spinach" to Ingredients.SPINACH,
        "broccoli" to Ingredients.BROCCOLI,
        "cauliflower" to Ingredients.CAULIFLOWER,
        "cabbage" to Ingredients.CABBAGE,
        "pepper" to Ingredients.BELL_PEPPER,
        "bell pepper" to Ingredients.BELL_PEPPER,
        "eggplant" to Ingredients.EGGPLANT,
        "cucumber" to Ingredients.CUCUMBER,
        "corn" to Ingredients.CORN,
        "peas" to Ingredients.PEAS,
        "garlic" to Ingredients.GARLIC,
        "ginger" to Ingredients.GINGER,
        "pumpkin" to Ingredients.PUMPKIN,
        "radish" to Ingredients.RADISH,

        // Fruits
        "apple" to Ingredients.APPLE,
        "banana" to Ingredients.BANANA,
        "orange" to Ingredients.ORANGE,
        "lemon" to Ingredients.LEMON,
        "lime" to Ingredients.LIME,
        "grapes" to Ingredients.GRAPES,
        "strawberry" to Ingredients.STRAWBERRY,
        "blueberry" to Ingredients.BLUEBERRIES,
        "blueberries" to Ingredients.BLUEBERRIES,
        "raspberry" to Ingredients.RASPBERRY,
        "cherry" to Ingredients.CHERRIES,
        "cherries" to Ingredients.CHERRIES,
        "peach" to Ingredients.PEACH,
        "pear" to Ingredients.PEAR,
        "pineapple" to Ingredients.PINEAPPLE,
        "mango" to Ingredients.MANGO,
        "kiwi" to Ingredients.KIWI,
        "melon" to Ingredients.MELON,
        "watermelon" to Ingredients.WATERMELON,
        "avocado" to Ingredients.AVOCADO,
        "coconut" to Ingredients.COCONUT,

        // Pantry & Grains
        "bread" to Ingredients.BREAD,
        "rice" to Ingredients.RICE,
        "pasta" to Ingredients.SPAGHETTI,
        "spaghetti" to Ingredients.SPAGHETTI,
        "macaroni" to Ingredients.MACARONI,
        "fusilli" to Ingredients.FUSILLI,
        "noodle" to Ingredients.NOODLE,
        "ramen" to Ingredients.RAMEN,
        "flour" to Ingredients.WHEAT_FLOUR,
        "sugar" to Ingredients.SUGAR_WHITE,
        "brown sugar" to Ingredients.SUGAR_BROWN,
        "salt" to Ingredients.SALT,
        "oil" to Ingredients.OIL,
        "olive" to Ingredients.OLIVES,
        "cereal" to Ingredients.CEREALS,
        "oat" to Ingredients.OAT,

        // Nuts & Seeds
        "almond" to Ingredients.ALMOND,
        "hazelnut" to Ingredients.HAZELNUT,
        "peanut" to Ingredients.PEANUTS,
        "peanut butter" to Ingredients.PEANUT_BUTTER,

        // Condiments & Sauces
        "ketchup" to Ingredients.KETCHUP,
        "mayonnaise" to Ingredients.MAYONNAISE,
        "mayo" to Ingredients.MAYONNAISE,
        "mustard" to Ingredients.MUSTARD,
        "vinegar" to Ingredients.VINEGAR,
        "honey" to Ingredients.HONEY,
        "jam" to Ingredients.JAM,
        "chili sauce" to Ingredients.CHILI_SAUCE,

        // Baking
        "yeast" to Ingredients.YEAST,
        "baking soda" to Ingredients.BAKING_SODA,
        "vanilla" to Ingredients.VANILLA,
        "cinnamon" to Ingredients.CINNAMON,
        "starch" to Ingredients.STARCH,

        // Snacks & Sweets
        "cookie" to Ingredients.COOKIE,
        "biscuit" to Ingredients.BISCUIT,
        "chocolate" to Ingredients.CHOCOLATE_BAR,
        "popcorn" to Ingredients.POPCORN,
        "pretzel" to Ingredients.PRETZEL,
        "pancake" to Ingredients.PANCAKES,
        "pudding" to Ingredients.PUDDING,

        // Others
        "coffee" to Ingredients.COFFEE_BEANS,
        "wine" to Ingredients.WINE,
        "alcohol" to Ingredients.ALCOHOL,
        "cognac" to Ingredients.COGNAC,
        "beans" to Ingredients.KIDNEY_BEANS,
        "kidney beans" to Ingredients.KIDNEY_BEANS,
        "tortilla" to Ingredients.TORTILLA,
        "taco" to Ingredients.TACO,
        "dumpling" to Ingredients.DUMPLING,
        "falafel" to Ingredients.FALAFEL
    )

    // Common brand names and irrelevant words to filter out
    private val brandNames = setOf(
        "tesco", "sainsbury", "asda", "morrisons", "aldi", "lidl", "marks", "spencer",
        "waitrose", "coop", "iceland", "farmfoods", "costco", "walmart", "target",
        "kroger", "safeway", "whole", "foods", "trader", "joe", "organic", "fresh",
        "free", "range", "farm", "local", "premium", "select", "choice", "value",
        "finest", "taste", "difference", "everyday", "essential", "smart", "price",
        "extra", "special", "own", "brand", "store", "market", "best", "quality",
        "natural", "pure", "real", "authentic", "traditional", "classic", "original",
        "super", "mega", "large", "small", "medium", "xl", "pack", "multipack",
        "family", "size", "portion", "serving", "ready", "to", "eat", "cook",
        "frozen", "chilled", "fresh", "dried", "canned", "tinned", "bottled",
        "jar", "tube", "packet", "bag", "box", "carton", "tray", "punnet"
    )

    private val irrelevantWords = setOf(
        "the", "and", "or", "with", "in", "on", "at", "by", "for", "of", "to",
        "from", "per", "each", "piece", "pieces", "item", "items", "product",
        "food", "drink", "beverage", "snack", "meal", "dish", "recipe", "style",
        "flavour", "flavor", "taste", "mixed", "assorted", "variety", "selection",
        "collection", "range", "series", "edition", "version", "type", "kind",
        "sort", "grade", "class", "level", "standard", "regular", "normal",
        "basic", "simple", "plain", "mild", "medium", "strong", "light", "heavy",
        "thick", "thin", "fine", "coarse", "smooth", "rough", "soft", "hard",
        "crispy", "crunchy", "tender", "juicy", "dry", "wet", "hot", "cold",
        "warm", "cool", "sweet", "sour", "bitter", "salty", "spicy", "mild"
    )

    fun findMatchingIngredient(
        product: Product,
        existingIngredients: List<IngredientItem>
    ): IngredientItem? {
        val productName =
            (product.productName ?: product.productNameEn ?: product.genericName ?: "").lowercase()
        val categories = product.categoriesTags ?: emptyList()
        val brands = (product.brands ?: "").lowercase()

        // Clean the product name by removing brand names and irrelevant words
        val cleanedProductName = cleanProductName(productName)

        // First, try exact name match with existing ingredients using cleaned name
        existingIngredients.forEach { ingredient ->
            val ingredientName = ingredient.name.lowercase()
            if (cleanedProductName.contains(ingredientName) ||
                productName.contains(ingredientName) ||
                brands.contains(ingredientName)
            ) {
                return ingredient
            }
        }

        // Try keyword matching with both original and cleaned names
        ingredientKeywords.forEach { (keyword, ingredientEnum) ->
            val keywordLower = keyword.lowercase()

            // Check in cleaned product name first (higher priority)
            if (cleanedProductName.contains(keywordLower) ||
                // Then check original product name
                productName.contains(keywordLower) ||
                // Then check brands
                brands.contains(keywordLower) ||
                // Finally check categories
                categories.any { it.lowercase().contains(keywordLower) }
            ) {

                return existingIngredients.find {
                    it.name.equals(ingredientEnum.displayName, ignoreCase = true)
                }
            }
        }

        // Try partial word matching for better results
        return findPartialMatch(cleanedProductName, existingIngredients)
    }

    private fun cleanProductName(productName: String): String {
        val words = productName.split(Regex("\\s+|[-_.,;:!?()]"))
        val cleanedWords = words.filter { word ->
            val cleanWord = word.trim().lowercase()
            cleanWord.isNotEmpty() &&
                    cleanWord.length > 2 &&
                    !brandNames.contains(cleanWord) &&
                    !irrelevantWords.contains(cleanWord) &&
                    !cleanWord.matches(Regex("\\d+.*")) // Remove words starting with numbers
        }
        return cleanedWords.joinToString(" ")
    }

    private fun findPartialMatch(
        cleanedName: String,
        existingIngredients: List<IngredientItem>
    ): IngredientItem? {
        val words = cleanedName.split(Regex("\\s+"))

        // Try to find ingredients that match any significant word
        for (word in words) {
            if (word.length > 3) { // Only consider words longer than 3 characters
                // Check if any keyword contains this word or vice versa
                ingredientKeywords.forEach { (keyword, ingredientEnum) ->
                    if (word.contains(keyword) || keyword.contains(word)) {
                        val matchedIngredient = existingIngredients.find {
                            it.name.equals(ingredientEnum.displayName, ignoreCase = true)
                        }
                        if (matchedIngredient != null) return matchedIngredient
                    }
                }

                // Check if any existing ingredient name contains this word
                existingIngredients.forEach { ingredient ->
                    val ingredientWords = ingredient.name.lowercase().split(Regex("\\s+"))
                    if (ingredientWords.any { it.contains(word) || word.contains(it) }) {
                        return ingredient
                    }
                }
            }
        }

        return null
    }

    // Hardcoded quantities for specific ingredients (in their actual amounts)
    private val hardcodedQuantities = mapOf(
        "egg" to 12.0,           // 12 eggs
        "eggs" to 12.0,          // 12 eggs
        "butter" to 250.0,       // 250g
        "milk" to 2200.0         // 2200ml (2.2L)
    )

    fun parseQuantity(product: Product): Double {
        // Check if this is a product that should have hardcoded quantities
        val productName =
            (product.productName ?: product.productNameEn ?: product.genericName ?: "").lowercase()
        val cleanedName = cleanProductName(productName)

        // Check for hardcoded quantities first
        hardcodedQuantities.forEach { (keyword, quantity) ->
            if (productName.contains(keyword) || cleanedName.contains(keyword)) {
                return quantity
            }
        }

        // Try different quantity fields and parsing strategies
        val quantityStrings = listOfNotNull(
            product.quantity,
            product.netWeight,
            product.servingSize,
            product.productQuantity
        )

        for (quantityString in quantityStrings) {
            val parsed = parseQuantityString(quantityString)
            if (parsed > 0.0) return parsed
        }

        // Default fallback
        return 1.0
    }

    private fun parseQuantityString(quantityString: String?): Double {
        if (quantityString == null) return 0.0

        val cleanString = quantityString.lowercase().trim()

        // Common patterns for quantities
        val patterns = listOf(
            // Pattern: "500g", "1.5kg", "250ml", "2l", etc.
            Regex("([0-9.]+)\\s*([kmgt]?[glm]?[bl]?)"),

            // Pattern: "500 grams", "1.5 kilograms", "250 milliliters", etc.
            Regex("([0-9.]+)\\s*(gram|kilogram|liter|litre|milliliter|millilitre|pound|ounce|oz|lb)s?"),

            // Pattern: "6 x 250ml", "4 pack", etc.
            Regex("([0-9]+)\\s*[x×]\\s*([0-9.]+)\\s*([a-z]+)"),

            // Pattern: just numbers "500", "1.5", etc.
            Regex("([0-9.]+)")
        )

        for (pattern in patterns) {
            val match = pattern.find(cleanString)
            if (match != null) {
                val groups = match.groupValues

                if (groups.size >= 4 && groups[1].isNotEmpty() && groups[2].isNotEmpty()) {
                    // Multi-pack scenario: "6 x 250ml"
                    val count = groups[1].toDoubleOrNull() ?: 1.0
                    val unitQuantity = groups[2].toDoubleOrNull() ?: 1.0
                    val unit = groups[3]

                    val baseQuantity = count * unitQuantity
                    return convertToStandardUnit(baseQuantity, unit)

                } else if (groups.size >= 3 && groups[1].isNotEmpty()) {
                    // Single quantity with unit: "500g"
                    val quantity = groups[1].toDoubleOrNull() ?: 1.0
                    val unit = if (groups.size > 2) groups[2] else ""

                    return convertToStandardUnit(quantity, unit)
                }
            }
        }

        return 0.0
    }

    private fun convertToStandardUnit(quantity: Double, unit: String): Double {
        return when (unit.lowercase().replace("s", "")) {
            // Weight conversions (convert to grams, then to reasonable units)
            "kg", "kilogram" -> quantity * 1000.0 / 250.0 // Assume 250g portions
            "g", "gram" -> quantity / 100.0 // Convert grams to "units" (100g = 1 unit)
            "lb", "pound" -> quantity * 453.592 / 250.0 // Convert pounds to 250g portions
            "oz", "ounce" -> quantity * 28.3495 / 100.0 // Convert ounces to 100g portions

            // Volume conversions (convert to ml, then to reasonable units)
            "l", "liter", "litre" -> quantity * 1000.0 / 250.0 // Assume 250ml portions
            "ml", "milliliter", "millilitre" -> quantity / 250.0 // Convert ml to 250ml portions

            // Special units
            "pack", "packet", "box", "bottle", "can", "jar" -> quantity

            // Default: assume it's already in reasonable units
            else -> {
                // For very large numbers, assume it's in grams/ml and convert
                if (quantity > 100) {
                    quantity / 100.0
                } else {
                    quantity.coerceAtLeast(0.5) // Minimum of 0.5 units
                }
            }
        }
    }
}

// Fetch function for multi-scan capability - does not update ingredients directly
suspend fun fetchProductForMultiScan(
    barcode: String,
    httpClient: HttpClient,
    currentIngredients: List<IngredientItem>,
    onResult: (ScanResult) -> Unit
) {
    try {
        val response: OpenFoodFactsResponse = httpClient.get("https://world.openfoodfacts.org/api/v2/product/$barcode").body()

        if (response.status == 1 && response.product != null) {
            val product = response.product
            val matchedIngredient = ProductMatcher.findMatchingIngredient(product, currentIngredients)

            if (matchedIngredient != null) {
                // Parse quantity from the product with improved logic
                val scannedQuantity = ProductMatcher.parseQuantity(product)
                val productDisplayName = product.productName ?: product.productNameEn ?: product.genericName ?: "Unknown Product"

                onResult(ScanResult.Success(
                    productName = productDisplayName,
                    matchedIngredient = matchedIngredient,
                    quantity = scannedQuantity
                ))
            } else {
                onResult(ScanResult.Error("❌ No matching ingredient found"))
            }
        } else {
            onResult(ScanResult.Error("❌ Product not found in database"))
        }
    } catch (e: Exception) {
        onResult(ScanResult.Error("❌ Error scanning: ${e.message}"))
    }
}

// Single-scan function that immediately updates the pantry (for backward compatibility)
suspend fun fetchProductAndUpdatePantry(
    barcode: String,
    httpClient: HttpClient,
    ingredientsManager: PersistenceManager<List<IngredientItem>>,
    onResult: (String) -> Unit
) {
    fetchProductForMultiScan(
        barcode = barcode,
        httpClient = httpClient,
        currentIngredients = ingredientsManager.state.value
    ) { result ->
        when (result) {
            is ScanResult.Success -> {
                // Create updated ingredient with increased quantity
                val updatedIngredient = result.matchedIngredient.copy(
                    quantity = result.matchedIngredient.quantity + result.quantity
                )

                // Update the ingredient in the manager using the extension function
                ingredientsManager.updateIngredient(updatedIngredient)

                // Format the quantity for display
                val formattedQuantity = if (result.quantity % 1.0 == 0.0) {
                    result.quantity.toInt().toString()
                } else {
                    result.quantity.toString().toString()
                }

                onResult("✓ Added ${result.productName} (+$formattedQuantity ${result.matchedIngredient.defaultUnit})")
            }
            is ScanResult.Error -> {
                onResult(result.message)
            }
        }
    }
}

// Configure HTTP client for JSON
val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}