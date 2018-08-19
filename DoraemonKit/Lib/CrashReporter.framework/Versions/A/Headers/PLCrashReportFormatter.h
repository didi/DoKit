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

#import "PLCrashReport.h"

/**
 * A crash report formatter accepts a PLCrashReport instance, formats it according to implementation-specified rules,
 * (such as implementing text output support), and returns the result.
 */
@protocol PLCrashReportFormatter

/**
 * Format the provided @a report.
 *
 * @param report Report to be formatted.
 * @param outError A pointer to an NSError object variable. If an error occurs, this pointer will contain an error
 * object indicating why the pending crash report could not be formatted. If no error occurs, this parameter will
 * be left unmodified. You may specify nil for this parameter, and no error information will be provided.
 *
 * @return Returns the formatted report data on success, or nil on failure.
 */
- (NSData *) formatReport: (PLCrashReport *) report error: (NSError **) outError;

@end
