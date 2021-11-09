//
//  NSArray+JSON.h
//  DoraemonKit
//
//  Created by wzp on 2021/10/11.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSArray (JSON)

+ (NSArray *)arrayWithJsonArray:(NSArray<NSDictionary *> *)aJsonArray modelClassName:(NSString *) aModelClassName;

@end

NS_ASSUME_NONNULL_END
