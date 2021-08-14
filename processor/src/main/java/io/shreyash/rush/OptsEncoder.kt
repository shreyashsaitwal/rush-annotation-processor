package io.shreyash.rush

import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.util.*

fun main(args: Array<String>) {
    val entries = args[0].split(";").toTypedArray()

    val os = ByteArrayOutputStream()
    val oos = ObjectOutputStream(os)
    oos.writeInt(entries.size)

    for (entry in entries) {
        val keyVal = entry.split("=").toTypedArray()
        oos.writeUTF(keyVal[0])
        oos.writeUTF(keyVal[1])
    }

    oos.flush()
    println(Base64.getEncoder().encodeToString(os.toByteArray()))
}
