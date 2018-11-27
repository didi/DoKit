//
//  DoraemonANRDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonANRDetailViewController.h"
#import "UIColor+DoreamonKit.h"
#import "UIView+DoraemonPositioning.h"

@interface DoraemonANRDetailViewController ()

@property (nonatomic, strong) UILabel *contentLabel;

@end

@implementation DoraemonANRDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"卡顿详情";
    
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
    _contentLabel.font = [UIFont systemFontOfSize:8];
    _contentLabel.numberOfLines = 0;
    _contentLabel.text = _anrInfo[@"content"];
    
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(self.view.frame.size.width-40, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(20, 20, fontSize.width, fontSize.height);
    [self.view addSubview:_contentLabel];
    
}


@end
