//
//  DoraemonDemoMultiConLongPressGesture.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/21.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiConLongPressGesture.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
@interface DoraemonDemoMultiConLongPressGesture ()

@property (nonatomic, strong)UIImageView *imageview;

@end

@implementation DoraemonDemoMultiConLongPressGesture

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"长按事件");
    self.imageview.userInteractionEnabled = YES;
    [self.view addSubview:self.imageview];
    UILongPressGestureRecognizer * longpress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longpress:)];
    longpress.minimumPressDuration = 0.5;
    longpress.numberOfTapsRequired = 0;
    longpress.cancelsTouchesInView = YES;
    [self.imageview addGestureRecognizer:longpress];
}

- (UIImageView *)imageview {
    if(!_imageview){
        CGFloat size = 240;
        _imageview = [[UIImageView alloc] initWithFrame:CGRectMake((self.view.doraemon_width - size) / 2.0, size, self.view.doraemon_width, size)];//        CGFloat size =
        
        _imageview.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_file_sync_banner"];
    }
    return _imageview;
}

-(void)longpress:(UILongPressGestureRecognizer *)sender {
    [self showAlertMessage];
}


- (void)showAlertMessage{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"提示" message:[NSString stringWithFormat:@"是否要删除图片"] preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *sureAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        
    }];
    UIAlertAction *sureAction2 = [UIAlertAction actionWithTitle:@"同意" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self deleteImageAction];
    }];
    [alertController addAction:sureAction];
    [alertController addAction:sureAction2];
    
    [self.navigationController presentViewController:alertController animated:YES completion:nil];
}


- (void)deleteImageAction {
    [UIView animateWithDuration:1.0
                          delay:0.0
                        options:0
                     animations:^{
                         self.imageview.alpha = 0.0;
                     } completion:^(BOOL finished) {
                         [self.imageview removeFromSuperview];
                     }];
}

@end
