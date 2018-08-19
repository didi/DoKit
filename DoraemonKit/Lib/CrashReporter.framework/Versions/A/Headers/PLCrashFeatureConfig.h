/*
 * Author: Landon Fuller <landonf@plausible.coop>
 *
 * Copyright (c) 2012-2013 Plausible Labs Cooperative, Inc.
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

#ifndef PLCRASH_FEATURE_CONFIG_H
#define PLCRASH_FEATURE_CONFIG_H

#include <TargetConditionals.h>

/**
 * @internal
 *
 * Build-time configuration for PLCrashReporter.
 *
 * This is used to automatically enable/disable features on a per-platform and per-configuration
 * basis; it may also be used by third-party vendors to configure a custom build of PLCrashReporter.
 *
 * @defgroup build_config Build Configuration
 * @ingroup constants
 * @{
 */

/*
 * Defaults
 */

/*
 * For release builds, disable unused unwind implementations on targets that do not use them. For non-release
 * builds, we include the unwind implementations to allow testing on a broader range of targets.
 */
#ifdef PLCF_RELEASE_BUILD
#  if defined(__arm__)
#    ifndef PLCRASH_FEATURE_UNWIND_DWARF
#      define PLCRASH_FEATURE_UNWIND_DWARF 0
#    endif
#    ifndef PLCRASH_FEATURE_UNWIND_COMPACT
#      define PLCRASH_FEATURE_UNWIND_COMPACT 0
#    endif
#  endif
#endif

/*
 * Configuration Flags
 */


#ifndef PLCRASH_FEATURE_MACH_EXCEPTIONS
/**
 * If true, enable Mach exception support. On Mac OS X, the Mach exception implementation is fully supported,
 * using publicly available API. On iOS, the APIs required for a complete implementation are not public. However, a
 * popular commercial crash reporter is now shipping with support for Mach exceptions, which implies that either
 * they've received special dispensation to use private APIs / private structures, they've found another way to do
 * it, or they're just using undocumented functionality and hoping for the best.
 *
 * The exposed surface of undocumented API usage is relatively low, and there has been strong user demand to
 * implement Mach exception handling regardless of concerns over API visiblity. Given this, we've enabled
 * Mach exception handling by default, and provided both build-time and runtime configuration
 * to disable its use.
 *
 * For more information on the potential issues with enabling mach exception support, @sa @ref mach_exceptions.
 */
#    define PLCRASH_FEATURE_MACH_EXCEPTIONS 1
#endif

#ifndef PLCRASH_FEATURE_UNWIND_DWARF
/** If true, enable DWARF unwinding support. */
#    define PLCRASH_FEATURE_UNWIND_DWARF 1
#endif


#ifndef PLCRASH_FEATURE_UNWIND_COMPACT
/** If true, enable compact unwinding support. */
#    define PLCRASH_FEATURE_UNWIND_COMPACT 1
#endif

/**
 * @}
 */

#endif /* PLCRASH_FEATURE_CONFIG_H */
