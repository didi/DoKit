//
//  DoraemonImageDetectionCell.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/17.
//

#import "DoraemonImageDetectionCell.h"
#import "DoraemonResponseImageModel.h"
#import "DoraemonDefine.h"

@interface DoraemonImageDetectionCell()
@property (nonatomic, strong) UIImageView *previewImageView;
@property (nonatomic, strong) UILabel *urlLabel;
@property (nonatomic, strong) UILabel *sizeLabel;
@property (nonatomic, strong) UIButton *button;

@end

@implementation DoraemonImageDetectionCell

+ (CGFloat)cellHeight {
    return 116;
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    [self initUI];
    return self;
}

- (void)setupWithModel:(DoraemonResponseImageModel *)model {
    self.urlLabel.text = [model.url absoluteString];
    self.previewImageView.image = [UIImage imageWithData: model.data];
    self.sizeLabel.text = [NSString stringWithFormat: @"size: %@", model.size];
}

- (void) initUI {
    CGFloat space = 8;
    
    self.previewImageView = [[UIImageView alloc] initWithFrame: CGRectMake(space, space, 100, 100)];
    self.previewImageView.backgroundColor = [UIColor colorWithRed: 0 green: 0 blue:0 alpha: 0.3];
    self.previewImageView.contentMode = UIViewContentModeScaleAspectFit;
    [self addSubview: self.previewImageView];
    
    UIView *imageInfoView = [[UIView alloc] initWithFrame: CGRectMake(self.previewImageView.doraemon_width + (space * 2), self.previewImageView.doraemon_y, DoraemonScreenWidth - self.previewImageView.doraemon_right - (space * 2), self.previewImageView.doraemon_height)];
    [self addSubview: imageInfoView];
    
    self.sizeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, imageInfoView.doraemon_width, 15)];
    self.sizeLabel.textColor = [UIColor doraemon_black_1];
    self.sizeLabel.font = [UIFont systemFontOfSize: 11];
    [imageInfoView addSubview: self.sizeLabel];
    
    self.urlLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, self.sizeLabel.doraemon_bottom, imageInfoView.doraemon_width, 80)];
    [imageInfoView addSubview: self.urlLabel];
    self.urlLabel.lineBreakMode = NSLineBreakByCharWrapping;
    self.sizeLabel.textColor = [UIColor doraemon_black_1];
    self.urlLabel.numberOfLines = 5;
    self.urlLabel.font = [UIFont systemFontOfSize: 11];

}

@end
