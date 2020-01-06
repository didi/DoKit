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

@end

@implementation DoraemonDemoUIViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"视觉测试Demo");
    
    UIView *redView = [[UIView alloc] initWithFrame:CGRectMake(100, 200, 60, 60)];
    redView.backgroundColor = [UIColor redColor];
    [self.view addSubview:redView];
    
//    UIView *alphaView = [[UIView alloc] initWithFrame:CGRectMake(100, 300, 60, 60)];
//    alphaView.backgroundColor = [UIColor doraemon_colorWithHexString:@"#FFFF00"];
//    alphaView.alpha = 0.5;
//    [self.view addSubview:alphaView];
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(100, 400, 200, 60)];
    titleLabel.text = DoraemonLocalizedString(@"我是来测试的");
    titleLabel.backgroundColor = [UIColor doraemon_colorWithString:@"#00FF00"];
    titleLabel.textColor = [UIColor doraemon_colorWithString:@"#FF0000"];
    [self.view addSubview:titleLabel];
    
    UITextField *input = [[UITextField alloc] initWithFrame:CGRectMake(100, 300, 200, 50)];
    input.textAlignment = NSTextAlignmentCenter;
    input.keyboardType = UIKeyboardTypeNumberPad;
    input.backgroundColor  = [UIColor lightGrayColor];
    [self.view addSubview:input];
    
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(200, 200, 200, 50)];
    button.backgroundColor = [UIColor lightGrayColor];
    button.layer.cornerRadius = 8;
    [button setTitle:@"UIMenuController测试" forState:UIControlStateNormal];
    [button addTarget:self action:@selector(deleteBtnAction:) forControlEvents:UIControlEventTouchUpInside];
    
    [self.view addSubview:button];
    
    
}


- (void)deleteBtnAction:(UIButton *)deleteBtn
{
    [self becomeFirstResponder];// 用于UIMenuController显示，缺一不可
    UIMenuController *menu = [UIMenuController sharedMenuController];
    UIMenuItem *item1 = [[UIMenuItem alloc] initWithTitle:@"撤销" action:@selector(revokeAction)];
    UIMenuItem *item2 = [[UIMenuItem alloc] initWithTitle:@"确认" action:@selector(sureAction)];
    menu.menuItems = @[item1, item2];
    menu.arrowDirection = UIMenuControllerArrowUp;
    [menu setTargetRect:deleteBtn.frame inView:deleteBtn.superview];//  [menu setTargetRect:所点击的按钮Frame inView:按钮的父视图];
    [menu setMenuVisible:YES animated:YES];
}

- (BOOL)canBecomeFirstResponder{
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
    NSLog(@"选择了撤销");
}

- (void)sureAction{
    NSLog(@"选择了删除");
}



@end
