//
//  DoraemonMLeaksFinderListCell.m
//  DoraemonKit
//
//  Created by didi on 2019/10/7.
//

#import "DoraemonMLeaksFinderListCell.h"
#import "DoraemonDefine.h"

@interface DoraemonMLeaksFinderListCell()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *rightLabel;

@end

@implementation DoraemonMLeaksFinderListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(28)];
        [self.contentView addSubview:_titleLabel];
    }
    return self;
}

- (void)renderCellWithData:(NSDictionary *)dic{
    self.titleLabel.text = dic[@"className"];
    [self.titleLabel sizeToFit];
    self.titleLabel.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(32), [[self class] cellHeight]/2-self.titleLabel.doraemon_height/2, DoraemonScreenWidth - 120, self.titleLabel.doraemon_height);
    self.titleLabel.adjustsFontSizeToFitWidth = YES;
    self.titleLabel.minimumScaleFactor = 0.1;
    
}

+ (CGFloat)cellHeight{
    return kDoraemonSizeFrom750_Landscape(104);
}


@end
