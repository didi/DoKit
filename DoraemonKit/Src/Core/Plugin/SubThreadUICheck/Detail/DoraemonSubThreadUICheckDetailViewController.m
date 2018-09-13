//
//  DoraemonSubThreadUICheckDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/9/13.
//

#import "DoraemonSubThreadUICheckDetailViewController.h"
#import "UIColor+DoreamonKit.h"
#import "UIView+Positioning.h"

@interface DoraemonSubThreadUICheckDetailViewController ()

@property (nonatomic, strong) UILabel *contentLabel;

@end

@implementation DoraemonSubThreadUICheckDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"检测详情";
    
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
    _contentLabel.font = [UIFont systemFontOfSize:8];
    _contentLabel.numberOfLines = 0;
    _contentLabel.text = _checkInfo[@"content"];
    
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(self.view.frame.size.width-40, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(20, 20, fontSize.width, fontSize.height);
    [self.view addSubview:_contentLabel];
}


@end
