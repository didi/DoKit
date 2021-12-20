//
//  DoraemonNetFlowDetailCell.m
//  DoraemonKit
//
//  Created by yixiang on 2018/4/19.
//

#import "DoraemonNetFlowDetailCell.h"
#import "DoraemonDefine.h"
#import "UIColor+Doraemon.h"

@interface DoraemonNetFlowDetailCell()

@property (nonatomic, strong) UITextView *contentLabel;
@property (nonatomic, strong) UIView *upLine;
@property (nonatomic, strong) UIView *downLine;

@end

@implementation DoraemonNetFlowDetailCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle =  UITableViewCellSelectionStyleNone;
        
        //大文本显示的时候，UIlabel在模拟器上会显示空白，使用TextView代替。
        //网上相似问题： https://blog.csdn.net/minghuyong2016/article/details/82882314
        _contentLabel = [DoraemonNetFlowDetailCell genTextView:16.0];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            self.backgroundColor = [UIColor systemBackgroundColor];
            
            _contentLabel.textColor = [UIColor labelColor];
        } else {
#endif
            _contentLabel.textColor = [UIColor blackColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        _contentLabel.editable = NO;
        [self.contentView addSubview:_contentLabel];
        
        _upLine = [[UIView alloc] init];
        _upLine.backgroundColor = [UIColor doraemon_colorWithHex:0xF2F2F2];
        [self.contentView addSubview:_upLine];
        _upLine.hidden = YES;
        
        _downLine = [[UIView alloc] init];
        _downLine.backgroundColor = [UIColor doraemon_colorWithHex:0xF2F2F2];
        [self.contentView addSubview:_downLine];
        _downLine.hidden = YES;
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    // 禁用 UITextView 滑动，解决其与 UITableView 的滑动冲突；
    // 放这里调用是因为在其他地方调用会出现文本未显示的问题(模拟器环境下)
    _contentLabel.scrollEnabled = false;
}

- (void)renderUIWithContent:(NSString *)content isFirst:(BOOL)isFirst isLast:(BOOL)isLast{
    _contentLabel.text = content;
    CGSize fontSize = [_contentLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-kDoraemonSizeFrom750_Landscape(32)*2, MAXFLOAT)];
    _contentLabel.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), kDoraemonSizeFrom750_Landscape(28), fontSize.width, fontSize.height);
    
    CGFloat cellHeight = [[self class] cellHeightWithContent:content];
    if(isFirst && isLast){
        _upLine.hidden = NO;
        _upLine.frame = CGRectMake(0, 0, DoraemonScreenWidth, 0.5);
        _downLine.hidden = NO;
        _downLine.frame = CGRectMake(0, cellHeight-0.5, DoraemonScreenWidth, 0.5);
    }else if(isFirst && !isLast){
        _upLine.hidden = NO;
        _upLine.frame = CGRectMake(0, 0, DoraemonScreenWidth, 0.5);
        _downLine.hidden = NO;
        _downLine.frame = CGRectMake(20, cellHeight-0.5, DoraemonScreenWidth-20, 0.5);
    }else if(!isFirst && isLast){
        _upLine.hidden = YES;
        _downLine.hidden = NO;
        _downLine.frame = CGRectMake(0, cellHeight-0.5, DoraemonScreenWidth, 0.5);
    }else{
        _upLine.hidden = YES;
        _downLine.hidden = NO;
        _downLine.frame = CGRectMake(20, cellHeight-0.5, DoraemonScreenWidth-20, 0.5);
    }
}

+ (CGFloat)cellHeightWithContent:(NSString *)content{
    UITextView *tempLabel = [DoraemonNetFlowDetailCell genTextView:16.0];
    tempLabel.text = content;
    CGSize fontSize = [tempLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-2*kDoraemonSizeFrom750_Landscape(32), MAXFLOAT)];
    return fontSize.height+kDoraemonSizeFrom750_Landscape(28)*2;
}

/// 生成 UITextView
+ (UITextView *)genTextView:(CGFloat)fontSize {
    UITextView *tempTextView = [[UITextView alloc] init];
    tempTextView.font = [UIFont systemFontOfSize:fontSize];
    return tempTextView;
}

@end
