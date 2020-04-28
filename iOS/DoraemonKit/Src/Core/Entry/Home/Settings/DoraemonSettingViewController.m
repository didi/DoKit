//
//  DoraemonSettingViewController.m
//  AFNetworking
//
//  Created by didi on 2020/4/24.
//

#import "DoraemonSettingViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonCellButton.h"
#import "DoraemonKitManagerViewController.h"

@interface DoraemonSettingViewController ()<DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellButton *kitManagerBtn;

@end

@implementation DoraemonSettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"设置");
    
    _kitManagerBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_kitManagerBtn renderUIWithTitle:DoraemonLocalizedString(@"工具管理")];
    _kitManagerBtn.delegate = self;
    [_kitManagerBtn needDownLine];
    [self.view addSubview:_kitManagerBtn];
}

#pragma mark -- DoraemonCellButtonDelegate
- (void)cellBtnClick:(id)sender{
    DoraemonKitManagerViewController *vc = [[DoraemonKitManagerViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}


@end
