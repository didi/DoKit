//
//  DoraemonThreadSafeMutableArray.m
//  AFNetworking
//
//  Created by didi on 2020/1/6.
//

#import "DoraemonThreadSafeMutableArray.h"
#import <pthread/pthread.h>

@interface DoraemonThreadSafeMutableArray(){
    NSMutableArray* _array;
    pthread_mutex_t _safeThreadArrayMutex;
    pthread_mutexattr_t _safeThreadArrayMutexAttr;
}

@end

@implementation DoraemonThreadSafeMutableArray

- (instancetype)initCommon
{
    self = [super init];
    if (self) {
        pthread_mutexattr_init(&(_safeThreadArrayMutexAttr));
        pthread_mutexattr_settype(&(_safeThreadArrayMutexAttr), PTHREAD_MUTEX_RECURSIVE); // must use recursive lock
        pthread_mutex_init(&(_safeThreadArrayMutex), &(_safeThreadArrayMutexAttr));
    }
    return self;
}

- (instancetype)init
{
    self = [self initCommon];
    if (self) {
        _array = [NSMutableArray array];
    }
    return self;
}

- (instancetype)initWithCapacity:(NSUInteger)numItems
{
    self = [self initCommon];
    if (self) {
        _array = [NSMutableArray arrayWithCapacity:numItems];
    }
    return self;
}

- (instancetype)initWithArray:(NSArray *)array
{
    self = [self initCommon];
    if (self) {
        _array = [NSMutableArray arrayWithArray:array];
    }
    return self;
}

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    self = [self initCommon];
    if (self) {
        _array = [[NSMutableArray alloc] initWithCoder:aDecoder];
    }
    return self;
}

- (instancetype)initWithObjects:(const id [])objects count:(NSUInteger)cnt
{
    self = [self initCommon];
    if (self) {
        _array = [NSMutableArray array];
        for (NSUInteger i = 0; i < cnt; ++i) {
            [_array addObject:objects[i]];
        }
    }
    return self;
}

- (NSUInteger)count
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array count];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (id)objectAtIndex:(NSUInteger)index
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array objectAtIndex:index];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (id)objectAtIndexedSubscript:(NSUInteger)index
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array objectAtIndexedSubscript:index];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (id)firstObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array firstObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (id)lastObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array lastObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (BOOL)containsObject:(id)anObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array containsObject:anObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (NSEnumerator *)objectEnumerator
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array objectEnumerator];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (NSEnumerator *)reverseObjectEnumerator
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array reverseObjectEnumerator];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)insertObject:(id)anObject atIndex:(NSUInteger)index
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array insertObject:anObject atIndex:index];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)setObject:(id)anObject atIndexedSubscript:(NSUInteger)index
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array setObject:anObject atIndexedSubscript:index];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)addObject:(id)anObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array addObject:anObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)removeObject:(id)anObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array removeObject:anObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)removeObjectAtIndex:(NSUInteger)index
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array removeObjectAtIndex:index];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)removeLastObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array removeLastObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)removeAllObjects
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array removeAllObjects];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)replaceObjectAtIndex:(NSUInteger)index withObject:(id)anObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        [_array replaceObjectAtIndex:index withObject:anObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (NSUInteger)indexOfObject:(id)anObject
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array indexOfObject:anObject];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (id)copy
{
    @try {
        pthread_mutex_lock(&_safeThreadArrayMutex);
        return [_array copy];
    }
    @finally {
        pthread_mutex_unlock(&_safeThreadArrayMutex);
    }
}

- (void)dealloc
{
    pthread_mutex_destroy(&_safeThreadArrayMutex);
    pthread_mutexattr_destroy(&_safeThreadArrayMutexAttr);
}

@end
