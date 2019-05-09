//
//  DoraemonDefine.h
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#ifndef DoraemonDefine_h
#define DoraemonDefine_h

#import "DoraemonAppInfoUtil.h"
#import "UIColor+Doraemon.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonToastUtil.h"

#define DoraemonScreenWidth [UIScreen mainScreen].bounds.size.width
#define DoraemonScreenHeight [UIScreen mainScreen].bounds.size.height

//根据750*1334分辨率计算size
#define kDoraemonSizeFrom750(x) ((x)*DoraemonScreenWidth/750)
// 如果横屏显示
#define kDoraemonSizeFrom750_Landscape(x) (kInterfaceOrientationPortrait ? kDoraemonSizeFrom750(x) : ((x)*DoraemonScreenHeight/750))

#define kInterfaceOrientationPortrait UIInterfaceOrientationIsPortrait([UIApplication sharedApplication].statusBarOrientation)


#define IS_IPHONE_X_Series [DoraemonAppInfoUtil isIPhoneXSeries]
#define IPHONE_NAVIGATIONBAR_HEIGHT  (IS_IPHONE_X_Series ? 88 : 64)
#define IPHONE_STATUSBAR_HEIGHT      (IS_IPHONE_X_Series ? 44 : 20)
#define IPHONE_SAFEBOTTOMAREA_HEIGHT (IS_IPHONE_X_Series ? 34 : 0)
#define IPHONE_TOPSENSOR_HEIGHT      (IS_IPHONE_X_Series ? 32 : 0)


#define DoraemonShowPluginNotification @"DoraemonShowPluginNotification"
#define DoraemonClosePluginNotification @"DoraemonClosePluginNotification"
#define DoraemonQuickOpenLogVCNotification @"DoraemonQuickOpenLogVCNotification"

#endif /* DoraemonDefine_h */
