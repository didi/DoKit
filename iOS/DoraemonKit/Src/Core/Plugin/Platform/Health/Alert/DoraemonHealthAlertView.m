//
//  DoraemonHealthAlertView.m
//  DoraemonKit
//
//  Created by didi on 2020/1/8.
//

#import "DoraemonHealthAlertView.h"
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonHealthEndInputView.h"

@interface DoraemonHealthAlertView()<UITextFieldDelegate>

@property (nonatomic, assign) CGFloat width;
@property (nonatomic, assign) CGFloat height;
@property (nonatomic, assign) CGFloat padding;
@property (nonatomic, strong) UILabel *title;
@property (nonatomic, strong) UIView *alertView;
@property (nonatomic, strong) NSMutableArray *inputViewArray;
@property (nonatomic, strong) UIButton *okBtn;
@property (nonatomic, strong) UIButton *cancleBtn;
@property (nonatomic, strong) UIButton *quitBtn;
@property (nonatomic, copy) DoraemonHealthAlertOKActionBlock okBlock;
@property (nonatomic, copy) DoraemonHealthAlertCancleActionBlock cancleBlock;
@property (nonatomic, copy) DoraemonHealthAlertQuitActionBlock quitBlock;

@end

@implementation DoraemonHealthAlertView

- (instancetype)init{
    self = [super initWithFrame:[UIScreen mainScreen].bounds];
    if (self) {
        self.backgroundColor = [UIColor colorWithWhite:0.2 alpha:0.6];
        self.userInteractionEnabled = YES;
        _padding = kDoraemonSizeFrom750_Landscape(134);
        _width = self.doraemon_width - _padding*2;
        _height = _padding;
        _alertView = [[UIView alloc] initWithFrame:CGRectMake(_padding, self.doraemon_height/5, _width, _height)];
        _alertView.backgroundColor = [UIColor whiteColor];
        _alertView.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(10);
        
        _title = [[UILabel alloc] initWithFrame:CGRectMake(0, _padding/3, _width, _padding/2)];
        _title.textAlignment = NSTextAlignmentCenter;
        _title.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(30)];
        _title.textColor = [UIColor doraemon_black_1];
        
        _inputViewArray = [[NSMutableArray alloc] init];

        _okBtn = [[UIButton alloc] initWithFrame:CGRectMake(_width/3*2, 0, _width/3, kDoraemonSizeFrom750_Landscape(90))];
        _okBtn.titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(30)];
        [_okBtn.layer setBorderColor:[UIColor doraemon_black_3].CGColor];
        [_okBtn.layer setBorderWidth:kDoraemonSizeFrom750_Landscape(0.5)];
        [_okBtn.layer setMasksToBounds:YES];
        [_okBtn setTitleColor:[UIColor doraemon_black_3] forState:UIControlStateNormal];
        [_okBtn setTitle:DoraemonLocalizedString(@"确定") forState:UIControlStateNormal];
        _okBtn.enabled = NO;
        [_okBtn addTarget:self action:@selector(okBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        
        _quitBtn = [[UIButton alloc] initWithFrame:CGRectMake(_width/3, 0, _width/3, kDoraemonSizeFrom750_Landscape(90))];
        _quitBtn.titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(30)];
        [_quitBtn.layer setBorderColor:[UIColor doraemon_black_3].CGColor];
        [_quitBtn.layer setBorderWidth:kDoraemonSizeFrom750_Landscape(0.5)];
        [_quitBtn.layer setMasksToBounds:YES];
        [_quitBtn setTitleColor:[UIColor doraemon_black_3] forState:UIControlStateNormal];
        [_quitBtn setTitle:DoraemonLocalizedString(@"丢弃") forState:UIControlStateNormal];
        [_quitBtn addTarget:self action:@selector(quitBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        
        _cancleBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, _width/3, _okBtn.doraemon_height)];
        _cancleBtn.titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(30)];
        [_cancleBtn.layer setBorderColor:[UIColor doraemon_black_3].CGColor];
        [_cancleBtn.layer setBorderWidth:kDoraemonSizeFrom750_Landscape(0.5)];
        [_cancleBtn.layer setMasksToBounds:YES];
        [_cancleBtn setTitleColor:[UIColor doraemon_black_3] forState:UIControlStateNormal];
        [_cancleBtn setTitle:DoraemonLocalizedString(@"取消") forState:UIControlStateNormal];
        [_cancleBtn addTarget:self action:@selector(cancleBtnAction:) forControlEvents:UIControlEventTouchUpInside];
        
        [self addSubview:_alertView];
        
        [_alertView addSubview:_title];
        [_alertView addSubview:_okBtn];
        [_alertView addSubview:_quitBtn];
        [_alertView addSubview:_cancleBtn];
        
    }
    return self;
}

