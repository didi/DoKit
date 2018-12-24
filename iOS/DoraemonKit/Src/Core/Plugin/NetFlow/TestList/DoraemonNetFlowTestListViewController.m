//
//  DoraemonNetFlowTestListViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/8.
//

#import "DoraemonNetFlowTestListViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonNetFlowTestListViewController ()

@end

@implementation DoraemonNetFlowTestListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"检测记录";
    
    UILabel *titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, IPHONE_NAVIGATIONBAR_HEIGHT+kDoraemonSizeFrom750(40), self.view.doraemon_width, kDoraemonSizeFrom750(40))];
    titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
    titleLabel.textColor = [UIColor doraemon_black_1];
    titleLabel.text = @"正在开发中,敬请期待";
    titleLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:titleLabel];
}



@end
