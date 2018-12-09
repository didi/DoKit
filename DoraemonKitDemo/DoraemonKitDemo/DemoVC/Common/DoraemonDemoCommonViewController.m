//
//  DoraemonDemoCommonViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/12/3.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoCommonViewController.h"
#import "DoraemonDefine.h"

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
}

- (void)addSubViewAtOtherThread{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        UIView *v = [UIView new];
        [self.view addSubview:v];
    });
}

@end
