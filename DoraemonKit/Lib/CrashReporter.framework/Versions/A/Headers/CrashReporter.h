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

#ifdef __APPLE__
#import <AvailabilityMacros.h>
#endif

// This must be included before any other PLCrashReporter includes, as
// it redefines symbol names
#import "PLCrashNamespace.h"

#import "PLCrashReporter.h"
#import "PLCrashReport.h"
#import "PLCrashReportTextFormatter.h"

/**
 * @defgroup functions Crash Reporter Functions Reference
 */

/**
 * @defgroup types Crash Reporter Data Types Reference
 */

/**
 * @defgroup constants Crash Reporter Constants Reference
 */

/**
 * @internal
 * @defgroup plcrash_internal Crash Reporter Internal Documentation
 */

/**
 * @defgroup enums Enumerations
 * @ingroup constants
 */

/**
 * @defgroup globals Global Variables
 * @ingroup constants
 */

/**
 * @defgroup exceptions Exceptions
 * @ingroup constants
 */

/* Exceptions */
extern NSString *PLCrashReporterException;

/* Error Domain and Codes */
extern NSString *PLCrashReporterErrorDomain;

/**
 * NSError codes in the Plausible Crash Reporter error domain.
 * @ingroup enums
 */
typedef enum {
    /** An unknown error has occured. If this
     * code is received, it is a bug, and should be reported. */
    PLCrashReporterErrorUnknown = 0,
    
    /** An Mach or POSIX operating system error has occured. The underlying NSError cause may be fetched from the userInfo
     * dictionary using the NSUnderlyingErrorKey key. */
    PLCrashReporterErrorOperatingSystem = 1,

    /** The crash report log file is corrupt or invalid */
    PLCrashReporterErrorCrashReportInvalid = 2,

    /** An attempt to use a resource which was in use at the time in a manner which would have conflicted with the request. */
    PLCrashReporterErrorResourceBusy = 3,
    
    /** The requested resource could not be found. */
    PLCRashReporterErrorNotFound = 4,
    
    /** Allocation failed. */
    PLCRashReporterErrorInsufficientMemory = 4
} PLCrashReporterError;


/* Library Imports */
#import "PLCrashReporter.h"
#import "PLCrashReport.h"
#import "PLCrashReportTextFormatter.h"

/**
 * @mainpage Plausible Crash Reporter
 *
 * @section intro_sec Introduction
 *
 * Plausile CrashReporter implements in-process crash reporting on the iPhone and Mac OS X.
 *
 * The following features are supported:
 *
 * - Implemented as an in-process signal handler.
 * - Does not interfer with debugging in gdb..
 * - Handles both uncaught Objective-C exceptions and fatal signals (SIGSEGV, SIGBUS, etc).
 * - Full thread state for all active threads (backtraces, register dumps) is provided.
 *
 * If your application crashes, a crash report will be written. When the application is next run, you may check for a
 * pending crash report, and submit the report to your own HTTP server, send an e-mail, or even introspect the
 * report locally.
 *
 * @section intro_encoding Crash Report Format
 *
 * Crash logs are encoded using <a href="http://code.google.com/p/protobuf/">google protobuf</a>, and may be decoded
 * using the provided PLCrashReport API. Additionally, the include plcrashutil handles conversion of binary crash reports to the 
 * symbolicate-compatible iPhone text format.
 *
 * @section doc_sections Documentation Sections
 * - @subpage example_usage_iphone
 * - @subpage error_handling
 * - @subpage async_safety
 */

/**
 * @page example_usage_iphone Example iPhone Usage
 *
 * @code
 * //
 * // Called to handle a pending crash report.
 * //
 * - (void) handleCrashReport {
 *     PLCrashReporter *crashReporter = [PLCrashReporter sharedReporter];
 *     NSData *crashData;
 *     NSError *error;
 *     
 *     // Try loading the crash report
 *     crashData = [crashReporter loadPendingCrashReportDataAndReturnError: &error];
 *     if (crashData == nil) {
 *         NSLog(@"Could not load crash report: %@", error);
 *         goto finish;
 *     }
 *     
 *     // We could send the report from here, but we'll just print out
 *     // some debugging info instead
 *     PLCrashReport *report = [[[PLCrashReport alloc] initWithData: crashData error: &error] autorelease];
 *     if (report == nil) {
 *         NSLog(@"Could not parse crash report");
 *         goto finish;
 *     }
 *     
 *     NSLog(@"Crashed on %@", report.systemInfo.timestamp);
 *     NSLog(@"Crashed with signal %@ (code %@, address=0x%" PRIx64 ")", report.signalInfo.name,
 *           report.signalInfo.code, report.signalInfo.address);
 *     
 *     // Purge the report
 * finish:
 *     [crashReporter purgePendingCrashReport];
 *     return;
 * }
 * 
 * // from UIApplicationDelegate protocol
 * - (void) applicationDidFinishLaunching: (UIApplication *) application {
 *     PLCrashReporter *crashReporter = [PLCrashReporter sharedReporter];
 *     NSError *error;
 *     
 *     // Check if we previously crashed
 *     if ([crashReporter hasPendingCrashReport])
 *         [self handleCrashReport];
    
 *     // Enable the Crash Reporter
 *     if (![crashReporter enableCrashReporterAndReturnError: &error])
 *         NSLog(@"Warning: Could not enable crash reporter: %@", error);
 *         
 * }
 * @endcode
 * 
 */

