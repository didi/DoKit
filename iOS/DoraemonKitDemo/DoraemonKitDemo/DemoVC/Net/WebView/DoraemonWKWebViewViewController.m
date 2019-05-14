//
//  DoraemonWKWebViewViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/12/26.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonWKWebViewViewController.h"
#import <WebKit/WebKit.h>
#import "Doraemoni18NUtil.h"

@interface DoraemonWKWebViewViewController ()

@end

@implementation DoraemonWKWebViewViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = DoraemonLocalizedString(@"WKWebView");
    WKWebView *webView = [[WKWebView alloc]initWithFrame:self.view.frame];
    [webView loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"https://www.baidu.com"]]];
    [self.view addSubview:webView];
    
}

@end
