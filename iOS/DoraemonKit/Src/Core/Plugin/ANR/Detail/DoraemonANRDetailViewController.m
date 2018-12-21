//
//  DoraemonANRDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonANRDetailViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonANRDetailViewController ()

@property (nonatomic, strong) UILabel *contentLabel;

@end

@implementation DoraemonANRDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"卡顿详情");
    
    _contentLabel = [[UILabel alloc] init];
    _contentLabel.textColor = [UIColor doraemon_black_2];
    _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(16)];
    _contentLabel.numberOfLines = 0;
    _contentLabel.text = _anrInfo[@"content"];
    
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(self.view.doraemon_width-40, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(20, IPHONE_NAVIGATIONBAR_HEIGHT, fontSize.width, fontSize.height);
    [self.view addSubview:_contentLabel];
    
}


@end
