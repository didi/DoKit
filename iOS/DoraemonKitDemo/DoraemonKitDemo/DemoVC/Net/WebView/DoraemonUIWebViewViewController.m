//
//  DoraemonUIWebViewViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/12/26.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonUIWebViewViewController.h"

@interface DoraemonUIWebViewViewController ()

@end

@implementation DoraemonUIWebViewViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"UIWebView";
    UIWebView * view = [[UIWebView alloc] initWithFrame:self.view.frame];
    [view loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"https://www.baidu.com"]]];
    [self.view addSubview:view];
    
}

@end
