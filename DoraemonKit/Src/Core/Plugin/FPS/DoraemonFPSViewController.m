//
//  DoraemonFPSViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/3.
//

#import "DoraemonFPSViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonFPSOscillogramWindow.h"

@interface DoraemonFPSViewController ()

@end

@implementation DoraemonFPSViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"FPS显示开关";
}

- (BOOL)switchViewOn{
    return [[DoraemonCacheManager sharedInstance] fpsSwitch];
}

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:isButtonOn];
    if(isButtonOn){
        [[DoraemonFPSOscillogramWindow shareInstance] show];
    }else{
        [[DoraemonFPSOscillogramWindow shareInstance] hide];
    }
}

@end
