/*
 * Author: Landon Fuller <landonf@plausiblelabs.com>
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

#import "PLCrashReportApplicationInfo.h"
#import "PLCrashReportBinaryImageInfo.h"
#import "PLCrashReportExceptionInfo.h"
#import "PLCrashReportMachineInfo.h"
#import "PLCrashReportMachExceptionInfo.h"
#import "PLCrashReportProcessInfo.h"
#import "PLCrashReportProcessorInfo.h"
#import "PLCrashReportRegisterInfo.h"
#import "PLCrashReportSignalInfo.h"
#import "PLCrashReportStackFrameInfo.h"
#import "PLCrashReportSymbolInfo.h"
#import "PLCrashReportSystemInfo.h"
#import "PLCrashReportThreadInfo.h"

/** 
 * @ingroup constants
 * Crash file magic identifier */
#define PLCRASH_REPORT_FILE_MAGIC "plcrash"

/** 
 * @ingroup constants
 * Crash format version byte identifier. Will not change outside of the introduction of
 * an entirely new crash log format. */
#define PLCRASH_REPORT_FILE_VERSION 1

/**
 * @ingroup types
 * Crash log file header format.
 *
 * Crash log files start with 7 byte magic identifier (#PLCRASH_REPORT_FILE_MAGIC),
 * followed by a single unsigned byte version number (#PLCRASH_REPORT_FILE_VERSION).
 * The crash log message format itself is extensible, so this version number will only
 * be incremented in the event of an incompatible encoding or format change.
 */
struct PLCrashReportFileHeader {
    /** Crash log magic identifier, not NULL terminated */
    const char magic[7];

    /** Crash log encoding/format version */
    const uint8_t version;

    /** File data */
    const uint8_t data[];
} __attribute__((packed));


/**
 * @internal
 * Private decoder instance variables (used to hide the underlying protobuf parser).
 */
typedef struct _PLCrashReportDecoder _PLCrashReportDecoder;

@interface PLCrashReport : NSObject {
@private
    /** Private implementation variables (used to hide the underlying protobuf parser) */
    _PLCrashReportDecoder *_decoder;

    /** System info */
    PLCrashReportSystemInfo *_systemInfo;
    
    /** Machine info */
    PLCrashReportMachineInfo *_machineInfo;

    /** Application info */
    PLCrashReportApplicationInfo *_applicationInfo;
    
    /** Process info */
    PLCrashReportProcessInfo *_processInfo;

    /** Signal info */
    PLCrashReportSignalInfo *_signalInfo;
    
    /** Mach exception info */
    PLCrashReportMachExceptionInfo *_machExceptionInfo;

    /** Thread info (PLCrashReportThreadInfo instances) */
    NSArray *_threads;

    /** Binary images (PLCrashReportBinaryImageInfo instances */
    NSArray *_images;

    /** Exception information (may be nil) */
    PLCrashReportExceptionInfo *_exceptionInfo;

    /** Report UUID */
    CFUUIDRef _uuid;
}

- (id) initWithData: (NSData *) encodedData error: (NSError **) outError;

- (PLCrashReportBinaryImageInfo *) imageForAddress: (uint64_t) address;

/**
 * System information.
 */
@property(nonatomic, readonly) PLCrashReportSystemInfo *systemInfo;

/**
 * YES if machine information is available.
 */
@property(nonatomic, readonly) BOOL hasMachineInfo;

/**
 * Machine information. Only available in later (v1.1+) crash report format versions. If not available,
 * will be nil.
 */
@property(nonatomic, readonly) PLCrashReportMachineInfo *machineInfo;

/**
 * Application information.
 */
@property(nonatomic, readonly) PLCrashReportApplicationInfo *applicationInfo;

/**
 * YES if process information is available.
 */
@property(nonatomic, readonly) BOOL hasProcessInfo;

/**
 * Process information. Only available in later (v1.1+) crash report format versions. If not available,
 * will be nil.
 */
@property(nonatomic, readonly) PLCrashReportProcessInfo *processInfo;

/**
 * Signal information. This provides the signal and signal code of the fatal signal.
 */
@property(nonatomic, readonly) PLCrashReportSignalInfo *signalInfo;

/**
 * Mach exception information, if available. This will only be included in the
 * case that encoding crash reporter's exception-based reporting was enabled, and a Mach
 * exception was caught.
 *
 * @warning If Mach exception information is available, the legacy signalInfo property will also be provided; this
 * s required to maintain backwards compatibility with the established API. Note, however, that the signal info may be derived from the
 * Mach exception info by the encoding crash reporter, and thus may not exactly match the kernel exception-to-signal
 * mappings implemented in xnu. As such, when Mach exception info is available, its use should be preferred.
 */
@property(nonatomic, readonly) PLCrashReportMachExceptionInfo *machExceptionInfo;

/**
 * Thread information. Returns a list of PLCrashReportThreadInfo instances.
 */
@property(nonatomic, readonly) NSArray *threads;

/**
 * Binary image information. Returns a list of PLCrashReportBinaryImageInfo instances.
 */
@property(nonatomic, readonly) NSArray *images;

/**
 * YES if exception information is available.
 */
@property(nonatomic, readonly) BOOL hasExceptionInfo;

/**
 * Exception information. Only available if a crash was caused by an uncaught exception,
 * otherwise nil.
 */
@property(nonatomic, readonly) PLCrashReportExceptionInfo *exceptionInfo;

/**
 * A client-generated 16-byte UUID. May be used to filter duplicate reports submitted or generated
 * by a single client. Only available in later (v1.2+) crash report format versions. If not available,
 * will be NULL.
 */
@property(nonatomic, readonly) CFUUIDRef uuidRef;

@end
