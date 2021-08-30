//
//  DoraemonDemoMultiConTapGesture.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/21.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiConTapGesture.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
@interface DoraemonDemoMultiConTapGesture ()
@property (nonatomic, strong)UIImageView *imageview;
@end

@implementation DoraemonDemoMultiConTapGesture

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"点击手势");
    [self.view addSubview:self.imageview];
    self.imageview.userInteractionEnabled = YES;
    
    UITapGestureRecognizer * tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap:)];
    [self.imageview addGestureRecognizer:tap];
}

- (UIImageView *)imageview {
    if(!_imageview){
        CGFloat size = 240;
        _imageview = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - size) / 2.0, size, self.view.doraemon_width, size)];//        CGFloat size =
        
        _imageview.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync_banner"];
    }
    return _imageview;
}


- (void)tap:(UITapGestureRecognizer *)sender {
    CAKeyframeAnimation * animation = [CAKeyframeAnimation animation];
    animation.keyPath = @"position.x";
    NSInteger initalPositionX = self.imageview.layer.position.x;
    animation.values = @[@(initalPositionX),
                         @(initalPositionX + 10),
                         @(initalPositionX - 10),
                         @(initalPositionX + 10),
                         @(initalPositionX)];
    animation.keyTimes = @[
                           @(0),
                           @(1/6.0),
                           @(3/6.0),
                           @(5/6.0),
                           @(1)];
    animation.removedOnCompletion = YES;
    [self.imageview.layer addAnimation:animation forKey:@"keyFrame"];
}

@end
