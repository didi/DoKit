//
//  DoraemonMethodUseTimeListCell.m
//  AFNetworking
//
//  Created by yixiang on 2019/1/23.
//

#import "DoraemonMethodUseTimeListCell.h"
#import "DoraemonDefine.h"

@interface DoraemonMethodUseTimeListCell()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *rightLabel;

@end

@implementation DoraemonMethodUseTimeListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(28)];
        [self.contentView addSubview:_titleLabel];
        
        _rightLabel = [[UILabel alloc] init];
        _rightLabel.textColor = [UIColor doraemon_blue];
        _rightLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(28)];
        [self.contentView addSubview:_rightLabel];
    }
    return self;
}

- (void)renderCellWithData:(NSDictionary *)dic{
    self.titleLabel.text = dic[@"name"];
    [self.titleLabel sizeToFit];
    self.titleLabel.frame = CGRectMake(kDoraemonSizeFrom750(32), [[self class] cellHeight]/2-self.titleLabel.doraemon_height/2, DoraemonScreenWidth - 120, self.titleLabel.doraemon_height);
    self.titleLabel.adjustsFontSizeToFitWidth = YES;
    self.titleLabel.minimumScaleFactor = 0.1;

    
    CGFloat cost = [dic[@"cost"] floatValue];
    NSString *costString = [NSString stringWithFormat:@"%.3fms",cost];
    self.rightLabel.text = costString;
    [self.rightLabel sizeToFit];
    self.rightLabel.frame = CGRectMake(DoraemonScreenWidth - kDoraemonSizeFrom750(32) - self.rightLabel.doraemon_width, [[self class] cellHeight]/2-self.rightLabel.doraemon_height/2, self.rightLabel.doraemon_width, self.rightLabel.doraemon_height);
    
}

+ (CGFloat)cellHeight{
    return kDoraemonSizeFrom750(104);
}



@end
