//
//  DoraemonOscillogramViewController.m
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/4.
//

#import "DoraemonOscillogramViewController.h"


@interface DoraemonOscillogramViewController ()

@end

@implementation DoraemonOscillogramViewController
- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator {
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
    dispatch_async(dispatch_get_main_queue(), ^{
        self.view.window.frame = CGRectMake(0, 0, size.height, size.width);
    });
}
@end
