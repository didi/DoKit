//
//  DoraemonPickerView.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonPickerView.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"

@implementation DoraemonPickerView

#pragma mark - Overwrite
- (void)initUI {
    [super initUI];
    self.overflow = YES;
    self.backgroundColor = [UIColor clearColor];
    self.layer.cornerRadius = self.doraemon_width / 2.0;
    self.layer.masksToBounds = YES;
    
    UIImageView *imageView = [[UIImageView alloc] initWithImage:[UIImage doraemon_imageNamed:@"doraemon_visual"]];
    imageView.frame = self.bounds;
    [self addSubview:imageView];
//    self.layer.borderColor = [UIColor blackColor].CGColor;
//    self.layer.borderWidth = 2;
//
//    CGFloat width = 20;
//    CAShapeLayer *layer = [CAShapeLayer layer];
//    layer.frame = self.bounds;
//    layer.path = [UIBezierPath bezierPathWithOvalInRect:CGRectMake((self.doraemon_width - width) / 2.0, (self.doraemon_height - width) / 2.0, width, width)].CGPath;
//    layer.fillColor = [[UIColor blackColor] colorWithAlphaComponent:0.5].CGColor;
//    layer.strokeColor = [UIColor whiteColor].CGColor;
//    layer.lineWidth = 0.5;
//    [self.layer addSublayer:layer];
}

@end
