//
//  DoraemonWeakNetworkViewController.m
//  DoraemonKit
//
//  Created by didi on 2019/11/21.
//

#import "DoraemonWeakNetworkViewController.h"
#import "DoraemonWeakNetworkManager.h"
#import "DoraemonCellSwitch.h"
#import "DoraemonWeakNetworkDetailView.h"
#import "DoraemonDefine.h"
#import "DoraemonToastUtil.h"
#import "DoraemonCacheManager.h"
#import "DoraemonWeakNetworkWindow.h"

@interface DoraemonWeakNetworkViewController()<DoraemonWeakNetworkWindowDelegate>

@property (nonatomic, strong) DoraemonCellSwitch *weakSwitchView;
@property (nonatomic, strong) DoraemonWeakNetworkDetailView *detail;

@end

@implementation DoraemonWeakNetworkViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"模拟弱网测试");
    
    _weakSwitchView = [[DoraemonCellSwitch alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_weakSwitchView renderUIWithTitle:DoraemonLocalizedString(@"弱网模式") switchOn:[DoraemonWeakNetworkManager shareInstance].shouldWeak];
    [_weakSwitchView.switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    [_weakSwitchView needDownLine];
    [self.view addSubview:_weakSwitchView];
    [DoraemonWeakNetworkWindow shareInstance].delegate = self;
    _detail = [[DoraemonWeakNetworkDetailView alloc] initWithFrame:CGRectMake(0, _weakSwitchView.doraemon_bottom , self.view.doraemon_width, self.view.doraemon_height - _weakSwitchView.doraemon_bottom)];
    _detail.hidden = ![DoraemonWeakNetworkManager shareInstance].shouldWeak;
    [self.view addSubview:_detail];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)switchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    if([[DoraemonCacheManager sharedInstance] healthStart]){
        switchButton.on = NO;
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"App当前处于健康体检状态，无法进行此操作") inView:self.view];
        return ;
    }
    [DoraemonWeakNetworkManager shareInstance].shouldWeak = [switchButton isOn];
    
    [[DoraemonWeakNetworkManager shareInstance] canInterceptNetFlow:[switchButton isOn]];
    _detail.hidden = ![switchButton isOn];
    [DoraemonWeakNetworkWindow shareInstance].hidden = _detail.hidden;
    if([switchButton isOn]){
        [[DoraemonWeakNetworkManager shareInstance] startRecord];
    }else{
        [[DoraemonWeakNetworkManager shareInstance] endRecord];
    }
}

#pragma mark - DoraemonWeakNetworkWindowDelegate
- (void)doraemonWeakNetworkWindowClosed {
    _weakSwitchView.switchView.on = NO;
    _detail.hidden = YES;
}

@end
