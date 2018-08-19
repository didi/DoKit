//
//  DoraemonDemoLoggerViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoLoggerViewController.h"
#import <CocoaLumberjack/CocoaLumberjack.h>
#import "UIView+Positioning.h"

@interface DoraemonDemoLoggerViewController ()

@end

@implementation DoraemonDemoLoggerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"日记测试Demo";
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 20, self.view.width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:@"添加一条日志" forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(addLogger) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
}

- (void)addLogger{
    DDLogInfo(@"日记来啦。。。");
}

@end
