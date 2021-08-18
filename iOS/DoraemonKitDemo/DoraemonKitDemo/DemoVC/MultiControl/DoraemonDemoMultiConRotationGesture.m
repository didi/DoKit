//
//  DoraemonDemoMultiConRotationGesture.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/21.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiConRotationGesture.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
@interface DoraemonDemoMultiConRotationGesture ()

@property (nonatomic, strong)UIImageView *imageview;

@end

@implementation DoraemonDemoMultiConRotationGesture

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"旋转手势");
    [self.view addSubview:self.imageview];
    self.imageview.center = self.view.center;
    self.imageview.userInteractionEnabled = YES;
    UIRotationGestureRecognizer * rotate = [[UIRotationGestureRecognizer alloc] initWithTarget:self action:@selector(rotate:)];
    [self.imageview addGestureRecognizer:rotate];
}

- (UIImageView *)imageview {
    if(!_imageview){
        CGFloat size = 240;
        _imageview = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - size) / 2.0, size, self.view.doraemon_width, size)];//        CGFloat size =
        
        _imageview.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync_banner"];
    }
    return _imageview;
}

-(void)rotate:(UIRotationGestureRecognizer *)sender{
    if (sender.state == UIGestureRecognizerStateChanged) {
        self.imageview.transform = CGAffineTransformMakeRotation(sender.rotation);
    }
}

@end
