/*
 * Author: Damian Morris <damian@moso.com.au>
 *
 * Copyright (c) 2010 MOSO Corporation, Pty Ltd.
 * Copyright (c) 2010-2013 Plausible Labs Cooperative, Inc.
 *
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

@interface PLCrashReportProcessInfo : NSObject {
@private
    /** Process name, or nil if unavailable. */
    NSString *_processName;
    
    /** Process ID */
    NSUInteger _processID;
    
    /** Process path */
    NSString* _processPath;

    /** Date and time that the crashing process was started. This may be unavailable, and this property
     * will be nil. */
    NSDate *_processStartTime;

    /** Parent process name, or nil if unavailable.  */
    NSString *_parentProcessName;
    
    /** Parent process ID */
    NSUInteger _parentProcessID;
    
    /** If false, the process is being run via process-level CPU emulation (such as Rosetta). */
    BOOL _native;
}

- (id) initWithProcessName: (NSString *) processName
                 processID: (NSUInteger) processID
               processPath: (NSString *) processPath
          processStartTime: (NSDate *) processStartTime
         parentProcessName: (NSString *) parentProcessName
           parentProcessID: (NSUInteger) parentProcessID
                    native: (BOOL) native;

/**
 * The process name. This value may not be included in the crash report, in which case this property
 * will be nil.
 */
@property(nonatomic, readonly) NSString *processName;

/**
 * The process ID.
 */
@property(nonatomic, readonly) NSUInteger processID;

/**
 * The path to the process executable. This value may not be included in the crash report, in which case this property
 * will be nil.
 */
@property(nonatomic, readonly) NSString *processPath;

/**
 * Date and time that the crashing process was started. This value may not be included in the crash report, in which case this property
 * will be nil.
 */
@property(nonatomic, readonly) NSDate *processStartTime;

/**
 * The parent process name. This value may not be included in the crash report, in which case this property
 * will be nil.
 */
@property(nonatomic, readonly) NSString *parentProcessName;

/**
 * The parent process ID.
 */
@property(nonatomic, readonly) NSUInteger parentProcessID;

/** The process' native execution status. If false, the process is being run via process-level CPU emulation (such as Rosetta). */
@property(nonatomic, readonly) BOOL native;

@end