/**
 * @page error_handling Error Handling Programming Guide
 *
 * Where a method may return an error, Plausible Crash Reporter provides access to the underlying
 * cause via an optional NSError argument.
 *
 * All returned errors will be a member of one of the below defined domains, however, new domains and
 * error codes may be added at any time. If you do not wish to report on the error cause, many methods
 * support a simple form that requires no NSError argument.
 *
 * @section error_domains Error Domains, Codes, and User Info
 *
 * @subsection crashreporter_errors Crash Reporter Errors
 *
 * Any errors in Plausible Crash Reporter use the #PLCrashReporterErrorDomain error domain, and and one
 * of the error codes defined in #PLCrashReporterError.
 */

/**
 * @page async_safety Async-Safe Programming Guide
 *
 * Plausible CrashReporter provides support for executing an application specified function in the context of the
 * crash reporter's signal handler, after the crash report has been written to disk. This was a regularly requested
 * feature, and provides the ability to implement application finalization in the event of a crash. However, writing
 * code intended for execution inside of a signal handler is exceptionally difficult, and is not recommended.
 *
 * @section program_flow Program Flow and Signal Handlers
 *
 * When the signal handler is called the normal flow of the program is interrupted, and your program is an unknown
 * state. Locks may be held, the heap may be corrupt (or in the process of being updated), and your signal
 * handler may invoke a function that was being executed at the time of the signal. This may result in deadlocks,
 * data corruption, and program termination.
 *
 * @section functions Async-Safe Functions
 *
 * A subset of functions are defined to be async-safe by the OS, and are safely callable from within a signal handler. If
 * you do implement a custom post-crash handler, it must be async-safe. A table of POSIX-defined async-safe functions
 * and additional information is available from the
 * <a href="https://www.securecoding.cert.org/confluence/display/seccode/SIG30-C.+Call+only+asynchronous-safe+functions+within+signal+handlers">CERT programming guide - SIG30-C</a>
 *
 * Most notably, the Objective-C runtime itself is not async-safe, and Objective-C may not be used within a signal
 * handler.
 *
 * @sa PLCrashReporter::setCrashCallbacks:
 */

