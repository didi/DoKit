//
//  DoraemonMetricsViewController.m
//  DoraemonKit
//
//  Created by xgb on 2019/1/10.
//

#import "DoraemonMetricsViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonDefine.h"
#import "DoraemonViewMetricsConfig.h"

@interface DoraemonMetricsViewController () <DoraemonSwitchViewDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;

@end

@implementation DoraemonMetricsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = DoraemonLocalizedString(@"元素边框线");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"元素边框线开关") switchOn:[DoraemonViewMetricsConfig defaultConfig].enable];
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
    [DoraemonViewMetricsConfig defaultConfig].enable = on;
}

@end
