//
//  DoraemonNetFlowSummaryMethodDataView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryMethodDataView.h"
#import "UIView+Doraemon.h"
#import <PNChart/PNChart.h>
#import "DoraemonNetFlowDataSource.h"
#import "Doraemoni18NUtil.h"

@interface DoraemonNetFlowSummaryMethodDataView()

@property (nonatomic, strong) NSArray *xLabels;
@property (nonatomic, strong) NSArray *yValues;
@property (nonatomic, strong) NSArray *strokeColors;

@end

@implementation DoraemonNetFlowSummaryMethodDataView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.cornerRadius = 5.f;
        self.backgroundColor = [UIColor whiteColor];
        
        
        UILabel *tipLabel = [[UILabel alloc] init];
        tipLabel.textColor = [UIColor blackColor];
        tipLabel.text = DoraemonLocalizedString(@"HTTP方法");
        tipLabel.font = [UIFont systemFontOfSize:14];
        [tipLabel sizeToFit];
        tipLabel.frame = CGRectMake(10, 10, tipLabel.doraemon_width, tipLabel.doraemon_height);
        [self addSubview:tipLabel];
        
        [self getData];
        
        if (_xLabels.count>0) {
            PNBarChart *chart = [[PNBarChart alloc] initWithFrame:CGRectMake(0, tipLabel.doraemon_bottom+10, self.doraemon_width, self.doraemon_height-tipLabel.doraemon_bottom-10)];
            chart.showChartBorder = YES;
            chart.showLabel = YES;
            chart.chartMarginTop = 5.0;
            chart.labelMarginTop = -5.0;
            chart.isGradientShow = NO;
            chart.xLabels = _xLabels;
            chart.yValues = _yValues;
            [chart setStrokeColors : _strokeColors];
            chart.yLabelFormatter = ^ (CGFloat yLabelValue) {
                return [NSString stringWithFormat:@"%f",yLabelValue];
            };
            [chart strokeChart];
            
            [self addSubview:chart];
        }

    }
    return self;
}

- (void)getData{
    NSArray *dataArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    NSMutableArray *methodArray = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel* httpModel in dataArray) {
        NSString *method = httpModel.method;
        if (!method || [methodArray containsObject:method]) {
            continue;
        }
        [methodArray addObject:method];
    }
    
    NSMutableArray *methodDataArray = [NSMutableArray array];
    NSArray *colors = @[PNGreen, PNYellow, PNRed];
    NSInteger colorIndex = 0;
    for (NSString *methodA in methodArray) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:methodA forKey:@"method"];
        NSInteger num = 0;
        for (DoraemonNetFlowHttpModel* httpModel in dataArray) {
            NSString *methodB = httpModel.method;
            if ([methodA isEqualToString:methodB]) {
                num++;
            }
        }
        [dic setValue:@(num) forKey:@"num"];
        [dic setValue:colors[colorIndex%(colors.count)] forKey:@"color"];
        colorIndex++;
        [methodDataArray addObject:dic];
    }
    
    NSMutableArray *xLabels = [NSMutableArray array];
    NSMutableArray *yValues = [NSMutableArray array];
    NSMutableArray *strokeColors = [NSMutableArray array];
    for (NSDictionary *methodData in methodDataArray) {
        [xLabels addObject:methodData[@"method"]];
        [yValues addObject:methodData[@"num"]];
        [strokeColors addObject:methodData[@"color"]];
    }
    
    _xLabels = [NSArray arrayWithArray:xLabels];
    _yValues = [NSArray arrayWithArray:yValues];
    _strokeColors = [NSArray arrayWithArray:strokeColors];
}


@end
