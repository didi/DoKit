//
//  DoraemonMemoryUtil.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import <Foundation/Foundation.h>

@interface DoraemonMemoryUtil : NSObject

//当前app内存使用量
+ (NSUInteger)useMemoryForApp;

//设备总的内存
+ (NSUInteger)totalMemoryForDevice;

@end
