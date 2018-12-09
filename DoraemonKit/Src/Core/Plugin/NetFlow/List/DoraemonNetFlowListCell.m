//
//  DoraemonNetFlowListCell.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import "DoraemonNetFlowListCell.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"
#import "UIColor+Doraemon.h"
#import "DoraemonUtil.h"
#import "Doraemoni18NUtil.h"

static CGFloat const kFontSize = 10;

@interface DoraemonNetFlowListCell()

@property (nonatomic, strong) UILabel *urlLabel;//url信息
@property (nonatomic, strong) UILabel *methodLabel;//请求方式
@property (nonatomic, strong) UILabel *statusLabel;//请求状态
@property (nonatomic, strong) UILabel *startTimeLabel;//请求开始时间
@property (nonatomic, strong) UILabel *timeLabel;//请求耗时
@property (nonatomic, strong) UILabel *flowLabel;//流量信息

@end

@implementation DoraemonNetFlowListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
        self.urlLabel = [[UILabel alloc] init];
        self.urlLabel.font = [UIFont systemFontOfSize:kFontSize];
        self.urlLabel.textColor = [UIColor blackColor];
        self.urlLabel.numberOfLines = 0;
        [self.contentView addSubview:self.urlLabel];
        
        self.methodLabel = [[UILabel alloc] init];
        self.methodLabel.font = [UIFont systemFontOfSize:kFontSize];
        self.methodLabel.textColor = [UIColor whiteColor];
        self.methodLabel.backgroundColor = [UIColor doraemon_colorWithHex:0XD26282];
        self.methodLabel.layer.cornerRadius = 2;
        self.methodLabel.layer.masksToBounds = YES;
        [self.contentView addSubview:self.methodLabel];
        
        self.statusLabel = [[UILabel alloc] init];
        self.statusLabel.font = [UIFont systemFontOfSize:kFontSize];
        self.statusLabel.textColor = [UIColor blackColor];
        [self.contentView addSubview:self.statusLabel];
        
        self.startTimeLabel = [[UILabel alloc] init];
        self.startTimeLabel.font = [UIFont systemFontOfSize:kFontSize];
        self.startTimeLabel.textColor = [UIColor blackColor];
        [self.contentView addSubview:self.startTimeLabel];
        
        self.timeLabel = [[UILabel alloc] init];
        self.timeLabel.font = [UIFont systemFontOfSize:kFontSize];
        self.timeLabel.textColor = [UIColor blackColor];
        [self.contentView addSubview:self.timeLabel];
        
        self.flowLabel = [[UILabel alloc] init];
        self.flowLabel.font = [UIFont systemFontOfSize:kFontSize];
        self.flowLabel.textColor = [UIColor blackColor];
        [self.contentView addSubview:self.flowLabel];
    }
    return self;
}

