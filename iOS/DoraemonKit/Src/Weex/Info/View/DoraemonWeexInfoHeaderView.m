//
//  DoraemonWeexInfoHeaderView.m
//  DoraemonKit
//
//  Created by yixiang on 2019/6/5.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexInfoHeaderView.h"
#import "DoraemonDefine.h"

@interface DoraemonWeexInfoHeaderView()

@property (nonatomic, strong) UILabel *textLabel;
@property (nonatomic, strong) UIView *downLine;

@end

@implementation DoraemonWeexInfoHeaderView

- (instancetype)init{
    self = [super init];
    if (self) {
        _textLabel = [[UILabel alloc] init];
        _textLabel.numberOfLines = 0;
        _textLabel.textColor = [UIColor doraemon_black_1];
        _textLabel.font = [UIFont systemFontOfSize:12];
        [self addSubview:_textLabel];
        
        _downLine = [[UILabel alloc] init];
        _downLine.backgroundColor = [UIColor doraemon_line];
        [self addSubview:_downLine];
    }
    return self;
}

- (void)renderUIWithWxBundleUrl:(NSString *)wxBundleUrl{
    _textLabel.text = wxBundleUrl;
     CGSize size = [_textLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-30, CGFLOAT_MAX)];
    _textLabel.frame = CGRectMake(15, 10, size.width, size.height);
    self.frame = CGRectMake(0, 0, DoraemonScreenWidth, _textLabel.doraemon_height+20);
    
    _downLine.frame = CGRectMake(0, self.doraemon_height-0.5, self.doraemon_width, 0.5);
}

@end
