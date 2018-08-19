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
#import <mach/machine.h>

/**
 * @ingroup constants
 *
 * The type encodings supported for CPU types and subtypes. Currently only Apple
 * Mach-O defined encodings are supported.
 *
 * @internal
 * These enum values match the protobuf values. Keep them synchronized.
 */
typedef enum {
    /** Unknown cpu type encoding. */
    PLCrashReportProcessorTypeEncodingUnknown = 0,

    /** Apple Mach-defined processor types. */
    PLCrashReportProcessorTypeEncodingMach = 1
} PLCrashReportProcessorTypeEncoding;

@interface PLCrashReportProcessorInfo : NSObject {
@private
    /** Type encoding */
    PLCrashReportProcessorTypeEncoding _typeEncoding;

    /** CPU type */
    uint64_t _type;

    /** CPU subtype */
    uint64_t _subtype;
}

- (id) initWithTypeEncoding: (PLCrashReportProcessorTypeEncoding) typeEncoding
                       type: (uint64_t) type
                    subtype: (uint64_t) subtype;

/** The CPU type encoding. */
@property(nonatomic, readonly) PLCrashReportProcessorTypeEncoding typeEncoding;

/** The CPU type. */
@property(nonatomic, readonly) uint64_t type;

/** The CPU subtype. */
@property(nonatomic, readonly) uint64_t subtype;

@end
