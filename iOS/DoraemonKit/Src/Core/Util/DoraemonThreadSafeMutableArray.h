//
//  DoraemonThreadSafeMutableArray.h
//  DoraemonKit
//
//  Created by didi on 2020/1/6.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonThreadSafeMutableArray : NSMutableArray

- (instancetype)init;
- (instancetype)initWithCapacity:(NSUInteger)numItems;
- (instancetype)initWithArray:(NSArray *)array;
- (instancetype)initWithCoder:(NSCoder *)aDecoder;

- (NSUInteger)count;
- (id)objectAtIndex:(NSUInteger)index;
- (id)objectAtIndexedSubscript:(NSUInteger)index;
- (id)firstObject;
- (id)lastObject;
- (BOOL)containsObject:(id)anObject;
- (NSEnumerator *)objectEnumerator;
- (NSEnumerator *)reverseObjectEnumerator;
- (void)insertObject:(id)anObject atIndex:(NSUInteger)index;
- (void)setObject:(id)anObject atIndexedSubscript:(NSUInteger)index;
- (void)addObject:(id)anObject;
- (void)removeObject:(id)anObject;
- (void)removeObjectAtIndex:(NSUInteger)index;
- (void)removeLastObject;
- (void)removeAllObjects;
- (void)replaceObjectAtIndex:(NSUInteger)index withObject:(id)anObject;
- (NSUInteger)indexOfObject:(id)anObject;

@end

NS_ASSUME_NONNULL_END
