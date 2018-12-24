//
//  DoraemonNSLogViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/25.
//

#import "DoraemonNSLogViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonCacheManager.h"
#import "DoraemonNSLogListViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonNSLogViewController ()

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonNSLogViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"NSLog";
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"开关") switchOn:[[DoraemonCacheManager sharedInstance] nsLogSwitch]];
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
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"提示") message:DoraemonLocalizedString(@"该功能需要重启App才能生效") preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
         weakSelf.switchView.switchView.on = !on;
    }];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [[DoraemonCacheManager sharedInstance] saveNSLogSwitch:on];
        exit(0);
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [self presentViewController:alertController animated:YES completion:nil];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonNSLogListViewController *vc = [[DoraemonNSLogListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
