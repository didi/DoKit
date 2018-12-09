//
//  DoreamonCrashListCell.m
//  DoraemonKit
//
//  Created by wenquan on 2018/11/22.
//

#import "DoreamonCrashListCell.h"
#import "DoraemonSandboxModel.h"
#import "DoraemonDefine.h"

@interface DoreamonCrashListCell ()

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UIImageView *arrowImageView;

@end

@implementation DoreamonCrashListCell

#pragma mark - Lifecycle

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor doraemon_black_1];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
        [self.contentView addSubview:_titleLabel];
        
        _arrowImageView = [[UIImageView alloc] initWithImage:[UIImage doraemon_imageNamed:@"doraemon_more"]];
        _arrowImageView.frame = CGRectMake(DoraemonScreenWidth-kDoraemonSizeFrom750(32)-_arrowImageView.doraemon_width, [[self class] cellHeight]/2-_arrowImageView.doraemon_height/2, _arrowImageView.doraemon_width, _arrowImageView.doraemon_height);
        [self.contentView addSubview:_arrowImageView];
        
    }
    return self;
}

#pragma mark - Public

- (void)renderUIWithData:(DoraemonSandboxModel *)model {
    self.titleLabel.text = @"";
    if ([model.name isKindOfClass:[NSString class]] && (model.name.length > 0)) {
        self.titleLabel.text = model.name;
        [self.titleLabel sizeToFit];
        CGFloat w = self.titleLabel.doraemon_width;
        if (w > DoraemonScreenWidth-kDoraemonSizeFrom750(120)) {
            w = DoraemonScreenWidth-kDoraemonSizeFrom750(120);
        }
        self.titleLabel.frame = CGRectMake(kDoraemonSizeFrom750(32), [[self class] cellHeight]/2-self.titleLabel.doraemon_height/2, w, self.titleLabel.doraemon_height);
    }
}

+ (CGFloat)cellHeight{
    return kDoraemonSizeFrom750(104);
}

@end
