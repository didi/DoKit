//
//  DoraemonWeakNetworkInputView.m
//  AFNetworking
//
//  Created by didi on 2019/12/16.
//

#import "DoraemonWeakNetworkInputView.h"
#import "DoraemonDefine.h"

@interface DoraemonWeakNetworkInputView()<UITextFieldDelegate>

@property (nonatomic, strong) UILabel *inputTitle;
@property (nonatomic, strong) UILabel *inputEpilog;
@property (nonatomic, strong) UITextField *speedInput;
@property (nonatomic, copy) DoraemonNetWeakInputBlock block;

@end

@implementation DoraemonWeakNetworkInputView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        
        _inputTitle = [[UILabel alloc] init];
        _speedInput = [[UITextField alloc] init];
        _speedInput.textAlignment = NSTextAlignmentCenter;
        _speedInput.delegate = self;
        _speedInput.keyboardType = UIKeyboardTypeNumberPad;
        _inputEpilog = [[UILabel alloc] init];
        _inputTitle.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        
        [self addSubview:_inputTitle];
        [self addSubview:_speedInput];
        [self addSubview:_inputEpilog];
    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title end:(NSString *)epilog{
    _inputTitle.text = title;
    CGSize size = [title sizeWithAttributes:@{
        @"NSFontAttributeName" : [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)]
    }];
    _inputTitle.frame = CGRectMake(0, 0,size.width + kDoraemonSizeFrom750_Landscape(58), self.doraemon_height);
    _inputEpilog.text = epilog;
    _speedInput.frame = CGRectMake(_inputTitle.doraemon_right, 0, kDoraemonSizeFrom750_Landscape(100), self.doraemon_height);
    _inputEpilog.frame = CGRectMake(_speedInput.doraemon_right + kDoraemonSizeFrom750_Landscape(32), 0, kDoraemonSizeFrom750_Landscape(100), self.doraemon_height);
}

- (void)renderUIWithSpeed:(long)speed define:(NSInteger)value{
    if(speed > 0){
        _speedInput.text = [NSString stringWithFormat:@"%ld",speed];
        _speedInput.textColor = [UIColor doraemon_black_1];
    }else{
        _speedInput.placeholder = [NSString stringWithFormat:@"%ld",(long)value];
        _speedInput.textColor = [UIColor lightGrayColor];
    }
}

- (long)getInputValue{
    return [_speedInput.text intValue];
}

- (void)addBlock:(DoraemonNetWeakInputBlock)block{
    self.block = block;
}

#pragma mark - UITextFieldDelegate
- (void)textFieldDidEndEditing:(UITextField *)textField{
    if(self.block){
        self.block();
    }
}

#pragma mark - UITextFieldDelegate
- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if(_speedInput.text.length == 0){
        if([@"0" isEqualToString:string] || [@"0" isEqualToString:[string substringToIndex:1]])
             return NO;
    }
    return [self validateNumber:string];
}

//键盘输入
- (BOOL)validateNumber:(NSString*)number {
    BOOL res = YES;
    NSCharacterSet* tmpSet = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];
    int i = 0;
    while (i < number.length) {
        NSString * string = [number substringWithRange:NSMakeRange(i, 1)];
        NSRange range = [string rangeOfCharacterFromSet:tmpSet];
        if (range.length == 0) {
            res = NO;
            break;
        }
        i++;
    }
    if(res){
        _speedInput.textColor = [UIColor doraemon_black_1];
    }
    return res;
}

@end
