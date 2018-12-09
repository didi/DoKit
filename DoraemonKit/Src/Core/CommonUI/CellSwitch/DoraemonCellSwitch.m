//
//  DoraemonSwitchView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import "DoraemonCellSwitch.h"
#import "DoraemonDefine.h"

@interface DoraemonCellSwitch()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIView *topLine;
@property (nonatomic, strong) UIView *downLine;

@end

@implementation DoraemonCellSwitch

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
        [self addSubview:_titleLabel];
        
        _switchView = [[UISwitch alloc] init];
        _switchView.onTintColor = [UIColor doraemon_blue];
        [_switchView addTarget:self action:@selector(switchChange:)forControlEvents:UIControlEventValueChanged];
        [self addSubview:_switchView];
        
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

- (void)renderUIWithTitle:(NSString *)title switchOn:(BOOL)on{
    _titleLabel.text = title;
    [_titleLabel sizeToFit];
    _titleLabel.frame = CGRectMake(20, self.doraemon_height/2-_titleLabel.doraemon_height/2, _titleLabel.doraemon_width, _titleLabel.doraemon_height);
    
    _switchView.on = on;
    _switchView.frame = CGRectMake(self.doraemon_width-20-_switchView.doraemon_width, self.doraemon_height/2-_switchView.doraemon_height/2, _switchView.doraemon_width, _switchView.doraemon_height);
}

- (void)needTopLine{
    _topLine.hidden = NO;
    _topLine.frame = CGRectMake(0, 0, self.doraemon_width, 0.5);
}

- (void)needDownLine{
    _downLine.hidden = NO;
    _downLine.frame = CGRectMake(0, self.doraemon_height-0.5, self.doraemon_width, 0.5);
}

- (void)switchChange:(UISwitch*)sender{
    BOOL on = sender.on;
    if (_delegate && [_delegate respondsToSelector:@selector(changeSwitchOn:sender:)]) {
        [_delegate changeSwitchOn:on sender:sender];
    }
}


@end
