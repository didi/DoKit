//
//  NSURLRequest+Doraemon.h
//  DoraemonKit
//
//  Created by yixiang on 2018/4/11.
//

#import <Foundation/Foundation.h>

@interface NSURLRequest (Doraemon)

- (NSString *)requestId;
- (void)setRequestId:(NSString *)requestId;


- (NSNumber*)startTime;
- (void)setStartTime:(NSNumber*)startTime;

@end
