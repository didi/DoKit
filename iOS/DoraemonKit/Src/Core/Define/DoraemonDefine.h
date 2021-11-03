//
//  DoraemonDefine.h
//  DoraemonKit
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
#import "DoraemonAlertUtil.h"
#import "DoraemonUtil.h"

#define DoKitVersion @"3.0.8"
#define DoKitKbChange(x) x * 1000

//#define DoKit_OpenLog

#ifdef DoKit_OpenLog
#define DoKitLog(...) NSLog(@"DoKitLog -> %s\n %@ \n\n",__func__,[NSString stringWithFormat:__VA_ARGS__]);
#else
#define DoKitLog(...)
#endif

#define WEAKSELF(weakSelf)  __weak __typeof(&*self)weakSelf = self;

#define DoraemonScreenWidth [UIScreen mainScreen].bounds.size.width
#define DoraemonScreenHeight [UIScreen mainScreen].bounds.size.height

//Doraemon默认位置
#define DoraemonStartingPosition            CGPointMake(0, DoraemonScreenHeight/3.0)

//Doraemon全屏默认位置
#define DoraemonFullScreenStartingPosition  CGPointZero

//根据750*1334分辨率计算size
#define kDoraemonSizeFrom750(x) ((x)*DoraemonScreenWidth/750)
// 如果横屏显示
#define kDoraemonSizeFrom750_Landscape(x) (kInterfaceOrientationLandscape ? ((x)*DoraemonScreenHeight/750) : kDoraemonSizeFrom750(x))

#define kInterfaceOrientationPortrait UIInterfaceOrientationIsPortrait([UIApplication sharedApplication].statusBarOrientation)

#define kInterfaceOrientationLandscape UIInterfaceOrientationIsLandscape([UIApplication sharedApplication].statusBarOrientation)


#define IS_IPHONE_X_Series [DoraemonAppInfoUtil isIPhoneXSeries]
#define IPHONE_NAVIGATIONBAR_HEIGHT  (IS_IPHONE_X_Series ? 88 : 64)
#define IPHONE_STATUSBAR_HEIGHT      (IS_IPHONE_X_Series ? 44 : 20)
#define IPHONE_SAFEBOTTOMAREA_HEIGHT (IS_IPHONE_X_Series ? 34 : 0)
#define IPHONE_TOPSENSOR_HEIGHT      (IS_IPHONE_X_Series ? 32 : 0)

//判空
#define STRING_IS_BLANK(str) (str==nil ||![str isKindOfClass:[NSString class]]|| [str length]<1)
#define STRING_NOT_NULL(str) ((str==nil)?@"":str)
#define DICTIONARY_IS_EMPTY(dic) ((dic)==nil || ![(dic) isKindOfClass:[NSDictionary class]] || (dic).count < 1)
#define ARRAY_IS_NULL(array) ((array)==nil || ![(array) isKindOfClass:[NSArray class]] || ([array count]) < 1)
#define ARRAY_IS_EMPTY(array) ((array)==nil || ![(array) isKindOfClass:[NSArray class]] || (array).count < 1)
#define STRING_DEFAULT_BLANK(str) ((str==nil)?@"":str)


#define DoraemonClosePluginNotification @"DoraemonClosePluginNotification"
#define DoraemonQuickOpenLogVCNotification @"DoraemonQuickOpenLogVCNotification"
#define DoraemonKitManagerUpdateNotification @"DoraemonKitManagerUpdateNotification"


#endif /* DoraemonDefine_h */
