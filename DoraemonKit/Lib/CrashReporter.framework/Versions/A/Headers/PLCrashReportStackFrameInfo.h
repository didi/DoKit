/*
 * Author: Landon Fuller <landonf@plausible.coop>
 *
 * Copyright (c) 2008-2013 Plausible Labs Cooperative, Inc.
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
#import "PLCrashReportSymbolInfo.h"

@interface PLCrashReportStackFrameInfo : NSObject {
@private
    /** Frame instruction pointer. */
    uint64_t _instructionPointer;

    /** Symbol information, if available. Otherwise, will be nil. */
    PLCrashReportSymbolInfo *_symbolInfo;
}

- (id) initWithInstructionPointer: (uint64_t) instructionPointer symbolInfo: (PLCrashReportSymbolInfo *) symbolInfo;

/**
 * Frame's instruction pointer.
 */
@property(nonatomic, readonly) uint64_t instructionPointer;

/** Symbol information for this frame.
 * This may be unavailable, and this property will be nil. */
@property(nonatomic, readonly) PLCrashReportSymbolInfo *symbolInfo;

@end
