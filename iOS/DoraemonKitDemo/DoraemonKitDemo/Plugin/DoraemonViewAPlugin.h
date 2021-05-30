//
//  DoraemonViewAPlugin.h
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/2.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DoraemonViewAPlugin : UIView

+ (DoraemonViewAPlugin *)shareInstance;

- (void)hide;

- (void)show;

-(void)setInfo:(UIView *)view;

@end
