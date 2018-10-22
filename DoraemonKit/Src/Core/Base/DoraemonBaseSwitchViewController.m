//
//  DoraemonBaseSwitchViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonBaseSwitchViewController.h"
#import "DoraemonDefine.h"
#import "UIView+Positioning.h"

@interface DoraemonBaseSwitchViewController ()

@end

@implementation DoraemonBaseSwitchViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)initUI{
    self.title = @"";
    
    UISwitch *switchView = [[UISwitch alloc] init];
    switchView.doraemon_origin = CGPointMake(DoraemonScreenWidth/2-switchView.doraemon_width/2, DoraemonScreenHeight/2-switchView.doraemon_height/2);
    [self.view addSubview:switchView];
    [switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
    switchView.on = [self switchViewOn];
    
    UILabel *tipLabel = [[UILabel alloc] init];
    tipLabel.font = [UIFont systemFontOfSize:16];
    tipLabel.textColor = [UIColor blackColor];
    tipLabel.text = @"点我右侧😘:  ";
    [self.view addSubview:tipLabel];
    [tipLabel sizeToFit];
    tipLabel.doraemon_origin = CGPointMake(switchView.doraemon_left-10-tipLabel.doraemon_width, DoraemonScreenHeight/2-tipLabel.doraemon_height/2);
}

- (BOOL)switchViewOn{
    return NO;
}

- (void)switchAction:(id)sender{
    
}

@end
