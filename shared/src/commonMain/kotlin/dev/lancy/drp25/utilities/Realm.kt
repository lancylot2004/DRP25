//package dev.lancy.drp25.utilities
//
//import dev.lancy.drp25.data.Ingredient
//import dev.lancy.drp25.data.Recipe
//import dev.lancy.drp25.data.Step
//import io.realm.kotlin.InitialDataCallback
//import io.realm.kotlin.ext.query
//import io.realm.kotlin.Realm
//import io.realm.kotlin.RealmConfiguration
//import io.realm.kotlin.ext.realmListOf
//import kotlinx.coroutines.runBlocking
//
//private val spaghetti = Recipe().apply {
//    name = "Spaghetti Carbonara"
//    cookingTime = 20
//    portions = 4
//    rating = 4.5f
//    cardImage = "https://s23209.pcdn.co/wp-content/uploads/2014/03/IMG_2694edit-1.jpg"
//    smallImage = "https://s23209.pcdn.co/wp-content/uploads/2014/03/IMG_2694edit-1.jpg"
//    cuisine = "Italian"
//    energy = 600
//    ingredients = realmListOf(
//        Ingredient(name = "Spaghetti", amount = "200g"),
//        Ingredient(name = "Eggs", amount = "2 large"),
//        Ingredient(name = "Parmesan cheese", amount = "50g"),
//        Ingredient(name = "Pancetta", amount = "100g"),
//        Ingredient(name = "Black pepper"),
//        Ingredient(name = "Salt"),
//        Ingredient(name = "Olive oil"),
//    )
//    steps = realmListOf(
//        Step(description = "Boil water in a large pot and add salt.", videoTimestamp = 12),
//        Step(description = "Cook spaghetti according to package instructions until al dente.", videoTimestamp = 30),
//        Step(description = "In a bowl, whisk together eggs and grated Parmesan cheese.", videoTimestamp = 45),
//        Step(description = "In a pan, cook pancetta until crispy. Remove from heat.", videoTimestamp = 60),
//        Step(description = "Drain spaghetti and add it to the pan with pancetta.", videoTimestamp = 75),
//    )
//}
//
//private val grilledCheese = Recipe().apply {
//    name = "Grilled Cheese Sandwich"
//    cookingTime = 10
//    portions = 1
//    rating = 4.2f
//    cardImage = "https://cdn.loveandlemons.com/wp-content/uploads/2023/01/grilled-cheese.jpg"
//    smallImage = "https://cdn.loveandlemons.com/wp-content/uploads/2023/01/grilled-cheese.jpg"
//    cuisine = "American"
//    energy = 350
//    ingredients = realmListOf(
//        Ingredient(name = "Bread slices", amount = "2"),
//        Ingredient(name = "Cheddar cheese", amount = "2 slices"),
//        Ingredient(name = "Butter", amount = "1 tbsp")
//    )
//    steps = realmListOf(
//        Step(description = "Butter one side of each bread slice."),
//        Step(description = "Place cheese between unbuttered sides."),
//        Step(description = "Grill in a pan until golden on both sides.")
//    )
//}
//
//private val scrambledEggs = Recipe().apply {
//    name = "Scrambled Eggs"
//    cookingTime = 5
//    portions = 1
//    rating = 4.0f
//    cardImage = "https://cdn.loveandlemons.com/wp-content/uploads/opengraph/2021/05/scrambled-eggs-1.jpg"
//    smallImage = "https://cdn.loveandlemons.com/wp-content/uploads/opengraph/2021/05/scrambled-eggs-1.jpg"
//    cuisine = "Universal"
//    energy = 200
//    ingredients = realmListOf(
//        Ingredient(name = "Eggs", amount = "2"),
//        Ingredient(name = "Butter", amount = "1 tsp"),
//        Ingredient(name = "Salt"),
//        Ingredient(name = "Black pepper")
//    )
//    steps = realmListOf(
//        Step(description = "Crack eggs into a bowl and whisk."),
//        Step(description = "Melt butter in a pan."),
//        Step(description = "Pour eggs and stir gently until set.")
//    )
//}
//
//private val avocadoToast = Recipe().apply {
//    name = "Avocado Toast"
//    cookingTime = 5
//    portions = 1
//    rating = 4.3f
//    cardImage = "https://alegumeaday.com/wp-content/uploads/2024/03/Bean-avocado-toast-3.jpg"
//    smallImage = "https://alegumeaday.com/wp-content/uploads/2024/03/Bean-avocado-toast-3.jpg"
//    cuisine = "Modern"
//    energy = 300
//    ingredients = realmListOf(
//        Ingredient(name = "Bread slice", amount = "1"),
//        Ingredient(name = "Avocado", amount = "1/2"),
//        Ingredient(name = "Salt"),
//        Ingredient(name = "Lemon juice", amount = "1 tsp"),
//        Ingredient(name = "Chili flakes")
//    )
//    steps = realmListOf(
//        Step(description = "Toast the bread."),
//        Step(description = "Mash avocado with lemon juice, salt, and chili flakes."),
//        Step(description = "Spread avocado mixture over toast.")
//    )
//}
//
//private val pbBananaSmoothie = Recipe().apply {
//    name = "Peanut Butter Banana Smoothie"
//    cookingTime = 5
//    portions = 2
//    rating = 4.6f
//    cardImage = "https://example.com/pb-smoothie-card.jpg"
//    smallImage = "https://example.com/pb-smoothie-small.jpg"
//    cuisine = "American"
//    energy = 400
//    ingredients = realmListOf(
//        Ingredient(name = "Banana", amount = "1"),
//        Ingredient(name = "Peanut butter", amount = "2 tbsp"),
//        Ingredient(name = "Milk", amount = "1 cup"),
//        Ingredient(name = "Honey", amount = "1 tsp"),
//        Ingredient(name = "Ice cubes")
//    )
//    steps = realmListOf(
//        Step(description = "Add all ingredients to a blender."),
//        Step(description = "Blend until smooth."),
//        Step(description = "Serve immediately.")
//    )
//}
//
//val REALM_CONFIG = RealmConfiguration
//    .Builder(schema = setOf(Recipe::class, Ingredient::class, Step::class))
//    .build()
//val realm = Realm.open(REALM_CONFIG)
//
//suspend fun initializeRealmWithRecipes() {
////    realm.write {
////        val asdf = query<Recipe>().find()
////        delete(asdf)
////    }
//
//    realm.write {
//        listOf(spaghetti, grilledCheese, scrambledEggs, avocadoToast, pbBananaSmoothie).forEach { recipe ->
//            val exists = query<Recipe>("name == $0", recipe.name).find().isNotEmpty()
//            if (!exists) {
//                copyToRealm(recipe)
//            }
//        }
//    }
//}
