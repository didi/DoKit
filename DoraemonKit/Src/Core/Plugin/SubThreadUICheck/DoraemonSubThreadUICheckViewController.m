//
//  DoraemonSubThreadUICheckViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/9/12.
//

#import "DoraemonSubThreadUICheckViewController.h"
#import "DoraemonSwitchView.h"
#import "DoraemonCellButton.h"
#import "UIView+Positioning.h"
#import "DoraemonCacheManager.h"
#import "DoraemonSubThreadUICheckListViewController.h"

@interface DoraemonSubThreadUICheckViewController ()<DoraemonSwitchViewDelegate,DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonSwitchView *switchView;
@property (nonatomic, strong) DoraemonCellButton *cellBtn;
@property (nonatomic, strong) DoraemonCellButton *testBtn;
@property (nonatomic, strong) UIView *testView;

@end

@implementation DoraemonSubThreadUICheckViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"子线程渲染UI检查";
    
    _switchView = [[DoraemonSwitchView alloc] initWithFrame:CGRectMake(0, 0, self.view.width, 53)];
    [_switchView renderUIWithTitle:@"子线程UI渲染检测开关" switchOn:[[DoraemonCacheManager sharedInstance] subThreadUICheckSwitch]];
    [_switchView needTopLine];
    [_switchView needDownLine];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _switchView.bottom, self.view.width, 53)];
    [_cellBtn renderUIWithTitle:@"查看检测记录"];
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];
    
    _testBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, _cellBtn.bottom, self.view.width, 53)];
    [_testBtn renderUIWithTitle:@"子线程UI渲染操作"];
    _testBtn.delegate = self;
    [_testBtn needDownLine];
    [self.view addSubview:_testBtn];
    
    _testView = [[UIView alloc] init];
    _testView.frame = CGRectMake(0, _testBtn.bottom+60, 100, 100);
    _testView.backgroundColor = [UIColor redColor];
    _testView.hidden = NO;
    [self.view addSubview:_testView];

}

#pragma mark -- DoraemonSwitchViewDelegate
- (void)changeSwitchOn:(BOOL)on{
    __weak typeof(self) weakSelf = self;
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:@"提示" message:@"该功能需要重启App才能生效" preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
         weakSelf.switchView.switchView.on = !on;
    }];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [[DoraemonCacheManager sharedInstance] saveSubThreadUICheckSwitch:on];
        exit(0);
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [self presentViewController:alertController animated:YES completion:nil];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    if (sender == _cellBtn) {
        DoraemonSubThreadUICheckListViewController *vc = [[DoraemonSubThreadUICheckListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    }else if(sender == _testBtn){
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            UIView *v = [UIView new];
            [self.view addSubview:v];
        });
    }
}


@end