- (void)renderCellWithModel:(DoraemonNetFlowHttpModel *)httpModel{
    CGFloat startY = 5,startX=10;
    NSString *urlString = httpModel.url;
    if (urlString.length>0){
        self.urlLabel.text = urlString;
        CGSize size = [self.urlLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-50, CGFLOAT_MAX)];
        self.urlLabel.frame = CGRectMake(startX, startY, DoraemonScreenWidth-40, size.height);
        startY += self.urlLabel.doraemon_height+2;
    }
    
    CGFloat height = 0;
    NSString *method = httpModel.method;
    NSString *status = httpModel.statusCode;
    if (method.length>0) {
        NSString *mineType = httpModel.mineType;
        if (mineType.length>0) {
            self.methodLabel.text = [NSString stringWithFormat:@" %@ > %@ ",method,mineType];
        }else{
            self.methodLabel.text = [NSString stringWithFormat:@" %@ ",method];
        }
        [self.methodLabel sizeToFit];
        self.methodLabel.frame = CGRectMake(10, startY, self.methodLabel.doraemon_width, self.methodLabel.doraemon_height);
        startX = self.methodLabel.doraemon_right+5;
        height = self.methodLabel.doraemon_height;
    }
    if (status.length>0) {
        self.statusLabel.text =[NSString stringWithFormat:@"[%@]",status];
        [self.statusLabel sizeToFit];
        self.statusLabel.frame = CGRectMake(startX, self.urlLabel.doraemon_bottom+2, self.statusLabel.doraemon_width, self.statusLabel.doraemon_height);
        height = self.statusLabel.doraemon_height;
    }
    if (method.length>0 || status.length>0) {
        startY += height + 2;
    }
    
    startX = 10;
    
    NSString *startTime = [DoraemonUtil dateFormatTimeInterval:httpModel.startTime];
    NSString *time = httpModel.totalDuration;
    if (startTime.length>0) {
        self.startTimeLabel.text = startTime;
        [self.startTimeLabel sizeToFit];
        self.startTimeLabel.frame = CGRectMake(startX, startY, self.startTimeLabel.doraemon_width, self.startTimeLabel.doraemon_height);
        startX = self.startTimeLabel.doraemon_right + 5;
        height = self.startTimeLabel.doraemon_height;
    }
    if (time.length>0) {
        self.timeLabel.text = [NSString stringWithFormat:@"%@:%@s",DoraemonLocalizedString(@"耗时"),time];
        [self.timeLabel sizeToFit];
        self.timeLabel.frame = CGRectMake(startX, startY, self.timeLabel.doraemon_width, self.timeLabel.doraemon_height);
        height = self.startTimeLabel.doraemon_height;
    }
    
    if (startTime.length>0 || time.length>0) {
        startY += height+2;
    }
    startX = 10;
    
    NSString *uploadFlow = [DoraemonUtil formatByte:[httpModel.uploadFlow floatValue]];
    NSString *downFlow = [DoraemonUtil formatByte:[httpModel.downFlow floatValue]];
    if(uploadFlow.length>0 || downFlow.length>0){
        NSMutableString *netflow = [NSMutableString string];
        if (uploadFlow.length>0) {
            [netflow appendString:[NSString stringWithFormat:@"↑ %@",uploadFlow]];
        }
        if (downFlow.length>0) {
            [netflow appendString:[NSString stringWithFormat:@"↓ %@",downFlow]];
        }
        
        self.flowLabel.text = netflow;
        [self.flowLabel sizeToFit];
        self.flowLabel.frame = CGRectMake(startX, startY, self.flowLabel.doraemon_width, self.flowLabel.doraemon_height);
    }
}

+ (CGFloat)cellHeightWithModel:(DoraemonNetFlowHttpModel *)httpModel{
    CGFloat height = 5;

    UILabel *tempLabel = [[UILabel alloc] init];
    tempLabel.font = [UIFont systemFontOfSize:10];
    NSString *urlString = httpModel.url;
    if (urlString.length>0) {
        tempLabel.numberOfLines = 0;
        tempLabel.text = urlString;
        CGSize size = [tempLabel sizeThatFits:CGSizeMake(DoraemonScreenWidth-50, CGFLOAT_MAX)];
        height += size.height;
        height += 2.;
    }
    
    NSString *method = httpModel.method;
    NSString *status = httpModel.statusCode;
    if (method.length>0 || status.length>0) {
        tempLabel.numberOfLines = 1;
        tempLabel.text = DoraemonLocalizedString(@"你好");
        [tempLabel sizeToFit];
        height += tempLabel.doraemon_height;
        height += 2;
    }
    
    NSString *startTime = [DoraemonUtil dateFormatTimeInterval:httpModel.startTime];
    NSString *time = httpModel.totalDuration;
    if (startTime.length>0 || time.length>0) {
        tempLabel.numberOfLines = 1;
        tempLabel.text = DoraemonLocalizedString(@"你好");
        [tempLabel sizeToFit];
        height += tempLabel.doraemon_height;
        height += 2;
    }
    
    NSString *uploadFlow = httpModel.uploadFlow;
    NSString *downFlow = httpModel.downFlow;
    if (uploadFlow.length>0 || downFlow.length>0) {
        tempLabel.numberOfLines = 1;
        tempLabel.text = DoraemonLocalizedString(@"你好");
        [tempLabel sizeToFit];
        height += tempLabel.doraemon_height;
        height += 2;
    }
    
    height += 3;

    return height;
}


@end
