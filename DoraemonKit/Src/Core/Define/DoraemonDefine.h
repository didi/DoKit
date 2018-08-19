//
//  DoraemonDefine.h
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#ifndef DoraemonDefine_h
#define DoraemonDefine_h

#import "DoraemonDeviceManager.h"

#define DoraemonScreenWidth [UIScreen mainScreen].bounds.size.width
#define DoraemonScreenHeight [UIScreen mainScreen].bounds.size.height

#define IS_IPHONE_X [DoraemonDeviceManager is_IPhone_X]
#define IPHONE_NAVIGATIONBAR_HEIGHT  (IS_IPHONE_X ? 88 : 64)
#define IPHONE_STATUSBAR_HEIGHT      (IS_IPHONE_X ? 44 : 20)
#define IPHONE_SAFEBOTTOMAREA_HEIGHT (IS_IPHONE_X ? 34 : 0)
#define IPHONE_TOPSENSOR_HEIGHT      (IS_IPHONE_X ? 32 : 0)

#define DoraemonMockCoordinateNotification @"DoraemonMockCoordinateNotification"


#define DoraemonShowPluginNotification @"DoraemonShowPluginNotification"
#define DoraemonClosePluginNotification @"DoraemonClosePluginNotification"

#define DoraemonH5DoorPluginNotification @"DoraemonH5DoorPluginNotification"

#endif /* DoraemonDefine_h */
