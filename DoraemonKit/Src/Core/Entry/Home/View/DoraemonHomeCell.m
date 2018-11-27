//
//  DoraemonHomeCell.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "DoraemonHomeCell.h"
#import "UIView+DoraemonPositioning.h"
#import "UIImage+DoraemonKit.h"

@interface DoraemonHomeCell()

@property (nonatomic,strong) UIImageView *iconView;

@property (nonatomic,strong) UILabel *titleLabel;

@end

@implementation DoraemonHomeCell

- (instancetype)initWithFrame:(CGRect)frame{
    self =[super initWithFrame:frame];
    if (self) {
        _iconView = [[UIImageView alloc] initWithFrame:CGRectMake(self.doraemon_width/2-15, 10, 30, 30)];
        [self.contentView addSubview:_iconView];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, _iconView.doraemon_bottom+8., self.doraemon_width, 14.)];
        _titleLabel.font = [UIFont systemFontOfSize:14];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_titleLabel];
    }
    return self;
}

- (void)renderUIWithData : (NSDictionary *)data{
    NSString *iconName = data[@"icon"];
    _iconView.image = [UIImage doraemon_imageNamed:iconName];
    
    NSString *name = data[@"name"];
    _titleLabel.text = name;
}

@end
