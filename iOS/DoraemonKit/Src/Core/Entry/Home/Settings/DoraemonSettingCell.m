//
//  DoraemonSettingCell.m
//  DoraemonKit
//
//  Created by yinhaichao on 2021/2/26.
//

#import "DoraemonSettingCell.h"

@interface DoraemonSettingCell ()

@property (nonatomic, strong) UILabel *titleLab;
@property (nonatomic, strong) UILabel *subTitleLab;

@end

@implementation DoraemonSettingCell

- (void)awakeFromNib {
    [super awakeFromNib];
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self.contentView addSubview:self.titleLab];
        [self.contentView addSubview:self.subTitleLab];
    }
    return self;
}

- (UILabel *)titleLab {
    if (!_titleLab) {
        _titleLab = [[UILabel alloc] initWithFrame:CGRectMake(20, 10, [UIScreen mainScreen].bounds.size.width - 60, 20)];
        _titleLab.font = [UIFont boldSystemFontOfSize:20];
    }
    return _titleLab;
}

- (UILabel *)subTitleLab {
    if (!_subTitleLab) {
        _subTitleLab = [[UILabel alloc] initWithFrame:CGRectMake(20, 30, [UIScreen mainScreen].bounds.size.width - 60, 70)];
        _subTitleLab.font = [UIFont systemFontOfSize:14];
        _subTitleLab.textColor = UIColor.lightGrayColor;
        _subTitleLab.numberOfLines = 3;
    }
    return _subTitleLab;
}

- (void)setCellData:(NSDictionary *)cellData {
    _cellData = cellData;
    _titleLab.text = [_cellData objectForKey:@"name"];
    _subTitleLab.text = [_cellData objectForKey:@"desc"];
}

@end
