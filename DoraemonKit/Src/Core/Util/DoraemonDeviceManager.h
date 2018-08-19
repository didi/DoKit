//
//  DoraemonDeviceManager.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/20.
//

#import <Foundation/Foundation.h>

@interface DoraemonDeviceManager : NSObject

//获取设备名称
+ (NSString *)deviceName;
+ (BOOL)is_IPhone_X;

@end
