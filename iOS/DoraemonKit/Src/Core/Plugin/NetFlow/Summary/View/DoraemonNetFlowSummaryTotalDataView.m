//
//  DoraemonNetFlowSummaryTotalDataView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryTotalDataView.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonUtil.h"
#import "DoraemonDefine.h"

@interface DoraemonNetFlowSummaryTotalDataItemView : UIView

@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) UILabel *valueLabel;

@end

@implementation DoraemonNetFlowSummaryTotalDataItemView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _valueLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, self.doraemon_width, kDoraemonSizeFrom750(44))];
        _valueLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(44)];
        _valueLabel.textColor = [UIColor doraemon_black_1];
        _valueLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_valueLabel];
        
        _titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, _valueLabel.doraemon_bottom+kDoraemonSizeFrom750(16), self.doraemon_width, kDoraemonSizeFrom750(24))];
        _titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(20)];
        _titleLabel.textColor = [UIColor doraemon_black_2];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_titleLabel];
        

    }
    return self;
}

- (void)renderUIWithTitle:(NSString *)title value:(NSString *)value{
    _titleLabel.text = title;
    _valueLabel.text = value;
}

@end

@interface DoraemonNetFlowSummaryTotalDataView()

@property (nonatomic, strong) DoraemonNetFlowSummaryTotalDataItemView *timeView;//抓包时间
@property (nonatomic, strong) DoraemonNetFlowSummaryTotalDataItemView *numView;//抓包数量
@property (nonatomic, strong) DoraemonNetFlowSummaryTotalDataItemView *upLoadView;//数据上传
@property (nonatomic, strong) DoraemonNetFlowSummaryTotalDataItemView *downLoadView;//数据下载

@end

@implementation DoraemonNetFlowSummaryTotalDataView

- (instancetype)initWithFrame:(CGRect)frame{
    self =[super initWithFrame:frame];
    if (self) {
        [self initUI];
    }
    return self;
}

- (void)initUI{
    self.layer.cornerRadius = 5.f;
    self.backgroundColor = [UIColor whiteColor];
    
    
    
    //抓包时间
    NSString *time;
    NSDate *startInterceptDate = [DoraemonNetFlowManager shareInstance].startInterceptDate;
    if (startInterceptDate) {
        NSDate *nowDate = [NSDate date];
        NSTimeInterval cha = [nowDate timeIntervalSinceDate:startInterceptDate];
        time = [NSString stringWithFormat:@"%.2f秒",cha];
    }else{
        time = DoraemonLocalizedString(@"暂未开启流量监控");
    }
    
    //抓包数量
    NSArray *httpModelArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    NSString *num = [NSString stringWithFormat:@"%zi",httpModelArray.count];
    
    CGFloat totalUploadFlow = 0.;
    CGFloat totalDownFlow = 0.;
    for (int i=0; i<httpModelArray.count; i++) {
        DoraemonNetFlowHttpModel *httpModel = httpModelArray[i];
        CGFloat uploadFlow =  [httpModel.uploadFlow floatValue];
        CGFloat downFlow = [httpModel.downFlow floatValue];
        totalUploadFlow += uploadFlow;
        totalDownFlow += downFlow;
    }
    //数据上传
    NSString *upLoad = [DoraemonUtil formatByte:totalUploadFlow];
    
    //数据下载
    NSString *downLoad = [DoraemonUtil formatByte:totalDownFlow];
    
    _timeView = [[DoraemonNetFlowSummaryTotalDataItemView alloc] initWithFrame:CGRectMake(0, 20, self.doraemon_width, 40)];
    [_timeView renderUIWithTitle:DoraemonLocalizedString(@"总计已为您抓包") value:time];
    [self addSubview:_timeView];
    
    CGFloat offsetY = 20+40+40;
    CGFloat itemWidth = self.doraemon_width/3;
    CGFloat offsetX = 0;
    
    _numView = [[DoraemonNetFlowSummaryTotalDataItemView alloc] initWithFrame:CGRectMake(0, offsetY, itemWidth, 40)];
    [_numView renderUIWithTitle:DoraemonLocalizedString(@"抓包数量") value:num];
    [self addSubview:_numView];
    
    offsetX += _numView.doraemon_width;
    
    _upLoadView = [[DoraemonNetFlowSummaryTotalDataItemView alloc] initWithFrame:CGRectMake(offsetX, offsetY, itemWidth, 40)];
    [_upLoadView renderUIWithTitle:DoraemonLocalizedString(@"数据上传") value:upLoad];
    [self addSubview:_upLoadView];
    
    offsetX += _upLoadView.doraemon_width;
    
    _downLoadView = [[DoraemonNetFlowSummaryTotalDataItemView alloc] initWithFrame:CGRectMake(offsetX, offsetY, itemWidth, 40)];
    [_downLoadView renderUIWithTitle:DoraemonLocalizedString(@"数据下载") value:downLoad];
    [self addSubview:_downLoadView];
    
}

@end
