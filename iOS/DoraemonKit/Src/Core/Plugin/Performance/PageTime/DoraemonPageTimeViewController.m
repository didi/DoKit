//
//  DoraemonPageTimeViewController.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/28.
//

#import "DoraemonPageTimeViewController.h"
#import "DoraemonCellSwitch.h"
//#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "NSObject+Doraemon.h"
#import "DoraemonManager.h"
#import <objc/runtime.h>
#import "DoraemonHealthManager.h"

@interface DoraemonPageTimeViewController ()<DoraemonSwitchViewDelegate>
@property (nonatomic, strong) DoraemonCellSwitch *switchView;

@end

@implementation DoraemonPageTimeViewController


- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"页面耗时");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"开关") switchOn:[[DoraemonCacheManager sharedInstance] startTimeSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    __weak typeof(self) weakSelf = self;
    [DoraemonAlertUtil handleAlertActionWithVC:self text:@"是否开启页面时长统计" okBlock:^{
        [[DoraemonCacheManager sharedInstance] savePageTimeSwitch:on];
    } cancleBlock:^{
        weakSelf.switchView.switchView.on = !on;
    }];
}

@end
