//
//  DoraemonLoggerConsoleSearchView.m
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLoggerConsoleSearchView.h"
#import <UIView+Positioning/UIView+Positioning.h>

@interface DoraemonLoggerConsoleSearchView()<UITextFieldDelegate>

@property (nonatomic, strong) UITextField *textField;

@end

@implementation DoraemonLoggerConsoleSearchView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _textField = [[UITextField alloc] initWithFrame:CGRectMake(20, 0, self.width-40, self.height)];
        _textField.layer.borderWidth = 1.;
        _textField.layer.borderColor = [UIColor orangeColor].CGColor;
        _textField.backgroundColor = [UIColor lightGrayColor];
        _textField.layer.cornerRadius = 2;
        _textField.clipsToBounds = YES;
        _textField.placeholder = @"请输入您要搜索的关键字";
        _textField.delegate = self;
        [_textField addTarget:self
                      action:@selector(textFieldDidChange:)
            forControlEvents:UIControlEventEditingChanged];
        [self addSubview:_textField];
    }
    return self;
}

- (void)textFieldDidChange:(UITextField *)textField{
    NSLog(@"%@",textField.text);
    if (self.delegate && [self.delegate respondsToSelector:@selector(searchViewCurrentText:)]) {
        [self.delegate searchViewCurrentText:textField.text];
    }
}

- (void)clearSearchView{
    _textField.text = nil;
}

@end
