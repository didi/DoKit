//
//  DoraemonDemoCrashViewController.m
//  DoraemonKitDemo
//
//  Created by wenquan on 2018/11/5.
//  Copyright © 2018 yixiang. All rights reserved.
//

#import "DoraemonDemoCrashViewController.h"

#import <DoraemonKit/UIView+Doraemon.h>

#import "DoraemonDemoCrashMRCView.h"
#import "DoraemonDefine.h"

typedef struct Test
{
    int a;
    int b;
}Test;

@interface DoraemonDemoCrashViewController ()

@property (nonatomic, strong) UIButton *uncaughtExceptionBtn;
@property (nonatomic, strong) UIButton *signalExceptionBtn;

@end

@implementation DoraemonDemoCrashViewController

#pragma mark - View Lifecycle

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"Crash";
    
    [self.view addSubview:self.uncaughtExceptionBtn];
    [self.view addSubview:self.signalExceptionBtn];
}

#pragma mark - Layout

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    self.uncaughtExceptionBtn.frame = CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60);
    
    self.signalExceptionBtn.frame = CGRectMake(0, self.uncaughtExceptionBtn.doraemon_bottom + 20, self.view.doraemon_width, 60);
}

#pragma mark - Actions

- (void)uncaughtExceptionBtnClicked:(id)sender {
    // ios崩溃
    NSArray *array= @[@"tom",@"xxx",@"ooo"];
    [array objectAtIndex:5];
}

- (void)signalExceptionBtnClicked:(id)sender {
    // 导致SIGABRT的错误，因为内存中根本就没有这个空间，哪来的free，就在栈中的对象而已
//    Test *pTest = {1,2};
//    free(pTest);
//    pTest->a = 5;
    
    // 导致SIGSEGV的错误
//    DoraemonDemoCrashMRCView *view = [[DoraemonDemoCrashMRCView alloc] init];
    
    //SIGBUS，内存地址未对齐
    //EXC_BAD_ACCESS(code=1,address=0x1000dba58)
    char *s = "hello world";
    *s = 'H';
}

#pragma mark - Getter

- (UIButton *)uncaughtExceptionBtn {
    if (!_uncaughtExceptionBtn) {
        _uncaughtExceptionBtn = [[UIButton alloc] init];
        _uncaughtExceptionBtn.backgroundColor = [UIColor orangeColor];
        [_uncaughtExceptionBtn setTitle:@"uncaughtException" forState:UIControlStateNormal];
        [_uncaughtExceptionBtn addTarget:self action:@selector(uncaughtExceptionBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _uncaughtExceptionBtn;
}

- (UIButton *)signalExceptionBtn {
    if (!_signalExceptionBtn) {
        _signalExceptionBtn = [[UIButton alloc] init];
        _signalExceptionBtn.backgroundColor = [UIColor orangeColor];
        [_signalExceptionBtn setTitle:@"signalException" forState:UIControlStateNormal];
        [_signalExceptionBtn addTarget:self action:@selector(signalExceptionBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _signalExceptionBtn;
}

@end
