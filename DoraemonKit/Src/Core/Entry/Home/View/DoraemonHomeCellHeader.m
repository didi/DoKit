//
//  DoraemonHomeCellHeader.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "DoraemonHomeCellHeader.h"
#import "UIView+DoraemonPositioning.h"
#import "UIColor+DoreamonKit.h"

@interface DoraemonHomeCellHeader()

@property (nonatomic,strong) UILabel *titleLabel;

@end

@implementation DoraemonHomeCellHeader

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        UIView *leftMarkView = [[UIView alloc] initWithFrame:CGRectMake(0, 5., 5., 30)];
        leftMarkView.backgroundColor = [UIColor orangeColor];
        [self addSubview:leftMarkView];
        
        UIView *seperatorLine = [[UIView alloc] initWithFrame:CGRectMake(0, 39., self.doraemon_width, 1.)];
        seperatorLine.backgroundColor = [UIColor doraemon_colorWithHex:0xF2F3F4];
        [self addSubview:seperatorLine];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, self.doraemon_width-10, self.doraemon_height)];
        _titleLabel.font = [UIFont systemFontOfSize:14];
        [self addSubview:_titleLabel];
    }
    return self;
}

- (void)setTitle:(NSString *)title{
    self.titleLabel.text = title;
}

@end