- (void)renderUI:(NSString *)title placeholder:(NSArray*)placeholders inputTip:(NSArray*)inputTips ok:(NSString *)okText quit:(NSString *)quitText cancle:(NSString *)cancleText  okBlock:(DoraemonHealthAlertOKActionBlock)okBlock quitBlock:(DoraemonHealthAlertQuitActionBlock) quitBlock
cancleBlock:(DoraemonHealthAlertCancleActionBlock)cancleBlock{
    int index = 0;
    _title.text = title;
    NSString *placeholder = nil;
    for (NSString* inputTip in inputTips) {
        DoraemonHealthEndInputView *inputView = [[DoraemonHealthEndInputView alloc] initWithFrame:CGRectMake(0, _height, _width, _padding)];
        if(index < placeholders.count){
            placeholder = placeholders[index];
        }else{
            placeholder = @"";
        }
        [inputView renderUIWithTitle:inputTip placeholder:placeholder];
        inputView.textField.delegate = self;
        [_alertView addSubview:inputView];
        [_inputViewArray addObject:inputView];
        _height += _padding + kDoraemonSizeFrom750_Landscape(10);
        index++;
    }
    _height += kDoraemonSizeFrom750_Landscape(38);
    _okBtn.frame = CGRectMake(_okBtn.doraemon_x, _height, _okBtn.doraemon_width, _okBtn.doraemon_height);
    if(okText.length>0){
        [_okBtn setTitle:okText forState:UIControlStateNormal];
    }
    _quitBtn.frame = CGRectMake(_quitBtn.doraemon_x, _height, _quitBtn.doraemon_width, _quitBtn.doraemon_height);
    if (quitText.length>0) {
        [_quitBtn setTitle:quitText forState:UIControlStateNormal];
    }
    _cancleBtn.frame = CGRectMake(_cancleBtn.doraemon_x, _height, _cancleBtn.doraemon_width, _cancleBtn.doraemon_height);
    if(cancleText.length>0){
        [_cancleBtn setTitle:cancleText forState:UIControlStateNormal];
    }
    _height += _okBtn.doraemon_height;
    _alertView.frame = CGRectMake(_padding, _alertView.doraemon_y, _width, _height);
    self.okBlock = okBlock;
    self.cancleBlock = cancleBlock;
    self.quitBlock = quitBlock;
}

- (NSArray *)getInputText{
    NSMutableArray *array = [[NSMutableArray alloc] init];
    for (DoraemonHealthEndInputView *inputView in _inputViewArray) {
        [array addObject:inputView.textField.text];
    }
    return array;
}

- (void)cancleBtnAction:(id)sender{
    self.cancleBlock ? self.cancleBlock() : nil;
    self.hidden = YES;
}

- (void)okBtnAction:(id)sender{
    self.okBlock ? self.okBlock() : nil;
    self.hidden = YES;
}

- (void)quitBtnAction:(id)sender{
    self.quitBlock ? self.quitBlock() : nil;
    self.hidden = YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField{
    BOOL enabled = YES;
    for (DoraemonHealthEndInputView *inputView in _inputViewArray) {
        if(inputView.textField.text.length <= 0){
            enabled = NO;
            break;
        }
    }
    _okBtn.enabled = enabled;
    if(enabled){
        [_okBtn setTitleColor:[UIColor doraemon_black_1] forState:UIControlStateNormal];
    }else{
        [_okBtn setTitleColor:[UIColor doraemon_black_3] forState:UIControlStateNormal];
    }
}
@end
