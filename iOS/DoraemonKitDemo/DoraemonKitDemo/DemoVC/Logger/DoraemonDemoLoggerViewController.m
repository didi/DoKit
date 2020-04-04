//
//  DoraemonDemoLoggerViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/5/15.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoLoggerViewController.h"
#import <DoraemonKit/UIView+Doraemon.h>
#import "DoraemonDefine.h"

#if __has_include(<CocoaLumberjack/CocoaLumberjack.h>)
#import <CocoaLumberjack/CocoaLumberjack.h>
#endif

@interface DoraemonDemoLoggerViewController ()

@end

@implementation DoraemonDemoLoggerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"日志测试Demo");
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:DoraemonDemoLocalizedString(@"添加一条CocoaLumberjack日志") forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(addLogger) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    
    UIButton *btn_2 = [[UIButton alloc] initWithFrame:CGRectMake(0, btn.doraemon_bottom+20, self.view.doraemon_width, 60)];
    btn_2.backgroundColor = [UIColor orangeColor];
    [btn_2 setTitle:DoraemonDemoLocalizedString(@"添加一条NSLog日志") forState:UIControlStateNormal];
    [btn_2 addTarget:self action:@selector(addLogger2) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn_2];
}

- (void)addLogger{
    #if __has_include(<CocoaLumberjack/CocoaLumberjack.h>)
    DDLogInfo(@"DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。DDLogInfo。。。");
    DDLogError(@"DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。DDLogError。。。");
    #endif
}

- (void)addLogger2{
    NSString *str = @"jack";
    NSInteger age = 29;
    NSLog(@"NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦。。。str == %@  age == %zi",str,age);
    
    NSString *specialString = @"callnative://saveTian/%22saveTianDataCallback43%22";
    NSLog(@"%@",specialString);
}

@end
