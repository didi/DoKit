//
//  DoraemonDemoCommonViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/12/3.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoCommonViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonManager.h"

@interface DoraemonDemoCommonViewController ()

@end

@implementation DoraemonDemoCommonViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"通用测试";
    
    UIButton *btn0 = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn0.backgroundColor = [UIColor orangeColor];
    [btn0 setTitle:@"子线程UI操作" forState:UIControlStateNormal];
    [btn0 addTarget:self action:@selector(addSubViewAtOtherThread) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn0];
    
    UIButton *btn1 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn0.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn1.backgroundColor = [UIColor orangeColor];
    [btn1 setTitle:DoraemonLocalizedString(@"显示入口") forState:UIControlStateNormal];
    [btn1 addTarget:self action:@selector(showEntry) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn1];
    
    UIButton *btn2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn1.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn2.backgroundColor = [UIColor orangeColor];
    [btn2 setTitle:DoraemonLocalizedString(@"隐藏入口") forState:UIControlStateNormal];
    [btn2 addTarget:self action:@selector(hiddenEntry) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn2];

}

- (void)addSubViewAtOtherThread{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        UIView *v = [UIView new];
        [self.view addSubview:v];
    });
}

- (void)showEntry{
    [[DoraemonManager shareInstance] showDoraemon];
}

- (void)hiddenEntry{
    [[DoraemonManager shareInstance] hiddenDoraemon];
}


@end
