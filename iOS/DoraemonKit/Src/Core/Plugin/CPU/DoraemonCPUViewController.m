//
//  DoraemonCPUViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonCPUViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonCPUOscillogramWindow.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonCPUOscillogramViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonDefine.h"

@interface DoraemonCPUViewController ()

@property (nonatomic, strong) DoraemonCellSwitch *switchView;

@end

@implementation DoraemonCPUViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"CPU检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"CPU检测开关") switchOn:[[DoraemonCacheManager sharedInstance] cpuSwitch]];
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
    [[DoraemonCacheManager sharedInstance] saveCpuSwitch:on];
    if(on){
        [[DoraemonCPUOscillogramWindow shareInstance] show];
    }else{
        [[DoraemonCPUOscillogramWindow shareInstance] hide];
    }
}

@end
