//
//  DoraemonMethodUseTimeViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import "DoraemonMethodUseTimeViewController.h"
#import "DoraemonCellSwitch.h"
#import "UIView+Doraemon.h"
#import "DoraemonCellButton.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonMethodUseTimeManager.h"
#import "DoraemonMethodUseTimeListViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonMethodUseTimeViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonMethodUseTimeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"Load耗时");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"Load耗时检测开关") switchOn:[DoraemonMethodUseTimeManager sharedInstance].on];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"查看检测记录")];
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
    [DoraemonAlertUtil handleAlertActionWithVC:self okBlock:^{
        [DoraemonMethodUseTimeManager sharedInstance].on = on;
        exit(0);
    } cancleBlock:^{
        weakSelf.switchView.switchView.on = !on;
    }];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonMethodUseTimeListViewController *vc = [[DoraemonMethodUseTimeListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
