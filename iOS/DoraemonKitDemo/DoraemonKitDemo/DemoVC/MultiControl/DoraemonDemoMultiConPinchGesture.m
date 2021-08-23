//
//  DoraemonDemoMultiConPinchGesture.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/21.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiConPinchGesture.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
@interface DoraemonDemoMultiConPinchGesture ()

@property (nonatomic, strong)UIImageView *imageview;
@property (nonatomic,strong) UIView * backgroundView;
@property (nonatomic) BOOL isLargeView;
@property (nonatomic) CGRect oldFrame;

@end


@implementation DoraemonDemoMultiConPinchGesture

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"长安事件");
    self.imageview.center = self.view.center;
    [self.view addSubview:self.imageview];
    [self.imageview setUserInteractionEnabled:YES];
    UIPinchGestureRecognizer * pinch = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(pinch:)];
    [self.imageview addGestureRecognizer:pinch];
    self.isLargeView = NO;
    self.oldFrame = self.imageview.frame;
}


- (UIImageView *)imageview {
    if(!_imageview){
        CGFloat size = 400;
        _imageview = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - size) / 2.0, size, self.view.doraemon_width, size)];//        CGFloat size =
        
        _imageview.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync_banner"];
    }
    return _imageview;
}


-(void)pinch:(UIPinchGestureRecognizer *)pinch{
    if (pinch.state == UIGestureRecognizerStateRecognized) {
        if (!self.isLargeView && pinch.velocity > 0) {
            self.backgroundView = [[UIView alloc] initWithFrame:self.view.frame];
            self.backgroundView.backgroundColor = [UIColor blackColor];
            self.backgroundView.alpha = 0.0;
            self.imageview.backgroundColor = [UIColor blueColor];
            [self.view insertSubview:self.backgroundView belowSubview:self.imageview];
            [UIView animateWithDuration:0.8
                                  delay:0.0
                                options:UIViewAnimationOptionCurveEaseInOut
                             animations:^{
                                 self.imageview.frame = CGRectMake(0,220,320,210);
                             }
                             completion:^(BOOL finished) {
                                 self.isLargeView = YES;
                                 
                             }];
        }
        if (self.isLargeView &&  pinch.velocity < 0) {
            [UIView animateWithDuration:0.8
                             animations:^{
                                 self.imageview.frame = self.oldFrame;
                                 
                             }
                             completion:^(BOOL finished) {
                                 [self.backgroundView removeFromSuperview];
                                 self.backgroundView = nil;
                                 self.isLargeView = NO;
                             }];
        }
    }
}







@end
