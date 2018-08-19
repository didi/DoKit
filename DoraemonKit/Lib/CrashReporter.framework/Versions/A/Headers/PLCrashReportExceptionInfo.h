/*
 * Author: Landon Fuller <landonf@plausiblelabs.com>
 *
 * Copyright (c) 2008-2009 Plausible Labs Cooperative, Inc.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

#import <Foundation/Foundation.h>
#import "PLCrashReportThreadInfo.h"


@interface PLCrashReportExceptionInfo : NSObject {
@private
    /** Name */
    NSString *_name;

    /** Reason */
    NSString *_reason;

    /** Ordered list of PLCrashReportStackFrame instances, or nil if unavailable. */
    NSArray *_stackFrames;
}

- (id) initWithExceptionName: (NSString *) name reason: (NSString *) reason;

- (id) initWithExceptionName: (NSString *) name 
                      reason: (NSString *) reason
                 stackFrames: (NSArray *) stackFrames;

/**
 * The exception name.
 */
@property(nonatomic, readonly) NSString *exceptionName;

/**
 * The exception reason.
 */
@property(nonatomic, readonly) NSString *exceptionReason;

/* The exception's original call stack, as an array of PLCrashReportStackFrameInfo instances, or nil if unavailable.
 * This may be preserved across rethrow of an exception, and can be used to determine the original call stack. */
@property(nonatomic, readonly) NSArray *stackFrames;

@end
