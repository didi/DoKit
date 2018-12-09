//
//  DoraemonCocoaLumberjackListCell.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/6.
//

#import "DoraemonCocoaLumberjackListCell.h"
#import "DoraemonDefine.h"
#import "DoraemonUtil.h"

@interface DoraemonCocoaLumberjackListCell()

@property (nonatomic, strong) UIImageView *arrowImageView;
@property (nonatomic, strong) UILabel *logLabel;

@end

@implementation DoraemonCocoaLumberjackListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _arrowImageView = [[UIImageView alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(27), [[self class] cellHeightWith:nil]/2-kDoraemonSizeFrom750(25)/2, kDoraemonSizeFrom750(25), kDoraemonSizeFrom750(25))];
        _arrowImageView.image = [UIImage doraemon_imageNamed:@"doraemon_expand_no"];
        _arrowImageView.contentMode = UIViewContentModeCenter;
        [self.contentView addSubview:_arrowImageView];
        
        _logLabel = [[UILabel alloc] init];
        _logLabel.textColor = [UIColor doraemon_black_1];
        _logLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)];
        [self.contentView addSubview:_logLabel];
    }
    return self;
}

- (void)renderCellWithData:(DoraemonDDLogMessage *)model{
    NSString *content;
    if (model && model.expand){
        NSString *log = model.message;
        NSString *time = [DoraemonUtil dateFormatNSDate:model.timestamp];
        content = [NSString stringWithFormat:@"%@\n触发时间: %@\n文件名称: %@\n所在行: %zi\n线程id: %@ \n线程名称: %@",log,time,model.fileName,model.line,model.threadId,model.threadName];
        _logLabel.numberOfLines = 0;
        _logLabel.text = content;
        CGSize size = [_logLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-kDoraemonSizeFrom750(32)*2-kDoraemonSizeFrom750(25)-kDoraemonSizeFrom750(12)*2, MAXFLOAT)];
        _logLabel.frame = CGRectMake(_arrowImageView.doraemon_right+kDoraemonSizeFrom750(12), [[self class] cellHeightWith:model]/2-size.height/2, size.width, size.height);
        
        _arrowImageView.image = [UIImage doraemon_imageNamed:@"doraemon_expand"];
    }else{
        _logLabel.numberOfLines = 1;
        _logLabel.text = model.message;
        _logLabel.frame = CGRectMake(_arrowImageView.doraemon_right+kDoraemonSizeFrom750(12), [[self class] cellHeightWith:model]/2-kDoraemonSizeFrom750(34)/2,DoraemonScreenWidth-kDoraemonSizeFrom750(32)*2-kDoraemonSizeFrom750(25)-kDoraemonSizeFrom750(12)*2 , kDoraemonSizeFrom750(34));
        _arrowImageView.image = [UIImage doraemon_imageNamed:@"doraemon_expand_no"];
    }
    
    
    
}

+ (CGFloat)cellHeightWith:(DoraemonDDLogMessage *)model{
    CGFloat cellHeight = kDoraemonSizeFrom750(60);
    if (model && model.expand) {
        NSString *log = model.message;
        NSString *time = [DoraemonUtil dateFormatNSDate:model.timestamp];
        NSString *content = [NSString stringWithFormat:@"%@\n触发时间: %@\n文件名称: %@\n所在行: %zi\n线程id: %@\n线程名称: %@ ",log,time,model.fileName,model.line,model.threadId,model.threadName];
        
        UILabel *logLabel = [[UILabel alloc] init];
        logLabel.textColor = [UIColor doraemon_black_1];
        logLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)];
        logLabel.text = content;
        logLabel.numberOfLines = 0;
        CGSize size = [logLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-kDoraemonSizeFrom750(32)*2-kDoraemonSizeFrom750(25)-kDoraemonSizeFrom750(12)*2, MAXFLOAT)];
        cellHeight = kDoraemonSizeFrom750(10) + size.height + kDoraemonSizeFrom750(10);
    }
    return cellHeight;
}


@end
