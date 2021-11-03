//
//  DoraemonMemoryOscillogramViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryOscillogramViewController.h"
#import "DoraemonOscillogramView.h"
#import "UIView+Doraemon.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonMemoryOscillogramWindow.h"

@interface DoraemonMemoryOscillogramViewController ()

@end

@implementation DoraemonMemoryOscillogramViewController

- (void)viewDidLoad {
    [super viewDidLoad];
}

- (NSString *)title{
    return DoraemonLocalizedString(@"内存检测");
}

- (NSString *)lowValue{
    return @"0";
}

- (NSString *)highValue{
    return [NSString stringWithFormat:@"%zi",[self deviceMemory]];
}

- (void)closeBtnClick{
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:NO];
    [[DoraemonMemoryOscillogramWindow shareInstance] hide];
}

//每一秒钟采样一次内存使用率
- (void)doSecondFunction{
    NSUInteger useMemoryForApp = [DoraemonMemoryUtil useMemoryForApp];
    NSUInteger totalMemoryForDevice = [self deviceMemory];
    
    // 0~totalMemoryForDevice   对应 高度0~_oscillogramView.doraemon_height
    [self.oscillogramView addHeightValue:useMemoryForApp*self.oscillogramView.doraemon_height/totalMemoryForDevice andTipValue:[NSString stringWithFormat:@"%zi",useMemoryForApp]];
}

- (NSUInteger)deviceMemory {
    return 1000;
}


@end
