//
//  NSObject+MemoryLeak.h
//  MLeaksFinder
//
//  Created by zeposhe on 12/12/15.
//  Copyright © 2015 zeposhe. All rights reserved.
//

#import <Foundation/Foundation.h>

#define MLCheck(TARGET) [self willReleaseObject:(TARGET) relationship:@#TARGET];

@interface NSObject (MemoryLeak)

- (BOOL)willDealloc;
- (void)willReleaseObject:(id)object relationship:(NSString *)relationship;

- (void)willReleaseChild:(id)child;
- (void)willReleaseChildren:(NSArray *)children;

- (NSArray *)viewStack;

+ (void)addClassNamesToWhitelist:(NSArray *)classNames;

+ (void)swizzleSEL:(SEL)originalSEL withSEL:(SEL)swizzledSEL;

@end
