package io.shreyash.rush

/**
 * Annotation to rename parameters of blocks.
 *
 * When used, @Rename(name = "foo"), the parameter name
 * of the block gets changed
 *  `public void Foo(@Rename("ahaha") String bar) { ... }`
 *
 *
 */
annotation class Rename (
    val name: String = "",
)
