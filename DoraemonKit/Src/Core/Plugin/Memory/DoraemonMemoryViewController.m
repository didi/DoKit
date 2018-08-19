//
//  DoraemonMemoryViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonMemoryOscillogramWindow.h"

@interface DoraemonMemoryViewController ()

@end

@implementation DoraemonMemoryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"Memory使用量显示开关";
}

- (BOOL)switchViewOn{
    return [[DoraemonCacheManager sharedInstance] memorySwitch];
}

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:isButtonOn];
    if(isButtonOn){
        [[DoraemonMemoryOscillogramWindow shareInstance] show];
    }else{
        [[DoraemonMemoryOscillogramWindow shareInstance] hide];
    }
}



@end
