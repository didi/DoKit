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
#import "PLCrashReportProcessorInfo.h"

@interface PLCrashReportBinaryImageInfo : NSObject {
@private
    /** Code type */
    PLCrashReportProcessorInfo *_processorInfo;

    /** Base image address */
    uint64_t _baseAddress;

    /** Image segment size */
    uint64_t _imageSize;

    /** Name of binary image */
    NSString *_imageName;

    /** If the UUID is available */
    BOOL _hasImageUUID;

    /** 128-bit object UUID. May be nil. */
    NSString *_imageUUID;
}

- (id) initWithCodeType: (PLCrashReportProcessorInfo *) processorInfo
            baseAddress: (uint64_t) baseAddress 
                   size: (uint64_t) imageSize
                   name: (NSString *) imageName
                   uuid: (NSData *) uuid;

/**
 * Image code type, or nil if unavailable.
 */
@property(nonatomic, readonly) PLCrashReportProcessorInfo *codeType;

/**
 * Image base address.
 */
@property(nonatomic, readonly) uint64_t imageBaseAddress;

/**
 * Segment size.
 */
@property(nonatomic, readonly) uint64_t imageSize;

/**
 * Image name (absolute path)
 */
@property(nonatomic, readonly) NSString *imageName;


/**
 * YES if this image has an associated UUID.
 */
@property(nonatomic, readonly) BOOL hasImageUUID;

/**
 * 128-bit object UUID (matches Mach-O DWARF dSYM files). May be nil if unavailable.
 */
@property(nonatomic, readonly) NSString *imageUUID;

@end
