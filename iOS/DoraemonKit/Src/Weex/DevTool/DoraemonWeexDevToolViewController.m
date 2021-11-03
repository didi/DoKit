//
//  DoraemonWeexDevToolViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2019/6/6.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexDevToolViewController.h"
#import "DoraemonQRScanView.h"
#import "DoraemonDefine.h"
#import "WXDevTool.h"

@interface DoraemonWeexDevToolViewController ()<DoraemonQRScanDelegate>

@property (nonatomic, strong) DoraemonQRScanView *scanView;

@end

@implementation DoraemonWeexDevToolViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Weex DevTool";
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    DoraemonQRScanView *scaner = [[DoraemonQRScanView alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-self.bigTitleView.doraemon_bottom)];
    scaner.delegate = self;
    scaner.showScanLine = YES;
    scaner.showBorderLine = YES;
    scaner.showCornerLine = YES;
    scaner.scanRect = CGRectMake(scaner.doraemon_width/2-kDoraemonSizeFrom750(480)/2, kDoraemonSizeFrom750(195), kDoraemonSizeFrom750(480), kDoraemonSizeFrom750(480));
    [self.view addSubview:scaner];
    self.scanView = scaner;
    [scaner startScanning];
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self removeScanView];
}

- (void)removeScanView{
    if (self.scanView) {
        [self.scanView stopScanning];
        [self.scanView removeFromSuperview];
        self.scanView = nil;
    }
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

#pragma mark -- DoraemonQRScanDelegate
- (void)scanView:(DoraemonQRScanView *)scanView pickUpMessage:(NSString *)message{
    if(message.length>0){
        [self doUrl:message];
    }
}

- (void)scanView:(DoraemonQRScanView *)scanView aroundBrightness:(NSString *)brightnessValue{
    
}

@end
