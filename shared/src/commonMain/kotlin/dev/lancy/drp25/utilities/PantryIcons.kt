package dev.lancy.drp25.utilities

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
import dev.lancy.drp25.shared.resources.Res
import dev.lancy.drp25.shared.resources.allDrawableResources

enum class IngredientIcon(val resourceKey: String) {
    ALCOHOL("ingredient_alcohol"),
    ALMOND("ingredient_almond"),
    ALMOND_MILK("ingredient_almond_milk"),
    APPLE("ingredient_apple"),
    APPLE_CIDER("ingredient_apple_cider"),
    AVOCADO("ingredient_avocado"),
    BACON("ingredient_bacon"),
    BAKING_SODA("ingredient_baking_soda"),
    BANANA("ingredient_banana"),
    BEEF_MEAT("ingredient_beef_meat"),
    BELL_PEPPER("ingredient_bell_pepper"),
    BISCUIT("ingredient_biscuit"),
    BLUEBERRIES("ingredient_blueberries"),
    BREAD("ingredient_bread"),
    BROCCOLI("ingredient_broccoli"),
    BUNS("ingredient_buns"),
    BUTTER("ingredient_butter"),
    CABBAGE("ingredient_cabbage"),
    CANNED_TUNA("ingredient_canned_tuna"),
    CARROT("ingredient_carrot"),
    CAULIFLOWER("ingredient_cauliflower"),
    CEREALS("ingredient_cereals"),
    CHEDDAR_CHEESE("ingredient_cheddar_cheese"),
    CHEESE_SLICES("ingredient_cheese_slices"),
    CHERRIES("ingredient_cherries"),
    CHERRY_TOMATO("ingredient_cherry_tomato"),
    CHICKEN_MEAT("ingredient_chicken_meat"),
    CHILI("ingredient_chili"),
    CHILI_SAUCE("ingredient_chili_sauce"),
    CHOCOLATE_BAR("ingredient_chocolate_bar"),
    CINNAMON("ingredient_cinnamon"),
    COCONUT("ingredient_coconut"),
    COFFEE_BEANS("ingredient_coffee_beans"),
    COGNAC("ingredient_cognac"),
    COOKIE("ingredient_cookie"),
    CORN("ingredient_corn"),
    CUCUMBER("ingredient_cucumber"),
    DOUBLE_CREAM("ingredient_double_cream"),
    DUCK_MEAT("ingredient_duck_meat"),
    DUMPLING("ingredient_dumpling"),
    EGG("ingredient_egg"),
    EGGPLANT("ingredient_eggplant"),
    FALAFEL("ingredient_falafel"),
    FISH("ingredient_fish"),
    FUSILI("ingredient_fusili"),
    GARLIC("ingredient_garlic"),
    GINGER("ingredient_ginger"),
    GRAPES("ingredient_grapes"),
    GREEN_SALAD("ingredient_green_salad"),
    HAM("ingredient_ham"),
    HAZELNUT("ingredient_hazelnut"),
    HONEY("ingredient_honey"),
    ICE_CREAM("ingredient_ice_cream"),
    JAM("ingredient_jam"),
    KETCHUP("ingredient_ketchup"),
    KIDNEY_BEANS("ingredient_kidney_beans"),
    KIWI("ingredient_kiwi"),
    LAMB("ingredient_lamb"),
    LEAFY_GREEN("ingredient_leafy_green"),
    LEMON("ingredient_lemon"),
    LEMON_JUICE("ingredient_lemon_juice"),
    LIME("ingredient_lime"),
    LOBSTER("ingredient_lobster"),
    MACARONI("ingredient_macaroni"),
    MANGO("ingredient_mango"),
    MAYONNAISE("ingredient_mayonnaise"),
    MEATBALLS("ingredient_meatballs"),
    MEAT_BURGER("ingredient_meat_burger"),
    MELON("ingredient_melon"),
    MILK("ingredient_milk"),
    MUSTARD("ingredient_mustard"),
    NOODLE("ingredient_noodle"),
    OAT("ingredient_oat"),
    OIL("ingredient_oil"),
    OLIVES("ingredient_olives"),
    ONION("ingredient_onion"),
    ORANGE("ingredient_tangerine"),
    OYSTER("ingredient_oyster"),
    PANCAKES("ingredient_pancakes"),
    PARMESAN_CHEESE("ingredient_parmesan_cheese"),
    PARSLEY("ingredient_parsley"),
    PEACH("ingredient_peach"),
    PEANUTS("ingredient_peanuts"),
    PEANUT_BUTTER("ingredient_peanut_butter"),
    PEAR("ingredient_pear"),
    PEAS("ingredient_peas"),
    PEPPER_BLACK("ingredient_pepper_black"),
    PINEAPPLE("ingredient_pineapple"),
    POPCORN("ingredient_popcorn"),
    PORK_MEAT("ingredient_pork_meat"),
    POTATO("ingredient_potato"),
    POULTRY_LEG("ingredient_poultry_leg"),
    PRETZEL("ingredient_pretzel"),
    PUDDING("ingredient_pudding"),
    PUMPKIN("ingredient_pumpkin"),
    RADISH("ingredient_radish"),
    RAMEN("ingredient_ramen"),
    RASPBERRY("ingredient_raspberry"),
    RICE("ingredient_rice"),
    SALAMI("ingredient_salami"),
    SALMON("ingredient_salmon"),
    SALT("ingredient_salt"),
    SAUSAGE("ingredient_sausage"),
    SEASONING("ingredient_seasoning"),
    SOYA("ingredient_soya"),
    SPAGHETTI("ingredient_spaghetti"),
    SPINACH("ingredient_spinach"),
    STARCH("ingredient_starch"),
    STEAK("ingredient_steak"),
    STRAWBERRY("ingredient_strawberry"),
    SUGAR_BROWN("ingredient_sugar_brown"),
    SUGAR_WHITE("ingredient_sugar_white"),
    SWEET_POTATO("ingredient_sweet_potato"),
    TACO("ingredient_taco"),
    TOMATO("ingredient_tomato"),
    TORTILLA("ingredient_tortilla"),
    TURKEY_MEAT("ingredient_turkey_meat"),
    VANILLA("ingredient_vanilla"),
    VINEGAR("ingredient_vinegar"),
    WATERMELON("ingredient_watermelon"),
    WHEAT_FLOUR("ingredient_wheat_flour"),
    WINE("ingredient_wine"),
    YEAST("ingredient_yeast"),
    YOGURT("ingredient_yogurt")
}

