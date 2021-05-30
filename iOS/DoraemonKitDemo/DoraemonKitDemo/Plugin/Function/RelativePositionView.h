//
//  RelativePositionView.h
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/26.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RelativePositionView : UIView

-(instancetype)initWithLocation:(CGPoint)location;
@property (nonatomic, assign) CGRect viewFrame;

- (void)hide;

- (void)show;

@end
