//
//  DoraemonDemoOCViewController.m
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/18.
//  Copyright © 2020 didi. All rights reserved.
//

#import "DoraemonDemoOCViewController.h"
#import "DoKitSwiftDemo-Swift.h"


@interface DoraemonDemoOCViewController ()

@end

@implementation DoraemonDemoOCViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = @"Call OC";
    self.view.backgroundColor = [UIColor whiteColor];
    
    UIButton *btn = [[UIButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, 60)];
    btn.backgroundColor = [UIColor orangeColor];
    [btn setTitle:@"Call Swift" forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(callSwift) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
}

- (void)callSwift{
    DoraemonDemoSwiftViewController *vc = [[DoraemonDemoSwiftViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
