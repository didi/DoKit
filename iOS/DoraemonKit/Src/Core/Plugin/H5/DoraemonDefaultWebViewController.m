//
//  DoraemonDefaultWebViewController.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/27.
//

#import "DoraemonDefaultWebViewController.h"

@interface DoraemonDefaultWebViewController ()

@end

@implementation DoraemonDefaultWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"Doraemon内置浏览器");
    UIWebView * view = [[UIWebView alloc] initWithFrame:self.view.frame];
    [view loadRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:self.h5Url]]];
    [self.view addSubview:view];
}

@end
