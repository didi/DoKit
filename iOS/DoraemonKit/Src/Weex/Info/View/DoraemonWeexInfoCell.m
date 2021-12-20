//
//  DoraemonWeexInfoCell.m
//  DoraemonKit
//
//  Created by yixiang on 2019/6/5.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexInfoCell.h"
#import "DoraemonDefine.h"

@interface DoraemonWeexInfoCell()

@property (nonatomic, strong) UILabel *leftLabel;
@property (nonatomic, strong) UILabel *rightLabel;

@end

@implementation DoraemonWeexInfoCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _leftLabel = [[UILabel alloc] init];
        _leftLabel.font = [UIFont systemFontOfSize:14];
        _leftLabel.textColor = [UIColor doraemon_black_2];
        [self.contentView addSubview:_leftLabel];
        
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.font = [UIFont systemFontOfSize:14];
        _rightLabel.textColor = [UIColor doraemon_black_2];
        [self.contentView addSubview:_rightLabel];
    }
    return self;
}

- (void)renderCellWithTitle:(NSString *)title content:(NSString *)content{
    CGFloat cellHeight = [[self class] cellHeight];
    
    _leftLabel.text = title;
    [_leftLabel sizeToFit];
    _leftLabel.frame = CGRectMake(15,cellHeight/2-_leftLabel.doraemon_height/2 , _leftLabel.doraemon_width, _leftLabel.doraemon_height);
    
    _rightLabel.text = content;
    [_rightLabel sizeToFit];
    _rightLabel.frame = CGRectMake(DoraemonScreenWidth-15-_rightLabel.doraemon_width, cellHeight/2-_rightLabel.doraemon_height/2, _rightLabel.doraemon_width, _rightLabel.doraemon_height);
}

+ (CGFloat)cellHeight{
    return kDoraemonSizeFrom750(88);
}

@end
