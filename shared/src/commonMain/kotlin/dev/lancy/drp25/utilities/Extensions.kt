package dev.lancy.drp25.utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.spotlight.Spotlight
import dev.lancy.drp25.ui.shared.NavTarget
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun TextStyle.height(): Dp {
    val density = LocalDensity.current
    val lineHeight = lineHeight.takeIf { it.isSpecified } ?: fontSize
    return with(density) { lineHeight.toDp() }
}

fun <T> ClosedFloatingPointRange<T>.intSteps(): Int where T : Comparable<T>, T : Number =
    endInclusive.toDouble().toInt() - start.toDouble().toInt() - 1

fun <T : NavTarget> BackStack<T>.currentTarget(): T =
    this.model.output.value.currentTargetState.active.interactionTarget

@Composable
fun <T : NavTarget> Spotlight<T>.selectedIndex(): Int =
    this.activeIndex
        .collectAsState()
        .value
        .toInt()

fun ClosedFloatingPointRange<Float>.toIntString(): String = if (ceil(start).toInt() <= floor(endInclusive).toInt()) {
    (ceil(start).toInt()..floor(endInclusive).toInt()).joinToString(", ", "(", ")")
} else {
    "()"
}

object ClosedFloatRangeSerializer : KSerializer<ClosedFloatingPointRange<Float>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ClosedFloatingPointRange") {
        element<Float>("start")
        element<Float>("endInclusive")
    }

    override fun serialize(encoder: Encoder, value: ClosedFloatingPointRange<Float>) {
        encoder.encodeStructure(descriptor) {
            encodeFloatElement(descriptor, 0, value.start)
            encodeFloatElement(descriptor, 1, value.endInclusive)
        }
    }

    override fun deserialize(decoder: Decoder): ClosedFloatingPointRange<Float> {
        return decoder.decodeStructure(descriptor) {
            var start: Float? = null
            var endInclusive: Float? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> {
                        start = decodeFloatElement(descriptor, 0)
                    }
                    1 -> {
                        endInclusive = decodeFloatElement(descriptor, 1)
                    }
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            requireNotNull(start) { "Missing start value" }
            requireNotNull(endInclusive) { "Missing endInclusive value" }

            val result = start..endInclusive
            result
        }
    }
}

fun <T> identity(it: T): T = it