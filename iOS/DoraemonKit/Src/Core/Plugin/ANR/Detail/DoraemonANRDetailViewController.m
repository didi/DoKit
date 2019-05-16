//
//  DoraemonANRDetailViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import "DoraemonANRDetailViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonANRDetailViewController ()

@property (nonatomic, strong) UILabel *anrTimeLabel;
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
    
    _anrTimeLabel = [[UILabel alloc] init];
    _anrTimeLabel.textColor = [UIColor doraemon_black_1];
    _anrTimeLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(16)];
    _anrTimeLabel.text = [NSString stringWithFormat:@"卡顿耗时 : %@s",_anrInfo[@"duration"]];
    _anrTimeLabel.sizeToFit;
    _anrTimeLabel.frame = CGRectMake(20, _contentLabel.doraemon_bottom+20, _anrTimeLabel.doraemon_width, _anrTimeLabel.doraemon_height);
    [self.view addSubview:_anrTimeLabel];
}


@end
