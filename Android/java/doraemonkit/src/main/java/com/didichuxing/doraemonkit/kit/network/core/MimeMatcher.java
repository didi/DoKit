/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.didichuxing.doraemonkit.kit.network.core;

import android.annotation.SuppressLint;

import java.util.ArrayList;

public class MimeMatcher<T> {
  private final ArrayList<MimeMatcherRule> mRuleMap = new ArrayList<MimeMatcherRule>();

  public void addRule(String ruleExpression, T resultIfMatched) {
    mRuleMap.add(new MimeMatcherRule(ruleExpression, resultIfMatched));
  }

  public void clear() {
    mRuleMap.clear();
  }

  public T match(String mimeT) {
    int ruleMapN = mRuleMap.size();
    for (int i = 0; i < ruleMapN; i++) {
      MimeMatcherRule rule = mRuleMap.get(i);
      if (rule.match(mimeT)) {
        return rule.getResultIfMatched();
      }
    }
    return null;
  }

  @SuppressLint("BadMethodUse-java.lang.String.length")
  private class MimeMatcherRule {
    private final boolean mHasWildcard;
    private final String mMatchPrefix;
    private final T mResultIfMatched;

    public MimeMatcherRule(String ruleExpression, T resultIfMatched) {
      if (ruleExpression.endsWith("*")) {
        mHasWildcard = true;
        mMatchPrefix = ruleExpression.substring(0, ruleExpression.length() - 1);
      } else {
        mHasWildcard = false;
        mMatchPrefix = ruleExpression;
      }
      if (mMatchPrefix.contains("*")) {
        throw new IllegalArgumentException("Multiple wildcards present in rule expression " +
            ruleExpression);
      }
      mResultIfMatched = resultIfMatched;
    }

    public boolean match(String mimeType) {
      if (!mimeType.startsWith(mMatchPrefix)) {
        return false;
      }
      return (mHasWildcard || mimeType.length() == mMatchPrefix.length());
    }

    public T getResultIfMatched() {
      return mResultIfMatched;
    }
  }
}
