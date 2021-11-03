//
//  DoraemonMLeaksFinderViewController.m
//  DoraemonKit
//
//  Created by didi on 2019/10/6.
//

#import "DoraemonMLeaksFinderViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"
#import "DoraemonCacheManager.h"
#import "DoraemonMLeaksFinderListViewController.h"


@interface DoraemonMLeaksFinderViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellSwitch *alertSwitchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonMLeaksFinderViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"内存泄漏");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"内存泄漏检测开关") switchOn:[[DoraemonCacheManager sharedInstance] memoryLeak]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _alertSwitchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, 53)];
    [_alertSwitchView renderUIWithTitle:DoraemonLocalizedString(@"内存泄漏检测弹框提醒") switchOn:[[DoraemonCacheManager sharedInstance] memoryLeakAlert]];
    [_alertSwitchView needDownLine];
    _alertSwitchView.delegate = self;
    [self.view addSubview:_alertSwitchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _alertSwitchView.doraemon_bottom, self.view.doraemon_width, 53)];
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
    if (sender == _switchView.switchView) {
        __weak typeof(self) weakSelf = self;
        [DoraemonAlertUtil handleAlertActionWithVC:self okBlock:^{
            [[DoraemonCacheManager sharedInstance] saveMemoryLeak:on];
            exit(0);
        } cancleBlock:^{
            weakSelf.switchView.switchView.on = !on;
        }];
    }else{
        [[DoraemonCacheManager sharedInstance] saveMemoryLeakAlert:on];
    }
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonMLeaksFinderListViewController *vc = [[DoraemonMLeaksFinderListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
