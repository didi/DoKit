//
//  DoraemonDemoUIViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoUIViewController.h"
#import <DoraemonKit/UIColor+Doraemon.h>
#import "DoraemonDefine.h"

@interface DoraemonDemoUIViewController ()

@property (nonatomic, strong) UIView *redView;
@property (nonatomic, strong) UILabel *titleLabelAAA;

@end

@implementation DoraemonDemoUIViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"视觉测试Demo");
    
    UIView *redView = [[UIView alloc] initWithFrame:CGRectMake(100, 200, 60, 60)];
    redView.backgroundColor = [UIColor redColor];
    [self.view addSubview:redView];
    _redView = redView;
    
//    UIView *alphaView = [[UIView alloc] initWithFrame:CGRectMake(100, 300, 60, 60)];
//    alphaView.backgroundColor = [UIColor doraemon_colorWithHexString:@"#FFFF00"];
//    alphaView.alpha = 0.5;
//    [self.view addSubview:alphaView];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(100, 400, 200, 60)];
    titleLabel.text = DoraemonDemoLocalizedString(@"我是来测试的");
    titleLabel.backgroundColor = [UIColor doraemon_colorWithString:@"#00FF00"];
    titleLabel.textColor = [UIColor doraemon_colorWithString:@"#FF0000"];
    [self.view addSubview:titleLabel];
    _titleLabelAAA = titleLabel;
    
    UITextField *input = [[UITextField alloc] initWithFrame:CGRectMake(100, 300, 200, 50)];
    input.textAlignment = NSTextAlignmentCenter;
    input.keyboardType = UIKeyboardTypeNumberPad;
    input.backgroundColor  = [UIColor lightGrayColor];
    [self.view addSubview:input];
    
    UITextField *input2 = [[UITextField alloc] initWithFrame:CGRectMake(100, 500, 200, 50)];
    input2.textAlignment = NSTextAlignmentCenter;
    //input2.keyboardType = UIKeyboardTypeNumberPad;
    input2.backgroundColor  = [UIColor lightGrayColor];
    [self.view addSubview:input2];
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(200, 200, 200, 50)];
    button.backgroundColor = [UIColor lightGrayColor];
    button.layer.cornerRadius = 8;
    [button setTitle:@"UIMenuController" forState:UIControlStateNormal];
    [button addTarget:self action:@selector(deleteBtnAction:) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:button];
    
    
}


- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
     //设置为第一响应者
}

- (void)deleteBtnAction:(UIButton *)deleteBtn{
    [self.view.window becomeFirstResponder];
    [self becomeFirstResponder];// 用于UIMenuController显示，缺一不可
    UIMenuController *menu = [UIMenuController sharedMenuController];
    UIMenuItem *item1 = [[UIMenuItem alloc] initWithTitle:@"cancel" action:@selector(revokeAction)];
    UIMenuItem *item2 = [[UIMenuItem alloc] initWithTitle:@"ok" action:@selector(sureAction)];
    menu.menuItems = @[item1, item2];
    menu.arrowDirection = UIMenuControllerArrowUp;
    [menu setMenuVisible:YES animated:YES];
    
    if(![menu isMenuVisible]) {
//            UIWindow *window = [[UIApplication sharedApplication].delegate window];
       
//        if([window isKeyWindow]){
//                [window becomeKeyWindow];
//                [window makeKeyAndVisible];
            [menu setTargetRect:deleteBtn.bounds inView:deleteBtn];
            if (@available(iOS 13.0, *)) {
                [menu showMenuFromView:self.view rect:deleteBtn.frame];
            } else {
                [menu setMenuVisible:YES animated:YES];
            }
        //}
    }
}

- (BOOL)canBecomeFirstResponder{
    UIWindow *window = [[UIApplication sharedApplication].delegate window];
    if(window.keyWindow == NO){
            [window becomeKeyWindow];
            [window makeKeyAndVisible];
        }
    return YES;
 }



- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if (action == @selector(revokeAction)) {
        return YES;
    }
    if (action == @selector(sureAction)) {
        return YES;
    }
    return NO;//隐藏系统默认的菜单项
}

- (void)revokeAction{
    NSLog(@"cancel");
    //UIWindow *window = [[UIApplication sharedApplication].delegate window];
    //[window.rootViewController presentViewController:[[PresentViewController alloc] init] animated:YES completion:nil];
}

- (void)sureAction{
    NSLog(@"ok");
    
    
    //NSArray *array = [string componentsSeparatedByString:@";"]; //从字符A中分隔成2个元素的数组
//    int index = 0;
//    srandom((unsigned)time(0));
//    while (index < array.count) {
//        usleep(40000);
//        NSLog(@"%@",array[index]);
//        index ++;
//    }
//
    
}




@end
