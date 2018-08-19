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

#import <Foundation/Foundation.h>

@interface PLCrashReportSymbolInfo : NSObject {
@private
    /** The symbol name. */
    NSString *_symbolName;
    
    /** The symbol start address. */
    uint64_t _startAddress;
    
    /** The symbol end address, if explicitly defined. Will be 0 if unknown. */
    uint64_t _endAddress;
}

- (id) initWithSymbolName: (NSString *) symbolName
             startAddress: (uint64_t) startAddress
               endAddress: (uint64_t) endAddress;

/** The symbol name. */
@property(nonatomic, readonly) NSString *symbolName;

/** The symbol start address. */
@property(nonatomic, readonly) uint64_t startAddress;

/* The symbol end address, if explicitly defined. This will only be included if the end address is
 * explicitly defined (eg, by DWARF debugging information), will not be derived by best-guess
 * heuristics.
 *
 * If unknown, the address will be 0.
 */
@property(nonatomic, readonly) uint64_t endAddress;

@end
