//
//  DoraemonDemoMemoryLeakViewController.m
//  DoraemonKitDemo
//
//  Created by didi on 2019/10/6.
//  Copyright © 2019 yixiang. All rights reserved.
//

#import "DoraemonDemoMemoryLeakViewController.h"
#import "DoraemonDemoMemoryLeakModel.h"
#import "DoraemonDemoMemoryLeakView.h"

@interface DoraemonDemoMemoryLeakViewController ()

@property (nonatomic, strong) DoraemonDemoMemoryLeakModel *model;
@property (nonatomic, strong) DoraemonDemoMemoryLeakView *testview;

@end

@implementation DoraemonDemoMemoryLeakViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"测试内存泄漏";
    
    _model = [[DoraemonDemoMemoryLeakModel alloc] init];
    [_model addBlock:^{
        [self printXX];
    }];
    //[_model install];

    //_testview = [[DoraemonDemoMemoryLeakView alloc] initWithFrame:CGRectMake(100, 200, 100, 100)];
    //[self.view addSubview:_testview];
    

    
    
}

- (void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    
    //__weak id weakSelf = self;
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(4 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        //__strong id strongSelf = weakSelf;
//        [self printXX];
//    });
}

- (void)printXX{
    NSLog(@"XX");
}

- (void)dealloc{
    NSLog(@"DoraemonDemoMemoryLeakViewController dealloc");
}

@end
