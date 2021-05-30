//
//  DoraemonViewBPlugin.h
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/2.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonVisualInfoWindow.h"
#import "DoraemonInfoWindow.h"
#import "DoraemonViewAPlugin.h"
#import <objc/runtime.h>


@interface DoraemonViewBPlugin : UIView{

@public
    CGFloat _xleft;
@public
    CGFloat _ytop;
@public
    CGFloat _xwidth;
@public
    CGFloat _yheight;
}

-(CGFloat) xleft;
-(CGFloat) ytop;
-(CGFloat) xwidth;
-(CGFloat) yheight;
- (void)hide;

- (void)show;


@end

