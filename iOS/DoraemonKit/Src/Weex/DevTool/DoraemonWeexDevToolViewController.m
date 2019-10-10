//
//  DoraemonWeexDevToolViewController.m
//  WeexDemo
//
//  Created by yixiang on 2019/6/6.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexDevToolViewController.h"
#import "DoraemonQRCodeTool.h"
#import "WXDevTool.h"

@interface DoraemonWeexDevToolViewController ()

@property (nonatomic,strong) DoraemonQRCodeTool *qrcode;

@end

@implementation DoraemonWeexDevToolViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Weex DevTool"; 
    
    self.qrcode = [DoraemonQRCodeTool shared];
    __weak typeof(self) weakSelf = self;
    [self.qrcode QRCodeDeviceInitWithVC:self WithQRCodeWidth:0 ScanResults:^(NSString *result) {
        [weakSelf.qrcode stopScanning];
        [weakSelf doUrl:result];
    }];
    [self.qrcode startScanning];
    [self.view bringSubviewToFront:self.bigTitleView];
}

- (void)doUrl:(NSString *)URL{
    NSString *transformURL = URL;
    NSArray* elts = [URL componentsSeparatedByString:@"?"];
    if (elts.count >= 2) {
        NSArray *urls = [elts.lastObject componentsSeparatedByString:@"="];
        for (NSString *param in urls) {
            if ([param isEqualToString:@"_wx_tpl"]) {
                transformURL = [[urls lastObject]  stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
                break;
            }
        }
    }
    NSURL *url = [NSURL URLWithString:transformURL];
    NSString *query = url.query;
    for (NSString *param in [query componentsSeparatedByString:@"&"]){
        NSArray *elts = [param componentsSeparatedByString:@"="];
        if([elts count] < 2) continue;
        if ([[elts firstObject] isEqualToString:@"_wx_devtool"]) {
            NSString *devToolURL = [[elts lastObject]  stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            [WXDevTool launchDevToolDebugWithUrl:devToolURL];
            [self leftNavBackClick:nil];
            break;
        }
    }
    
}

- (BOOL)needBigTitleView{
    return YES;
}

@end
