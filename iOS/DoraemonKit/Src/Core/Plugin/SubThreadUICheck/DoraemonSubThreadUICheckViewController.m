//
//  DoraemonSubThreadUICheckViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/9/12.
//

#import "DoraemonSubThreadUICheckViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "DoraemonCacheManager.h"
#import "DoraemonSubThreadUICheckListViewController.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonDefine.h"

@interface DoraemonSubThreadUICheckViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonSubThreadUICheckViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"子线程渲染UI检查");
    
    _switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_switchView renderUIWithTitle:DoraemonLocalizedString(@"子线程UI渲染检测开关") switchOn:[[DoraemonCacheManager sharedInstance] subThreadUICheckSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
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
        [[DoraemonCacheManager sharedInstance] saveSubThreadUICheckSwitch:on];
        exit(0);
    } cancleBlock:^{
        weakSelf.switchView.switchView.on = !on;
    }];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonSubThreadUICheckListViewController *vc = [[DoraemonSubThreadUICheckListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }
}


@end
