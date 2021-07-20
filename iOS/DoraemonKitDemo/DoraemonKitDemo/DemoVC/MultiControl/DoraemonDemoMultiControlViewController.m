//
//  DoraemonDemoMultiControlViewController.m
//  DoraemonKitDemo
//
//  Created by wzp on 2021/7/14.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonDemoMultiControlViewController.h"
#import <DoraemonKit/UIView+Doraemon.h>
#import "DoraemonDefine.h"
#import "DoraemonHealthAlertView.h"
@interface DoraemonDemoMultiControlViewController ()

@end

@implementation DoraemonDemoMultiControlViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"一机多控Demo");
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:DoraemonDemoLocalizedString(@"UIControl 点击事件 系统 AlertView") forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(controlEvent) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    
    

    
    UIButton *btn_2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn_2.backgroundColor = [UIColor orangeColor];
    [btn_2 setTitle:DoraemonDemoLocalizedString(@"UIControl 点击事件 系统 UIAlertController") forState:UIControlStateNormal];
    [btn_2 addTarget:self action:@selector(controlEvent2) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn_2];
    
    
    UIButton *btn_3 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn_2.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn_3.backgroundColor = [UIColor orangeColor];
    [btn_3 setTitle:DoraemonDemoLocalizedString(@"UIControl 点击事件 自定义Alertview") forState:UIControlStateNormal];
    [btn_3 addTarget:self action:@selector(controlEvent3) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn_3];
}

- (void)controlEvent{
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"点击事件测试" message:@"我是普通警告框"
        delegate:nil cancelButtonTitle:@"好的" otherButtonTitles: nil];
     [alert show];

}

- (void)controlEvent2{
    [self showAlertMessage];
}

- (void)controlEvent3{
    DoraemonHealthAlertView *alertView = [[DoraemonHealthAlertView alloc] init];
    [alertView renderUI:DoraemonLocalizedString(@"UIControl 点击事件 自定义Alertview") placeholder:@[] inputTip:@[DoraemonLocalizedString(@"测试用例名称"),DoraemonLocalizedString(@"测试人名称")] ok:DoraemonLocalizedString(@"提交") quit:DoraemonLocalizedString(@"丢弃") cancle:DoraemonLocalizedString(@"取消") okBlock:^{


    } quitBlock:^{

    } cancleBlock:^{

    }];

    [self.view addSubview:alertView];
}

- (void)showAlertMessage{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"提示" message:[NSString stringWithFormat:@"当前设备为模拟器，不支持扫一扫功能"] preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *sureAction = [UIAlertAction actionWithTitle:@"知道了" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    
    }];
    [alertController addAction:sureAction];
    
    [self.navigationController presentViewController:alertController animated:YES completion:nil];
}
@end
