//
//  DoraemonMockGPSInputView.m
//  DoraemonKit
//
//  Created by yixiang on 2018/12/2.
//

#import "DoraemonMockGPSInputView.h"
#import "DoraemonDefine.h"

@interface DoraemonMockGPSInputView()

@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, strong) UIButton *searchBtn;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, strong) UILabel *exampleLabel;

@end

@implementation DoraemonMockGPSInputView


- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(8);
        
        _textField = [[UITextField alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(40), self.doraemon_width-2*kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(45))];
        _textField.placeholder = DoraemonLocalizedString(@"请输入经纬度");
        [_textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [self addSubview:_textField];
        
        _searchBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.doraemon_width-kDoraemonSizeFrom750_Landscape(120), 0, kDoraemonSizeFrom750_Landscape(120), kDoraemonSizeFrom750_Landscape(120))];
        _searchBtn.imageView.contentMode = UIViewContentModeCenter;
        [_searchBtn setImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_search"] forState:UIControlStateNormal];
        [_searchBtn addTarget:self action:@selector(searchBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:_searchBtn];
        
        _lineView = [[UIView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(32), _textField.doraemon_bottom+kDoraemonSizeFrom750_Landscape(19), self.doraemon_width-2*kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(1))];
        _lineView.backgroundColor = [UIColor doraemon_colorWithHexString:@"#EEEEEE"];
        [self addSubview:_lineView];
        
        _exampleLabel = [[UILabel alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(32),_lineView.doraemon_bottom+kDoraemonSizeFrom750_Landscape(15), self.doraemon_width-kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(33))];
        _exampleLabel.textColor = [UIColor doraemon_black_3];
        _exampleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        _exampleLabel.text = DoraemonLocalizedString(@"(示例: 120.15 30.28)");
        [self addSubview:_exampleLabel];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            self.backgroundColor = [UIColor systemBackgroundColor];
        } else {
#endif
            self.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
    }
    return self;
}

-(void)textFieldDidChange:(id)sender{
    UITextField *senderTextField = (UITextField *)sender;
    //去除首尾空格
    NSString *textSearchStr = [senderTextField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    if (textSearchStr.length > 0) {
        [_searchBtn setImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_search_highlight"] forState:UIControlStateNormal];
    }else{
        [_searchBtn setImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_search"] forState:UIControlStateNormal];
    }
}

- (void)searchBtnClick:(id)sender{
    //去除首尾空格
    NSString *textSearchStr = [_textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    if (textSearchStr.length>0) {
        if (_delegate && [_delegate respondsToSelector:@selector(inputViewOkClick:)]) {
            [_delegate inputViewOkClick:textSearchStr];
        }
    }
}
@end
