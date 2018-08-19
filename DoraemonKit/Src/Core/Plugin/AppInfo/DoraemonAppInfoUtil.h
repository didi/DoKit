//
//  DoraemonAppInfoUtil.h
//  Aspects
//
//  Created by yixiang on 2018/4/15.
//

#import <Foundation/Foundation.h>

@interface DoraemonAppInfoUtil : NSObject

+ (NSString *)iphoneType;

+ (NSString *)locationAuthority;

+ (NSString *)pushAuthority;

+ (NSString *)netAuthority;

+ (NSString *)takePhotoAuthority;

+ (NSString *)audioAuthority;
@end
