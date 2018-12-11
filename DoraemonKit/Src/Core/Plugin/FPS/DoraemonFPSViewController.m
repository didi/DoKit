//
//  DoraemonFPSViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/3.
//

#import "DoraemonFPSViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonFPSOscillogramWindow.h"
#import "DoraemonFPSOscillogramViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"
#import "DoraemonFPSListViewController.h"

@interface DoraemonFPSViewController ()

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonFPSViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"帧率检测");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"帧率检测开关") switchOn:[[DoraemonCacheManager sharedInstance] fpsSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(104))];
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
    [[DoraemonCacheManager sharedInstance] saveFpsSwitch:on];
    if(on){
        [[DoraemonFPSOscillogramWindow shareInstance] show];
    }else{
        [[DoraemonFPSOscillogramWindow shareInstance] hide];
    }
}


#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonFPSListViewController *vc = [[DoraemonFPSListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
