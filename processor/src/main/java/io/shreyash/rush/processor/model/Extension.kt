package io.shreyash.rush.processor.model

import com.google.appinventor.components.annotations.Meta
import io.shreyash.rush.processor.block.DesignerProperty
import io.shreyash.rush.processor.block.Event
import io.shreyash.rush.processor.block.Function
import io.shreyash.rush.processor.block.Property

data class Extension(
    val meta: Meta,
    val fqcn: String,
    val events: List<Event>,
    val functions: List<Function>,
    val properties: List<Property>,
    val designerProperties: List<DesignerProperty>,
)
