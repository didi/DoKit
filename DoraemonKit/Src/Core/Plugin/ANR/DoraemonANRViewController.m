//
//  DoraemonANRViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import "DoraemonANRViewController.h"
#import "DoraemonSwitchView.h"
#import "UIView+DoraemonPositioning.h"
#import "DoraemonANRManager.h"
#import "DoraemonCellButton.h"
#import "DoraemonANRListViewController.h"
#import "DoraemonToastUtil.h"

@interface DoraemonANRViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonSwitchView *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;
@property (nonatomic, strong) DoraemonCellButton *testBtn;

@end

@implementation DoraemonANRViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"卡顿检测";
    
    _switchView = [[DoraemonSwitchView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, 53)];
    [_switchView renderUIWithTitle:@"卡顿检测开关" switchOn:[DoraemonANRManager sharedInstance].anrTrackOn];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_cellBtn renderUIWithTitle:@"查看卡顿记录"];
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];
    
    _testBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _cellBtn.doraemon_bottom, self.view.doraemon_width, 53)];
    [_testBtn renderUIWithTitle:@"卡顿操作"];
    _testBtn.delegate = self;
    [_testBtn needDownLine];
    [self.view addSubview:_testBtn];
    
}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on{
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
    }else if(sender == _testBtn){
        [DoraemonToastUtil showToast:@"卡顿中"];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [NSThread sleepForTimeInterval:1.5];
        });
    }
}

@end
