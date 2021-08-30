//
//  DoraemonDemoMultiConSwipeGesture.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/21.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiConSwipeGesture.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
#import "UIColor+Doraemon.h"
@interface DoraemonDemoMultiConSwipeGesture ()
@property (nonatomic, strong)UIImageView *image3;
@property (nonatomic, strong)UIImageView *image2;
@property (nonatomic, strong)UILabel *label;
@end

@implementation DoraemonDemoMultiConSwipeGesture

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"滑动手势");
    
    UISwipeGestureRecognizer * swipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(swipe:)];
    swipe.numberOfTouchesRequired = 2;
    [swipe setDirection:UISwipeGestureRecognizerDirectionLeft];
    [self.view addGestureRecognizer:swipe];
    [self.view addSubview:self.image2];
    [self.view addSubview:self.image3];
    [self.view addSubview:self.label];
    self.label.text = @"向左滑方向";
    self.label.center = self.view.center;
    
    self.image2.userInteractionEnabled = YES;
    self.image2.hidden = NO;
    self.image3.userInteractionEnabled = YES;
    self.image3.hidden = YES;
}


-(void)swipe:(UISwipeGestureRecognizer *)sender{
    
    if (sender.state == UIGestureRecognizerStateRecognized) {
        [UIView transitionWithView:self.view
                          duration:0.8
                           options:UIViewAnimationOptionTransitionCurlUp
                        animations:^{
                            self.image3.hidden = !self.image3.hidden;
                            self.image2.hidden = !self.image2.hidden;
                        } completion:^(BOOL finished) {
                            
                        }];
        
    }
    
}
- (UILabel *)label {
    if(!_label){
        _label = [[UILabel alloc]initWithFrame:CGRectMake(0, 0, 200, 40)];
        _label.textColor = [UIColor doraemon_colorWithString:@"#333333"];
        _label.font = [UIFont boldSystemFontOfSize:20];
        _label.textAlignment = NSTextAlignmentCenter;
    }
    return _label;
    
}
- (UIImageView *)image3 {
    if(!_image3){
        CGFloat size = 40;
        _image3 = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - 3*size) / 2.0, size*3, size, size)];//        CGFloat size =
        
        _image3.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync"];
    }
    return _image3;
}

- (UIImageView *)image2 {
    if(!_image2){
        CGFloat size = 40;
        _image2 = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - 3*size) / 2.0, size*5, size, size)];//        CGFloat size =
        
        _image2.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync"];
    }
    return _image2;
}

@end
