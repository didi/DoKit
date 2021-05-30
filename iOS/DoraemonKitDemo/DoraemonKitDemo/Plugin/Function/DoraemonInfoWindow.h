//
//  DoraemonInfoWindow.h
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/5.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonVisualInfoWindow.h"
#import <objc/runtime.h>


@interface DoraemonInfoWindow : UIView
   
+ (DoraemonInfoWindow *)shareInstance;

- (void)hide;

- (void)show;

-(void)setInfo:(UIView *)view:(int)choice;

@end

