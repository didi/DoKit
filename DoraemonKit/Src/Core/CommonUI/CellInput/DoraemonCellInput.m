//
//  DoraemonCellInput.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/7.
//

#import "DoraemonCellInput.h"
#import "DoraemonDefine.h"

@interface DoraemonCellInput()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIView *topLine;
@property (nonatomic, strong) UIView *downLine;

@end

@implementation DoraemonCellInput

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(32), 0, kDoraemonSizeFrom750(150), self.doraemon_height)];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
        [self addSubview:_titleLabel];
        
        _textField = [[UITextField alloc] initWithFrame:CGRectMake(_titleLabel.doraemon_right+kDoraemonSizeFrom750(30), self.doraemon_height/2-kDoraemonSizeFrom750(48)/2, self.doraemon_width-_titleLabel.doraemon_right-kDoraemonSizeFrom750(30), kDoraemonSizeFrom750(48))];
        [self addSubview:_textField];
        
        _topLine = [[UIView alloc] init];
        _topLine.hidden = YES;
        _topLine.backgroundColor = [UIColor doraemon_line];
        [self addSubview:_topLine];
        
        _downLine = [[UIView alloc] init];
        _downLine.hidden = YES;
        _downLine.backgroundColor = [UIColor doraemon_line];
        [self addSubview:_downLine];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title{
    _titleLabel.text = title;
}

- (void)renderUIWithPlaceholder:(NSString *)placeholder{
    _textField.placeholder = placeholder;
}

- (void)needTopLine{
    _topLine.hidden = NO;
    _topLine.frame = CGRectMake(0, 0, self.doraemon_width, 0.5);
}

- (void)needDownLine{
    _downLine.hidden = NO;
    _downLine.frame = CGRectMake(0, self.doraemon_height-0.5, self.doraemon_width, 0.5);
}

@end
