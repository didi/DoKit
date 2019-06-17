//
//  DoraemonImageDetectionCell.m
//  DoraemonKit
//
//  Created by licd on 2019/5/17.
//

#import "DoraemonImageDetectionCell.h"
#import "DoraemonResponseImageModel.h"

@interface DoraemonImageDetectionCell()
@property (nonatomic, strong) UIImageView *imageView1;
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
    self.imageView1.image = [UIImage imageWithData: model.data];
    self.sizeLabel.text = [NSString stringWithFormat: @"size: %@", model.size];
}

- (void) initUI {
    CGFloat buttonWidth = 44;
    CGFloat space = 8;
    
    self.imageView1 = [[UIImageView alloc] initWithFrame: CGRectMake(space, space, 100, 100)];
    self.imageView1.backgroundColor = [UIColor colorWithRed: 0 green: 0 blue:0 alpha: 0.3];
    self.imageView1.contentMode = UIViewContentModeScaleAspectFit;
    [self addSubview: self.imageView1];
    
    UIView *imageInfoView = [[UIView alloc] initWithFrame: CGRectMake(self.imageView1.doraemon_width + (space * 2), self.imageView1.doraemon_y, DoraemonScreenWidth - self.imageView1.doraemon_right - (space * 2), self.imageView1.doraemon_height)];
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
