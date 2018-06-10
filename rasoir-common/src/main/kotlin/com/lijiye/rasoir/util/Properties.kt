package com.lijiye.rasoir.util

import com.google.common.base.Preconditions.checkArgument
import org.apache.commons.configuration2.BaseConfiguration
import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.builder.fluent.Configurations
import java.io.File


class Properties(other: Properties? = null) : BaseConfiguration() {
    companion object {
        const val IMMUTABLE_KEY = "IMMUTABLE"
    }

    private val immutableKeys: MutableSet<String> = mutableSetOf()

    init {
        if (other != null) {
            this.immutableKeys.addAll(other.immutableKeys)
            other.keys.forEach { key -> this.addProperty(key, other.getProperty(key)) }
        }
    }

    fun load(file: File) {
        checkArgument(file.exists(), "The file ${file.absolutePath} is not exist.")
        checkArgument(file.isFile, "The file ${file.absolutePath} is a directory.")
        checkArgument(file.canRead(), "The file ${file.absolutePath} can not be read.")
        val other = Configurations().properties(file)
        this.addAll(other)
    }

    fun addAll(other: Properties) {
        other.keys.forEach { key ->
            if (!immutableKeys.contains(key)) {
                this.addProperty(key, other.getProperty(key))
            }
        }
        this.immutableKeys.addAll(other.immutableKeys)
    }

    private fun addAll(other: Configuration) {
        other.keys.forEach { it ->
            val key = if (it.startsWith(IMMUTABLE_KEY)) {
                val tmp = it.substring(IMMUTABLE_KEY.length)
                this.immutableKeys.add(tmp)
                tmp
            } else {
                it
            }
            if (!this.immutableKeys.contains(key)) {
                val value = other.getProperty(it)
                this.addProperty(key, value)
            }
        }
    }

    fun addProperty(key: String, value: Any, isImmutable: Boolean = false) {
        if (!immutableKeys.contains(key)) {
            addProperty(key, value)
        }
        if (isImmutable) {
            immutableKeys.add(key)
        }
    }
}


