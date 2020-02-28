//
//  DoraemonCrashViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/19.
//

#import "DoraemonCrashViewController.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonCellButton.h"
#import "UIView+Doraemon.h"
#import "DoraemonCacheManager.h"
#import "DoraemonCrashListViewController.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonCrashTool.h"
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"

@interface DoraemonCrashViewController () <DoraemonSwitchViewDelegate, DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *switchView;
@property (nonatomic, strong) DoraemonCellButton *checkBtn;
@property (nonatomic, strong) DoraemonCellButton *clearBtn;

@end

@implementation DoraemonCrashViewController

#pragma mark - Lifecycle

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self commonInit];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)commonInit {
    self.title = DoraemonLocalizedString(@"Crash查看");
    
    self.switchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [self.switchView renderUIWithTitle:DoraemonLocalizedString(@"Crash日志收集开关") switchOn:[[DoraemonCacheManager sharedInstance] crashSwitch]];
    [self.switchView needDownLine];
    self.switchView.delegate = self;
    [self.view addSubview:self.switchView];
    
    self.checkBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, self.switchView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [self.checkBtn renderUIWithTitle:DoraemonLocalizedString(@"查看Crash日志")];
    self.checkBtn.delegate = self;
    [self.checkBtn needDownLine];
    [self.view addSubview:self.checkBtn];
    
    self.clearBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, self.checkBtn.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [self.clearBtn renderUIWithTitle:DoraemonLocalizedString(@"一键清理Crash日志")];
    self.clearBtn.delegate = self;
    [self.clearBtn needDownLine];
    [self.view addSubview:self.clearBtn];
}

#pragma mark - Delegate

#pragma mark DoraemonSwitchViewDelegate

- (void)changeSwitchOn:(BOOL)on sender:(id)sender{
    __weak typeof(self) weakSelf = self;
    [DoraemonAlertUtil handleAlertActionWithVC:self okBlock:^{
        [[DoraemonCacheManager sharedInstance] saveCrashSwitch:on];
        exit(0);
    } cancleBlock:^{
        weakSelf.switchView.switchView.on = !on;
    }];
}

#pragma mark DoraemonCellButtonDelegate

- (void)cellBtnClick:(id)sender {
    if (sender == self.checkBtn) {
        DoraemonCrashListViewController *vc = [[DoraemonCrashListViewController alloc] init];
        [self.navigationController pushViewController:vc animated:YES];
    } else if (sender == self.clearBtn) {
        UIAlertController * alertController = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"提示") message:DoraemonLocalizedString(@"确认删除所有崩溃日志吗？") preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        }];
        UIAlertAction *okAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            NSFileManager *fm = [NSFileManager defaultManager];
            if ([fm removeItemAtPath:[DoraemonCrashTool crashDirectory] error:nil]) {
                [DoraemonToastUtil showToast:DoraemonLocalizedString(@"删除成功") inView:self.view];
            } else {
                [DoraemonToastUtil showToast:DoraemonLocalizedString(@"删除失败") inView:self.view];
            }
        }];
        [alertController addAction:cancelAction];
        [alertController addAction:okAction];
        [self presentViewController:alertController animated:YES completion:nil];
    }
}

@end
