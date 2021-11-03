//
//  DoraemonHealthEndInputView.m
//  DoraemonKit
//
//  Created by didi on 2020/1/8.
//

#import "DoraemonHealthEndInputView.h"
#import "DoraemonDefine.h"

@implementation DoraemonHealthEndInputView


- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        CGFloat padding = kDoraemonSizeFrom750_Landscape(40);
        _label = [[UILabel alloc] initWithFrame:CGRectMake(padding, 0, self.doraemon_width - padding*2, padding + kDoraemonSizeFrom750_Landscape(5))];
        _label.textAlignment = NSTextAlignmentLeft;
        _label.textColor = [UIColor doraemon_black_3];
        _label.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        
        _textField = [[UITextField alloc] initWithFrame:CGRectMake(padding, _label.doraemon_bottom, self.doraemon_width- padding*2, padding*2)];
        _textField.textAlignment = NSTextAlignmentLeft;
        _textField.font = _label.font;
        _textField.layer.borderColor = [UIColor doraemon_black_1].CGColor;
        _textField.borderStyle = UITextBorderStyleRoundedRect;
        _textField.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(8);
        
        [self addSubview:_label];
        [self addSubview:_textField];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)tip placeholder:(NSString *)placeholder{
    _label.text = tip;
    _textField.placeholder = placeholder;
}

@end
