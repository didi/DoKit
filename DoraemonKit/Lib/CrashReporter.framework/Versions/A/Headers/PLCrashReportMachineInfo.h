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

#import "PLCrashReportProcessorInfo.h"

@interface PLCrashReportMachineInfo : NSObject {
@private
    /** The hardware model name (eg, MacBookPro6,1). This may be unavailable, and this property will be nil. */
    NSString *_modelName;
    
    /** The processor type. */
    PLCrashReportProcessorInfo *_processorInfo;
    
    /* The number of actual physical processor cores. */
    NSUInteger _processorCount;
    
    /* The number of logical processors. */
    NSUInteger _logicalProcessorCount;
}

- (id) initWithModelName: (NSString *) modelName
           processorInfo: (PLCrashReportProcessorInfo *) processorInfo
          processorCount: (NSUInteger) processorCount
   logicalProcessorCount: (NSUInteger) logicalProcessorCount;

/** The hardware model name (eg, MacBookPro6,1). This may be unavailable, and this property will be nil. */
@property(nonatomic, readonly) NSString *modelName;

/** The processor type. This will be unavailable in reports generated prior to PLCrashReporter 1.2, in which case this property will be nil. */
@property(nonatomic, readonly) PLCrashReportProcessorInfo *processorInfo;

/*
 * The number of actual physical processor cores. Note that the number of active processors may be managed by the
 * operating system's power management system, and this value may not reflect the number of active
 * processors at the time of the crash.
 */
@property(nonatomic, readonly) NSUInteger processorCount;

/*
 * The number of logical processors.  Note that the number of active processors may be managed by the
 * operating system's power management system, and this value may not reflect the number of active
 * processors at the time of the crash.
 */
@property(nonatomic, readonly) NSUInteger logicalProcessorCount;

@end
