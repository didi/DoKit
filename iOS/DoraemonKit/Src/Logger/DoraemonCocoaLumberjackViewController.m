//
//  DoraemonCocoaLumberjackViewController.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/4.
//

#import "DoraemonCocoaLumberjackViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonCocoaLumberjackListViewController.h"

@interface DoraemonCocoaLumberjackViewController ()

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonCocoaLumberjackViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"CocoaLumberjack";
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"开关") switchOn:[[DoraemonCacheManager sharedInstance] loggerSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"查看记录")];
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
    [DoraemonToastUtil handleRestartActionWithVC:self restartBlock:^{
        [[DoraemonCacheManager sharedInstance] saveLoggerSwitch:on];
        exit(0);
    } cancleBlock:^{
         weakSelf.switchView.switchView.on = !on;
    }];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonCocoaLumberjackListViewController *vc = [[DoraemonCocoaLumberjackListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
