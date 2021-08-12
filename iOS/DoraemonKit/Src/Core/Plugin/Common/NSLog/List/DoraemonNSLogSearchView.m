//
//  DoraemonNSLogSearchView.m
//  DoraemonKit
//
//  Created by yixiang on 2018/12/3.
//

#import "DoraemonNSLogSearchView.h"
#import "DoraemonDefine.h"

@interface DoraemonNSLogSearchView()

@property (nonatomic, strong) UIImageView *searchIcon;
@property (nonatomic, strong) UITextField *textField;

@end

@implementation DoraemonNSLogSearchView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(8);
        self.layer.borderWidth = kDoraemonSizeFrom750_Landscape(2);
        self.layer.borderColor = [UIColor doraemon_colorWithHex:0x999999 andAlpha:0.2].CGColor;
        
        _searchIcon = [[UIImageView alloc] initWithImage:[UIImage doraemon_xcassetImageNamed:@"doraemon_search"]];
        _searchIcon.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(20), self.doraemon_height/2-_searchIcon.doraemon_height/2, _searchIcon.doraemon_width, _searchIcon.doraemon_height);
        [self addSubview:_searchIcon];
        
        _textField = [[UITextField alloc] initWithFrame:CGRectMake(_searchIcon.doraemon_right+kDoraemonSizeFrom750_Landscape(20), self.doraemon_height/2-kDoraemonSizeFrom750_Landscape(50)/2, self.doraemon_width-_searchIcon.doraemon_right-kDoraemonSizeFrom750_Landscape(20), kDoraemonSizeFrom750_Landscape(50))];
        _textField.placeholder = DoraemonLocalizedString(@"请输入您要搜索的关键字");
        [_textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
        [self addSubview:_textField];
    }
    return self;
}

-(void)textFieldDidChange:(id)sender{
    UITextField *senderTextField = (UITextField *)sender;
    //去除首尾空格
    NSString *textSearchStr = [senderTextField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    if (self.delegate && [self.delegate respondsToSelector:@selector(searchViewInputChange:)]) {
        [self.delegate searchViewInputChange:textSearchStr];
    }
}

@end
