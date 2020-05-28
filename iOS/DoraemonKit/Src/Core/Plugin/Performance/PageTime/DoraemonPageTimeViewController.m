//
//  DoraemonPageTimeViewController.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/28.
//

#import "DoraemonPageTimeViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "NSObject+Doraemon.h"
#import "DoraemonManager.h"
#import <objc/runtime.h>
#import "DoraemonHealthManager.h"
#import "DoraemonPageTimeProfilerListViewController.h"

@interface DoraemonPageTimeViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>
@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

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
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    if ([[DoraemonCacheManager sharedInstance] pageTimeSwitch]){
        [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"页面耗时时间为")];
    }
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];

}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    __weak typeof(self) weakSelf = self;
    [DoraemonAlertUtil handleAlertActionWithVC:self text:@"页面耗时检测开关" okBlock:^{
        [[DoraemonCacheManager sharedInstance] savePageTimeSwitch:on];
    } cancleBlock:^{
        weakSelf.switchView.switchView.on = !on;
    }];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    DoraemonPageTimeProfilerListViewController *vc = [[DoraemonPageTimeProfilerListViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
