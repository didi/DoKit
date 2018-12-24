//
//  DoraemonSubThreadUICheckDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/9/13.
//

#import "DoraemonSubThreadUICheckDetailViewController.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonDefine.h"

@interface DoraemonSubThreadUICheckDetailViewController ()

@property (nonatomic, strong) UILabel *contentLabel;

@end

@implementation DoraemonSubThreadUICheckDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"检测详情");
    
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textColor = [UIColor doraemon_black_2];
    _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(16)];
    _contentLabel.numberOfLines = 0;
    _contentLabel.text = _checkInfo[@"content"];
    
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(self.view.frame.size.width-40, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(20, IPHONE_NAVIGATIONBAR_HEIGHT, fontSize.width, fontSize.height);
    [self.view addSubview:_contentLabel];
}


@end
