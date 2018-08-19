//
//  DoraemonCPUViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonCPUViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonCPUOscillogramWindow.h"

@interface DoraemonCPUViewController ()

@end

@implementation DoraemonCPUViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"CPU使用率显示开关";
}

- (BOOL)switchViewOn{
    return [[DoraemonCacheManager sharedInstance] cpuSwitch];
}

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveCpuSwitch:isButtonOn];
    if(isButtonOn){
        [[DoraemonCPUOscillogramWindow shareInstance] show];
    }else{
        [[DoraemonCPUOscillogramWindow shareInstance] hide];
    }
}

@end
