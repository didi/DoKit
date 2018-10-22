//
//  DoraemonNetFlowViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/9.
//

#import "DoraemonNetFlowViewController.h"
#import "DoraemonCacheManager.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonDefine.h"
#import "UIView+Positioning.h"
#import "DoraemonNetFlowListViewController.h"
#import "DoraemonUtil.h"
#import "DoraemonNetFlowSummaryViewController.h"
#import "UIImage+DoraemonKit.h"
#import "UIColor+DoreamonKit.h"
#import "DoraemonNetFlowOscillogramWindow.h"

@interface DoraemonNetFlowViewController ()

@property (nonatomic, strong) UISwitch *totalSwitchView;
@property (nonatomic, strong) UISwitch *showOscillogramSwitchView;

@property (nonatomic, strong) UITabBarController *tabBar;

@end

@implementation DoraemonNetFlowViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI{
    self.title = @"流量监控开关";
    
    UISwitch *totalSwitchView = [[UISwitch alloc] init];
    totalSwitchView.doraemon_origin = CGPointMake(DoraemonScreenWidth/2-totalSwitchView.doraemon_width/2, 60);
    [self.view addSubview:totalSwitchView];
    [totalSwitchView addTarget:self action:@selector(totalSwitchAction:) forControlEvents:UIControlEventValueChanged];
    totalSwitchView.on = [self totalSwitchViewOn];
    _totalSwitchView = totalSwitchView;
    
    UILabel *totalTipLabel = [[UILabel alloc] init];
    totalTipLabel.font = [UIFont systemFontOfSize:16];
    totalTipLabel.textColor = [UIColor blackColor];
    totalTipLabel.text = @"流量监控开关:  ";
    [self.view addSubview:totalTipLabel];
    [totalTipLabel sizeToFit];
    totalTipLabel.doraemon_origin = CGPointMake(totalSwitchView.doraemon_left-10-totalTipLabel.doraemon_width, totalSwitchView.doraemon_centerY-totalTipLabel.doraemon_height/2);
    
    UISwitch *showOscillogramSwitchView = [[UISwitch alloc] init];
    showOscillogramSwitchView.doraemon_origin = CGPointMake(DoraemonScreenWidth/2-showOscillogramSwitchView.doraemon_width/2, 120);
    [self.view addSubview:showOscillogramSwitchView];
    [showOscillogramSwitchView addTarget:self action:@selector(showOscillogramSwitchAction:) forControlEvents:UIControlEventValueChanged];
    showOscillogramSwitchView.on = [self showOscillogramSwitchViewOn];
    _showOscillogramSwitchView = showOscillogramSwitchView;
    
    UILabel *showOscillogramTipLabel = [[UILabel alloc] init];
    showOscillogramTipLabel.font = [UIFont systemFontOfSize:16];
    showOscillogramTipLabel.textColor = [UIColor blackColor];
    showOscillogramTipLabel.text = @"流量曲线开关:  ";
    [self.view addSubview:showOscillogramTipLabel];
    [showOscillogramTipLabel sizeToFit];
    showOscillogramTipLabel.doraemon_origin = CGPointMake(showOscillogramSwitchView.doraemon_left-10-showOscillogramTipLabel.doraemon_width, showOscillogramSwitchView.doraemon_centerY-showOscillogramTipLabel.doraemon_height/2);
    
    
    UIButton *showNetFlowDetailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    showNetFlowDetailBtn.frame = CGRectMake(showOscillogramTipLabel.doraemon_left, _showOscillogramSwitchView.doraemon_bottom+40, _showOscillogramSwitchView.doraemon_right-showOscillogramTipLabel.doraemon_left, 40);
    [showNetFlowDetailBtn setTitle:@"显示流量监控详情" forState:UIControlStateNormal];
    showNetFlowDetailBtn.backgroundColor = [UIColor orangeColor];
    [showNetFlowDetailBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [showNetFlowDetailBtn addTarget:self action:@selector(showNetFlowDetail) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:showNetFlowDetailBtn];
}

- (BOOL)totalSwitchViewOn{
    return [[DoraemonCacheManager sharedInstance] netFlowSwitch];
}

- (BOOL)showOscillogramSwitchViewOn{
    return [[DoraemonCacheManager sharedInstance] netFlowShowOscillogramSwitch];
}

- (void)totalSwitchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:isButtonOn];
    if(isButtonOn){
        [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:YES];
    }else{
        [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:NO];
        if (_showOscillogramSwitchView.on) {
            _showOscillogramSwitchView.on = NO;
            [[DoraemonCacheManager sharedInstance] saveNetFlowShowOscillogramSwitch:NO];
        }
        [self hiddenOscillogramView];
    }
}

- (void)showOscillogramSwitchAction:(id)sender{
    UISwitch *switchButton = (UISwitch*)sender;
    BOOL isButtonOn = [switchButton isOn];
    [[DoraemonCacheManager sharedInstance] saveNetFlowShowOscillogramSwitch:isButtonOn];
    if(isButtonOn){
        if (!_totalSwitchView.on) {
            _totalSwitchView.on = YES;
            [[DoraemonCacheManager sharedInstance] saveNetFlowSwitch:YES];
            [[DoraemonNetFlowManager shareInstance] canInterceptNetFlow:YES];
        }
        
        [self showOscillogramView];
    }else{
        [self hiddenOscillogramView];
    }
}

- (void)showOscillogramView{
    [[DoraemonNetFlowOscillogramWindow shareInstance] show];
}

- (void)hiddenOscillogramView{
    [[DoraemonNetFlowOscillogramWindow shareInstance] hide];
}


- (void)showNetFlowDetail{
    UITabBarController *tabBar = [[UITabBarController alloc] init];
    tabBar.tabBar.backgroundColor = [UIColor whiteColor];
    _tabBar = tabBar;
    
    UIViewController *vc1 = [[DoraemonNetFlowSummaryViewController alloc] init];
    UINavigationController *nav1 = [[UINavigationController alloc] initWithRootViewController:vc1];
    nav1.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"流量监控概要" image:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_summary_unselect"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_summary_select"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] ];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x666666],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav1.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0xFF8800],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    
    UIViewController *vc2 = [[DoraemonNetFlowListViewController alloc] init];
    UINavigationController *nav2 = [[UINavigationController alloc] initWithRootViewController:vc2];
    nav2.tabBarItem = [[UITabBarItem alloc] initWithTitle:@"流量监控列表" image:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_list_unselect"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[[UIImage doraemon_imageNamed:@"doraemon_netflow_list_select"] doraemon_scaledToSize:CGSizeMake(30,30)] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0x666666],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateNormal];
    [nav2.tabBarItem setTitleTextAttributes:@{NSForegroundColorAttributeName:[UIColor doraemon_colorWithHex:0xFF8800],NSFontAttributeName:[UIFont systemFontOfSize:10]} forState:UIControlStateSelected];
    
    tabBar.viewControllers = @[nav1,nav2];
    
    [self presentViewController:tabBar animated:YES completion:nil];
}

@end
