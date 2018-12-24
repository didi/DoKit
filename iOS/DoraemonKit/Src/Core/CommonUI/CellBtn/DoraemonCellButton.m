//
//  DoraemonCellButton.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"

@interface DoraemonCellButton()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *rightLabel;
@property (nonatomic, strong) UIView *topLine;
@property (nonatomic, strong) UIView *downLine;
@property (nonatomic, strong) UIImageView *arrowImageView;

@end

@implementation DoraemonCellButton

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
        [self addSubview:_titleLabel];
        
        _topLine = [[UIView alloc] init];
        _topLine.hidden = YES;
        _topLine.backgroundColor = [UIColor doraemon_line];
        [self addSubview:_topLine];
        
        _downLine = [[UIView alloc] init];
        _downLine.hidden = YES;
        _downLine.backgroundColor = [UIColor doraemon_line];
        [self addSubview:_downLine];
        
        _arrowImageView = [[UIImageView alloc] initWithImage:[UIImage doraemon_imageNamed:@"doraemon_more"]];
        _arrowImageView.frame = CGRectMake(self.doraemon_width-kDoraemonSizeFrom750(32)-_arrowImageView.doraemon_width, self.doraemon_height/2-_arrowImageView.doraemon_height/2, _arrowImageView.doraemon_width, _arrowImageView.doraemon_height);
        [self addSubview:_arrowImageView];
        
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.hidden = YES;
        _rightLabel.textColor = [UIColor doraemon_black_2];
        _rightLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
        [self addSubview:_rightLabel];
        
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
        self.userInteractionEnabled = YES;
        [self addGestureRecognizer:tap];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title{
    _titleLabel.text = title;
    [_titleLabel sizeToFit];
    _titleLabel.frame = CGRectMake(20, self.doraemon_height/2-_titleLabel.doraemon_height/2, _titleLabel.doraemon_width, _titleLabel.doraemon_height);
}

- (void)renderUIWithRightContent:(NSString *)rightContent{
    _rightLabel.hidden = NO;
    _rightLabel.text = rightContent;
    [_rightLabel sizeToFit];
    _rightLabel.frame = CGRectMake(_arrowImageView.doraemon_left-kDoraemonSizeFrom750(24)-_rightLabel.doraemon_width, self.doraemon_height/2-_rightLabel.doraemon_height/2, _rightLabel.doraemon_width, _rightLabel.doraemon_height);
}

- (void)needTopLine{
    _topLine.hidden = NO;
    _topLine.frame = CGRectMake(0, 0, self.doraemon_width, 0.5);
}

- (void)needDownLine{
    _downLine.hidden = NO;
    _downLine.frame = CGRectMake(0, self.doraemon_height-0.5, self.doraemon_width, 0.5);
}

- (void)tap{
    if (_delegate && [_delegate respondsToSelector:@selector(cellBtnClick:)]) {
        [_delegate cellBtnClick:self];
    }
}

@end