/**
 * @page mach_exceptions Mach Exceptions on Mac OS X and iOS
 *
 * PLCrashReporter includes support for monitoring crashes via an in-process Mach exception handler. There are a small
 * number of crash cases that will not be caught with via a POSIX signal handler, but can be caught via a Mach
 * exception handler:
 *
 * - Stack overflow. sigaltstack() is broken in later iOS releases, and even if functional, must be configured
 *   on a per-thread basis.
 * - Internal Apple assertions that call libSystem's __assert. These include compiler-checked constraints
 *   for built-in functions, such as strcpy_chk(). The __abort() implementation actually disables the SIGABRT
 *   signal handler (resetting it to SIG_DFL) prior to to issueing a SIGABRT, bypassing signal-based crash
 *   reporters entirely.
 *
 * Unfortunately, the latter issue (__assert) can not be handled on iOS; trapping abort requires that
 * a Mach exception handler operate out-of-process, which is impossible on iOS. On Mac OS X, this will
 * only be handled once we've implemented fully out-of-process crash excution.
 *
 * On Mac OS X, the Mach exception implementation is fully supported using entirely public API. On iOS,
 * the APIs required are not fully public -- more details on the implications of this for exception handling on
 * iOS may be found in @ref mach_exceptions_ios below. It is worth noting that even where the Mach exception APIs
 * are fully supported, kernel-internal constants, as well
 * as architecture-specific trap information, may be required to fully interpret a Mach exception's root cause.
 *
 * For example, the EXC_SOFTWARE exception is dispatched for four different failure types, using the exception
 * code to differentiate failure types:
 *   - Non-existent system call invoked (SIGSYS)
 *   - Write on a pipe with no reader (SIGPIPE)
 *   - Abort program (SIGABRT -- unused)
 *   - Kill program (SIGKILL)
 *
 * Of those four types, only the constant required to interpret the SIGKILL behavior (EXC_SOFT_SIGNAL) is publicly defined.
 * Of the remaining three failure types, the constant values are kernel implementation-private, defined only in the available
 * kernel sources. On iOS, these sources are unavailable, and while they generally do match the Mac OS X implementation, there
 * are no gaurantees that this is -- or will remain -- the case in the future.
 *
 * Likewise, interpretation of particular fault types requires information regarding the underlying machine traps
 * that triggered the Mach exceptions. For example, a floating point trap on x86/x86-64 will trigger an EXC_ARITHMETIC,
 * with a subcode value containing the value of the FPU status register. Determining the exact FPU cause requires
 * extracting the actual exception flags from status register as per the x86 architecture documentation. The exact format
 * of this subcode value is not actually documented outside the kernel, and may change in future releases.
 *
 * While we have the advantage of access to the x86 kernel sources, the situation on ARM is even less clear. The actual
 * use of the Mach exception codes and subcodes is largely undefined by both headers and publicly available documentation,
 * and the available x86 kernel sources are of little use in interpreting this data.
 *
 * As such, while Mach exceptions may catch some cases that BSD signals can not, they are not a perfect solution,
 * and may also provide less insight into the actual failures that occur. By comparison, the BSD signal interface
 * is both fully defined and architecture independent, with any necessary interpretation of the Mach exception
 * codes handled in-kernel at the time of exception dispatch. It is generally recommended by Apple as the preferred
 * interface, and should generally be preferred by PLCrashReporter API clients.
 *
 * @section mach_exceptions_compatibility Compatibility Issues
 *
 * @subsection Debuggers
 *
 * Enabling in-process Mach exception handlers will conflict with any attached debuggers; the debugger
 * may suspend the processes Mach exception handling thread, which will result in any exception messages
 * sent via the debugger being lost, as the in-process handler will be unable to receive and forward
 * the messages.
 *
 * @subsection Managed Runtimes (Xamarin, Unity)
 *
 * A Mach exception handler may conflict with any managed runtime that registers a BSD signal handler that
 * can safely handle otherwise fatal signals, allowing execution to proceed. This includes products
 * such as Xamarin for iOS.
 *
 * In such a case, PLCrashReporter will write a crash report for non-fatal signals, as there is no
 * immediate mechanism for determining whether a signal handler exists and that it can safely
 * handle the failure. This can result in unexpected delays in application execution, increased I/O to
 * disk, and other undesirable operations.
 *
 * @section mach_exceptions_ios Mach Exceptions on iOS
 *
 * The APIs required for Mach exception handling are not fully public on iOS. After filing a request with
 * Apple DTS to clarify the status of the Mach exception APIs on iOS, and implementing a Mach Exception
 * handler using only supported API, they provided the following guidance:
 *
 *    <em>Our engineers have reviewed your request and have determined that this would be best handled as a bug report,
 *    which you have already filed. There is no documented way of accomplishing this, nor is there a workaround
 *    possible.</em>
 *
 * Due to user request, PLCrashReporter provides an optional implementation of Mach exception handling for both
 * iOS and Mac OS X.
 *
 * This implementation uses only supported API on Mac OS X, and depends on limited undefined API on iOS. The reporter
 * may be excluded entirely at build time by modifying the PLCRASH_FEATURE_MACH_EXCEPTIONS build configuration; it
 * may also be disabled at runtime by configuring the PLCrashReporter instance appropriately via PLCrashReporterConfig.
 *
 * The iOS implementation is implemented almost entirely using public API, and links against no actual private symbols;
 * the use of undocumented functionality is limited to assuming the use of specific msgh_id values (see below
 * for details). As a result, it may be considered perfectly safe to include the Mach Exception code in the
 * standard build, and enable/disable it at runtime.
 *
 * The following issues exist in the iOS implementation:
 *  - The msgh_id values required for an exception reply message are not available from the available
 *    headers and must be hard-coded. This prevents one from safely replying to exception messages, which
 *    means that it is impossible to (correctly) inform the server that an exception has *not* been
 *    handled.
 *
 *    Impact:
 *      This can lead to the process locking up and not dispatching to the host exception handler (eg, Apple's
 *      crash reporter), depending on the behavior of the kernel exception code.
 *
 *  - The mach_* structure/type variants required by MACH_EXCEPTION_CODES are not publicly defined (on Mac OS X,
 *    these are provided by mach_exc.defs). This prevents one from forwarding exception messages to an existing
 *    handler that was registered with a MACH_EXCEPTION_CODES behavior (eg, forwarding is entirely non-functional
 *    on ARM64 devices).
 *
 *    Impact:
 *      This can break forwarding to any task exception handler that registers itself with MACH_EXCEPTION_CODES,
 *      including other handlers registered within the current process, eg, by a managed runtime. This could
 *      also result in misinterpretation of a Mach exception message, in the case where the message format is
 *      modified by Apple to be incompatible with the existing 32-bit format.
 *
 *      This is the case with LLDB; it will register a task exception handler with MACH_EXCEPTION_CODES set. Failure
 *      to correctly forward these exceptions will result in the debugger breaking in interesting ways; for example,
 *      changes to the set of dyld-loaded images are detected by setting a breakpoint on the dyld image registration
 *      funtions, and this functionality will break if the exception is not correctly forwarded.
 *
 * Since Mach exception handling is important for a fully functional crash reporter, we have also filed a radar
 * to request that the API be made public:
 *  Radar: rdar://12939497 RFE: Provide mach_exc.defs for iOS
 *
 * At the time of this writing, the radar remains open/unresolved.
 */
