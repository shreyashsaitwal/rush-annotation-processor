package io.shreyash.rush

import io.shreyash.rush.block.DesignerProperty
import io.shreyash.rush.block.Event
import io.shreyash.rush.block.Method
import io.shreyash.rush.block.Property

/**
 * Singleton class to stores all the parsed blocks.
 */
class BlockStore {
    val events = mutableListOf<Event>()
    val methods = mutableListOf<Method>()
    val properties = mutableListOf<Property>()
    val designerProperties = mutableListOf<DesignerProperty>()

    companion object {
        val instance = BlockStore()
    }

    fun putEvent(event: Event) = events.add(event)

    fun putMethod(method: Method) = methods.add(method)

    fun putProperty(property: Property) = properties.add(property)

    fun putDesignerProperty(designerProperty: DesignerProperty) =
        designerProperties.add(designerProperty)
}
