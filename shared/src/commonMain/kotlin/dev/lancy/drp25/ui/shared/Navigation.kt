package dev.lancy.drp25.ui.shared

import com.bumble.appyx.navigation.node.Node

/**
 * [NavTarget] is an interface that represents a target for navigation within the application.
 */
interface NavTarget

/**
 * [StaticNavTarget] is applied to any `sealed` or `enum` class where all possible navigation
 * targets are known. This is usually when a `sealed` class contains only `data object`s or in the
 * case of an `enum` class.
 */
interface StaticNavTarget : NavTarget {
    /**
     * The default target for this [StaticNavTarget]. This value should be overridden/provided once in the declaring
     * `sealed` or `enum` class.
     */
    val default: NavTarget

    /**
     * Enumeration of all possible targets defined by this `sealed` or `enum` class. Must respect
     * the order of declaration.
     */
    val entries: List<NavTarget>

    val defaultIndex: Int
        get() = entries.indexOf(default)
}

/**
 * [NavProvider] is an interface that provides navigation capabilities for a specific type of
 * [NavTarget].
 */
interface NavProvider<T : NavTarget> {
    /**
     * Within the current lifecycle scope, navigate to [target], and asynchronously returns an
     * explicit reference to the destination node with its own target type [C]. Chain these calls
     * to navigate through multiple nodes.
     */
    suspend fun <C : NavTarget> navigate(target: T): Node<C>

    suspend fun goBack() = Unit
}

/**
 * [NavConsumer] is an interface that represents a consumer of navigation capabilities, allowing
 * navigation to a specific [NavTarget] type [T] using a parent node of type [P].
 */
interface NavConsumer<T : NavTarget, P> where P : Node<T>, P : NavProvider<T> {
    /**
     * A reference to the parent [Node] of the current [Node]. Useful for when navigating beyond
     * one level.
     */
    val navParent: P

    /**
     * Within the current lifecycle scope, calls the parent to navigate to [target], and
     * asynchronously returns an explicit and typed reference to the destination node.
     */
    suspend fun <C : NavTarget> superNavigate(target: T): Node<C>
}

/**
 * [NavConsumerImpl] is a convenience implementation of [NavConsumer].
 */
open class NavConsumerImpl<T : NavTarget, P>(
    override val navParent: P,
) : NavConsumer<T, P> where P : Node<T>, P : NavProvider<T> {
    override suspend fun <C : NavTarget> superNavigate(target: T): Node<C> = navParent.navigate(target)
}
