//
//  DoraemonAppInfoUtil.h
//  Aspects
//
//  Created by yixiang on 2018/4/15.
//

#import <Foundation/Foundation.h>

@interface DoraemonAppInfoUtil : NSObject

/**
 DeviceInfo：获取当前设备的 用户自定义的别名，例如：库克的 iPhone 9
 
 @return 当前设备的 用户自定义的别名，例如：库克的 iPhone 9
 */
+ (NSString *)iphoneName;

/**
 DeviceInfo：获取当前设备的 系统名称，例如：iOS 13.1
 
 @return 当前设备的 系统名称，例如：iOS 13.1
 */
+ (NSString *)iphoneSystemVersion;

+ (NSString *)bundleIdentifier;

+ (NSString *)bundleVersion;

+ (NSString *)bundleShortVersionString;

+ (NSString *)iphoneType;

+ (BOOL)isIPhoneXSeries;

+ (BOOL)isIpad;

+ (NSString *)locationAuthority;

+ (NSString *)pushAuthority;

+ (NSString *)cameraAuthority;

+ (NSString *)audioAuthority;

+ (NSString *)photoAuthority;

+ (NSString *)addressAuthority;

+ (NSString *)calendarAuthority;

+ (NSString *)remindAuthority;

+ (NSString *)bluetoothAuthority;

/// 设备是否模拟器
+ (BOOL)isSimulator;
@end
