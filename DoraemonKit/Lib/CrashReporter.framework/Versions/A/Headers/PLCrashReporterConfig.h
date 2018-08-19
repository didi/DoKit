/*
 * Author: Landon Fuller <landonf@plausible.coop>
 *
 * Copyright (c) 2013 Plausible Labs Cooperative, Inc.
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
#import "PLCrashFeatureConfig.h"

/**
 * @ingroup enums
 * Supported mechanisms for trapping and handling crashes.
 */
typedef NS_ENUM(NSUInteger, PLCrashReporterSignalHandlerType) {
    /**
     * Trap fatal signals via a sigaction(2)-registered BSD signal handler.
     *
     * PLCrashReporter's signal handler will supersede previously registered handlers; existing
     * handlers will not be called. This behavior may be modified in a future release, and should
     * not be relied upon as a mechanism to prevent existing signal handlers from being called.
     *
     * There are some limitations to signal-based crash handling on Mac OS X and iOS; specifically:
     *
     * - On Mac OS X, stack overflows will only be handled on the thread on which
     *   the crash reporter was initialized. This should generally be the main thread.
     * - On iOS 6.0 and later, any stack overflows will not be handled due to sigaltstack() being
     *   non-functional on the device. (see rdar://13002712 - SA_ONSTACK/sigaltstack() ignored on iOS).
     * - Some exit paths in Apple's Libc will deregister a signal handler before firing SIGABRT, resulting
     *   in the signal handler never being called (see rdar://14313497 - ___abort() disables SIGABRT signal
     *   handlers prior to raising SIGABRT).  These __abort()-based checks are:
     *     - Implemented for unsafe memcpy/strcpy/snprintf C functions.
     *     - Only enabled when operating on a fixed-width target buffer (in which case the
     *       compiler rewrites the function calls to the built-in variants, and provides the fixed-width length as an argument).
     *     - Only trigger in the case that the source data exceeds the size of the fixed width target
     *       buffer, and the maximum length argument either isn't supplied by the caller (eg, when using strcpy),
     *       or a too-long argument is supplied (eg, strncpy with a length argument longer than the target buffer),
     *       AND that argument can't be checked at compile-time.
     */
    PLCrashReporterSignalHandlerTypeBSD = 0,

#if PLCRASH_FEATURE_MACH_EXCEPTIONS
    /**
     * Trap fatal signals via a Mach exception server.
     *
     * If any existing Mach exception server has been registered for the task, exceptions will be forwarded to that
     * exception handler. Should the exceptions be handled by an existing handler, no report will be generated
     * by PLCrashReporter.
     *
     * @par Mac OS X
     *
     * On Mac OS X, the Mach exception implementation is fully supported, using publicly available API -- note,
     * however, that some kernel-internal constants, as well as architecture-specific trap information,
     * may be required to fully interpret a Mach exception's root cause.
     *
     * @par iOS
     *
     * On iOS, the APIs required for a complete implementation are not fully public.
     *
     * The exposed surface of undocumented API usage is relatively low, and there has been strong user demand to
     * implement Mach exception handling regardless of concerns over API visiblity. Given this, we've included
     * Mach exception handling as an optional feature, with both build-time and runtime configuration
     * to disable its inclusion or use, respectively.
     *
     * @par Debugger Incompatibility
     *
     * The Mach exception handler executes in-process, and will interfere with debuggers when they attempt to
     * suspend all active threads (which will include the Mach exception handler). Mach-based handling
     * should not be used when a debugger is attached.
     *
     * @par More Details
     *
     * For more information, refer to @ref mach_exceptions.
     */
    PLCrashReporterSignalHandlerTypeMach = 1
#endif /* PLCRASH_FEATURE_MACH_EXCEPTIONS */
};

/**
 * @ingroup enums
 * Supported mechanisms for performing local symbolication.
 *
 * Local symbolication is performed using inexact heuristics and symbol data available at runtime; it may
 * return information that is incorrect. This may still be useful in the case where DWARF data is unavailable
 * for a given build; in that case, it can provide function and method names (though not line numbers) for a
 * crash report that may otherwise be unusable.
 *
 * Note, however, this comes at the cost of a significant increase in code that must run within the critical
 * crash reporting section, where failures may result in crash reports being corrupted or left unwritten. In
 * addition, some of the provided symbolication strategies rely on knowledge of runtime internals that may
 * change in future iOS releases. Given that DWARF symbolication data will <em>always</em> be more accurate, and
 * the risks inherent in executing considerably more code at crash time, it is strongly recommended that local
 * symbolication only be enabled for non-release builds.
 *
 * Multiple symbolication strategies may be enabled, in which case a best-match heuristic will be applied to the
 * results.
 */
typedef NS_OPTIONS(NSUInteger, PLCrashReporterSymbolicationStrategy) {
    /** No symbolication. */
    PLCrashReporterSymbolicationStrategyNone = 0,

    /**
     * Use the standard binary symbol table. On iOS, this alone will return
     * incomplete results, as most symbols are rewritten to the common '\<redacted>' string.
     */
    PLCrashReporterSymbolicationStrategySymbolTable = 1 << 0,

    /**
     * Use Objective-C metadata to find method and class names. This relies on detailed parsing
     * of the Objective-C runtime data, including undefined flags and other runtime internals. As such,
     * it may return incorrect data should the runtime be changed incompatibly.
     */
    PLCrashReporterSymbolicationStrategyObjC = 1 << 1,
    
    /**
     * Enable all available symbolication strategies.
     */
    PLCrashReporterSymbolicationStrategyAll = (PLCrashReporterSymbolicationStrategySymbolTable|PLCrashReporterSymbolicationStrategyObjC)
};

@interface PLCrashReporterConfig : NSObject {
@private
    /** The configured signal handler type. */
    PLCrashReporterSignalHandlerType _signalHandlerType;
    
    /** The configured symbolication strategy. */
    PLCrashReporterSymbolicationStrategy _symbolicationStrategy;
}

+ (instancetype) defaultConfiguration;

- (instancetype) init;
- (instancetype) initWithSignalHandlerType: (PLCrashReporterSignalHandlerType) signalHandlerType
                     symbolicationStrategy: (PLCrashReporterSymbolicationStrategy) symbolicationStrategy;

/** The configured signal handler type. */
@property(nonatomic, readonly) PLCrashReporterSignalHandlerType signalHandlerType;

/** The configured symbolication strategy. */
@property(nonatomic, readonly) PLCrashReporterSymbolicationStrategy symbolicationStrategy;


@end

