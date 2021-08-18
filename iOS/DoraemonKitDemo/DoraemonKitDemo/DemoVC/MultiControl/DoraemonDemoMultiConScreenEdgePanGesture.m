//
//  DoraemonDemoMultiConScreenEdgePanGesture.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/21.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiConScreenEdgePanGesture.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
@interface DoraemonDemoMultiConScreenEdgePanGesture ()
@property (nonatomic,strong)UIView * edgeView;
@property (nonatomic)CGPoint  offsetCenter;
@end

@implementation DoraemonDemoMultiConScreenEdgePanGesture

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"边缘手势");
    UIScreenEdgePanGestureRecognizer * edge = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(edgePan:)];
    edge.edges = UIRectEdgeRight;
    [self.view addGestureRecognizer:edge];
}

-(void)edgePan:(UIScreenEdgePanGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateBegan) {
        self.edgeView = [[UIView alloc] initWithFrame:CGRectOffset(self.view.frame,CGRectGetWidth(self.view.frame),0)];
        self.edgeView.backgroundColor = [UIColor blueColor];
        self.offsetCenter = self.edgeView.center;
        [self.view addSubview:self.edgeView];
    }else if(sender.state == UIGestureRecognizerStateChanged){
        CGPoint translation = [sender translationInView:self.view];
        self.edgeView.center = CGPointMake(self.offsetCenter.x + translation.x,self.offsetCenter.y);
    }else if(sender.state == UIGestureRecognizerStateEnded)
    {
        if ([sender velocityInView:self.view].x < 0) {
            [UIView animateWithDuration:0.3 animations:^{
                self.edgeView.center = self.view.center;
            }];
        }else{
            [UIView animateWithDuration:0.3
                                  delay:0.0
                                options:UIViewAnimationOptionBeginFromCurrentState
                             animations:^{
                                 self.edgeView.center = self.offsetCenter;
                             }
                             completion:^(BOOL finished) {
                                 [self.edgeView removeFromSuperview];
                                 self.edgeView = nil;
                             }];
        }
    }else{
        [UIView animateWithDuration:0.3
                              delay:0.0
                            options:UIViewAnimationOptionBeginFromCurrentState
                         animations:^{
                             self.edgeView.center = self.offsetCenter;
                         }
                         completion:^(BOOL finished) {
                             [self.edgeView removeFromSuperview];
                             self.edgeView = nil;
                         }];
    }
}


//- (UIImageView *)imageview {
//    if(!_imageview){
//        CGFloat size = 240;
//        _imageview = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - size) / 2.0, size, self.view.doraemon_width, size)];//        CGFloat size =
//
//        _imageview.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync_banner"];
//    }
//    return _imageview;
//}


@end
