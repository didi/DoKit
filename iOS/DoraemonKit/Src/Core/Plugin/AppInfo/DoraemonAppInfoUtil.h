//
//  DoraemonAppInfoUtil.h
//  Aspects
//
//  Created by yixiang on 2018/4/15.
//

#import <Foundation/Foundation.h>

@interface DoraemonAppInfoUtil : NSObject

+ (NSString *)iphoneType;

+ (BOOL)isIPhoneXSeries;

+ (NSString *)locationAuthority;

+ (NSString *)pushAuthority;

+ (NSString *)cameraAuthority;

+ (NSString *)audioAuthority;

+ (NSString *)photoAuthority;

+ (NSString *)addressAuthority;

+ (NSString *)calendarAuthority;

+ (NSString *)remindAuthority;

+ (NSString *)bluetoothAuthority;
@end
