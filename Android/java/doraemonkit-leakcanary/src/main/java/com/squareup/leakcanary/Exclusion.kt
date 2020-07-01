package com.squareup.leakcanary

import com.squareup.leakcanary.ExcludedRefs.ParamsBuilder
import java.io.Serializable

class Exclusion internal constructor(builder: ParamsBuilder) : Serializable {
    @JvmField
    val name: String? = builder.name

    @JvmField
    val reason: String? = builder.reason

    @JvmField
    val alwaysExclude: Boolean = builder.alwaysExclude

    @JvmField
    val matching: String = builder.matching

}