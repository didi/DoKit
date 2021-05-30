//
//  DoraemonMemoryLeakData.h
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2019/10/7.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMemoryLeakData : NSObject

+ (DoraemonMemoryLeakData *)shareInstance;

- (void)addObject:(id)object;

- (void)removeObjectPtr:(NSNumber *)objectPtr;

- (NSArray *)getResult;

- (void)clearResult;

@end

NS_ASSUME_NONNULL_END
