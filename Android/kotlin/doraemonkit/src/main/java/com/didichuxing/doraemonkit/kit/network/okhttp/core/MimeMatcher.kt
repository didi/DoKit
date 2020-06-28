/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.core

import android.annotation.SuppressLint
import java.util.*

class MimeMatcher<T> {
    private val mRuleMap = ArrayList<MimeMatcherRule>()
    fun addRule(ruleExpression: String?, resultIfMatched: T) {
        mRuleMap.add(MimeMatcherRule(ruleExpression!!, resultIfMatched))
    }

    fun clear() {
        mRuleMap.clear()
    }

    fun match(mimeT: String?): T? {
        val ruleMapN = mRuleMap.size
        for (i in 0 until ruleMapN) {
            val rule = mRuleMap[i]
            if (rule.match(mimeT!!)) {
                return rule.resultIfMatched
            }
        }
        return null
    }

    @SuppressLint("BadMethodUse-java.lang.String.length")
    private inner class MimeMatcherRule(ruleExpression: String, resultIfMatched: T) {
        private var mHasWildcard = false
        private lateinit var mMatchPrefix: String
        val resultIfMatched: T
        fun match(mimeType: String): Boolean {
            return if (!mimeType.startsWith(mMatchPrefix)) {
                false
            } else mHasWildcard || mimeType.length == mMatchPrefix.length
        }


        init {
            if (ruleExpression.endsWith("*")) {
                mHasWildcard = true
                mMatchPrefix = ruleExpression.substring(0, ruleExpression.length - 1)
            } else {
                mHasWildcard = false
                mMatchPrefix = ruleExpression
            }
            require(!mMatchPrefix.contains("*")) {
                "Multiple wildcards present in rule expression " +
                        ruleExpression
            }
            this.resultIfMatched = resultIfMatched
        }
    }
}