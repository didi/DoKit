//
//  DoraemonANRViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import "DoraemonANRViewController.h"
#import "DoraemonCellSwitch.h"
#import "UIView+Doraemon.h"
#import "DoraemonANRManager.h"
#import "DoraemonCellButton.h"
#import "DoraemonANRListViewController.h"
#import "DoraemonToastUtil.h"
#import "Doraemoni18NUtil.h"

@interface DoraemonANRViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonANRViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"卡顿检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"卡顿检测开关") switchOn:[DoraemonANRManager sharedInstance].anrTrackOn];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"查看卡顿记录")];
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    [DoraemonANRManager sharedInstance].anrTrackOn = on;
    if (on) {
        [[DoraemonANRManager sharedInstance] start];
    }else{
        [[DoraemonANRManager sharedInstance] stop];
    }
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonANRListViewController *vc = [[DoraemonANRListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
