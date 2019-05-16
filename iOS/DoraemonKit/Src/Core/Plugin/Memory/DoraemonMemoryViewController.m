//
//  DoraemonMemoryViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonMemoryOscillogramWindow.h"
#import "DoraemonMemoryOscillogramViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonDefine.h"

@interface DoraemonMemoryViewController ()

@property (nonatomic, strong) DoraemonCellSwitch *switchView;

@end

@implementation DoraemonMemoryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"内存检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"内存检测开关") switchOn:[[DoraemonCacheManager sharedInstance] memorySwitch]];
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
    [[DoraemonCacheManager sharedInstance] saveMemorySwitch:on];
    if(on){
        [[DoraemonMemoryOscillogramWindow shareInstance] show];
    }else{
        [[DoraemonMemoryOscillogramWindow shareInstance] hide];
    }
}

@end
