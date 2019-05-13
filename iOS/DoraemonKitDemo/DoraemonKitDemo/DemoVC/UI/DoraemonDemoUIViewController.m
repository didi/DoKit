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
    
}


@end