enum class UtensilIcon(val resourceKey: String) {
    CLEAVER_BUTCHER("utensil_cleaver_butcher"),
    COOK_KNIFE("utensil_cook_knife"),
    FOOD_PROCESSOR("utensil_food_processor"),
    FOOD_SCALE("utensil_food_scale"),
    FORK("utensil_fork"),
    FREEZER("utensil_freezer"),
    FRIDGE("utensil_fridge"),
    FUNNEL("utensil_funnel"),
    GRATER("utensil_grater"),
    GRILL("utensil_grill"),
    HAND_MIXER("utensil_hand_mixer"),
    JUICER("utensil_juicer"),
    KETTLE("utensil_kettle"),
    LADLE("utensil_ladle"),
    MEASURING_CUP("utensil_measuring_cup"),
    MICROWAVE("utensil_microwave"),
    MIXING_BOWL("utensil_mixing_bowl"),
    OVEN("utensil_oven"),
    OVEN_GLOVE("utensil_oven_glove"),
    PAN("utensil_pan"),
    PEELER("utensil_peeler"),
    PRESSURE_COOKER("utensil_pressure_cooker"),
    RICE_COOKER("utensil_rice_cooker"),
    ROLLING_PIN("utensil_rolling_pin"),
    SAUCEPAN("utensil_saucepan"),
    SCRISSORS("utensil_scrissors"),
    SKIMMER("utensil_skimmer"),
    SPATULA("utensil_spatula"),
    STAND_MIXER("utensil_stand_mixer"),
    STOCK_POT("utensil_stock_pot"),
    STOVE("utensil_stove"),
    STRAINER("utensil_strainer"),
    TENDERIZER("utensil_tenderizer"),
    TOASTER("utensil_toaster"),
    TONGS("utensil_tongs"),
    WHISK("utensil_whisk"),
    WOK("utensil_wok")
}

@OptIn(ExperimentalResourceApi::class)
private fun getResource(key: String): DrawableResource =
    Res.allDrawableResources[key]
        ?: error("Drawable resource with key '$key' not found")

@Composable
fun IngredientIconView(
    icon: IngredientIcon,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(getResource(icon.resourceKey)),
        contentDescription = icon.name.lowercase().replace("_", " "),
        modifier = modifier
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun UtensilIconView(
    icon: UtensilIcon,
    modifier: Modifier = Modifier
) {
    Res.allDrawableResources.keys.forEach { key ->
        println("Resource key: $key")
    }
    Image(
        painter = painterResource(getResource(icon.resourceKey)),
        contentDescription = icon.name.lowercase().replace("_", " "),
        modifier = modifier
    )
}
