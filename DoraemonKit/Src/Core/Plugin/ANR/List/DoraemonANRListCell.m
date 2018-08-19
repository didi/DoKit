//
//  DoraemonANRListCell.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import "DoraemonANRListCell.h"
#import "UIColor+DoreamonKit.h"
#import <UIView+Positioning/UIView+Positioning.h>

@interface DoraemonANRListCell()

@property (nonatomic, strong) UILabel *titleLabel;

@end

@implementation DoraemonANRListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_colorWithHexString:@"#666666"];
        _titleLabel.font = [UIFont systemFontOfSize:14];
        [self.contentView addSubview:_titleLabel];
    }
    return self;
}

- (void)renderCellWithData:(NSDictionary *)dic{
    _titleLabel.text = dic[@"title"];
    [_titleLabel sizeToFit];
    _titleLabel.frame = CGRectMake(20, [[self class] cellHeight]/2-_titleLabel.height/2, _titleLabel.width, _titleLabel.height);
}

+ (CGFloat)cellHeight{
    return 53;
}
@end
