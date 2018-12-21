//
//  DoraemonNetFlowSummaryTypeDataView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryTypeDataView.h"
#import "UIView+Doraemon.h"
#import <PNChart/PNChart.h>
#import "DoraemonNetFlowDataSource.h"
#import "Doraemoni18NUtil.h"

@interface DoraemonNetFlowSummaryTypeDataView()

@property (nonatomic, strong) NSArray *xLabels;
@property (nonatomic, strong) NSArray *yValues;
@property (nonatomic, strong) NSArray *strokeColors;

@end

@implementation DoraemonNetFlowSummaryTypeDataView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.cornerRadius = 5.f;
        self.backgroundColor = [UIColor whiteColor];
        
        
        UILabel *tipLabel = [[UILabel alloc] init];
        tipLabel.textColor = [UIColor blackColor];
        tipLabel.text = DoraemonLocalizedString(@"数据类型");
        tipLabel.font = [UIFont systemFontOfSize:14];
        [tipLabel sizeToFit];
        tipLabel.frame = CGRectMake(10, 10, tipLabel.doraemon_width, tipLabel.doraemon_height);
        [self addSubview:tipLabel];
        
        [self getData];
        
        if (_xLabels.count>0) {
            NSMutableArray *items = [NSMutableArray array];
            for (int i=0; i<_xLabels.count; i++) {
                NSString *des = _xLabels[i];
                NSUInteger value = [_yValues[i] integerValue];
                UIColor *color = _strokeColors[i];
                PNPieChartDataItem *item = [PNPieChartDataItem dataItemWithValue:value color:color description:des];
                [items addObject:item];
            }
            
            PNPieChart *chart = [[PNPieChart alloc] initWithFrame:CGRectMake(self.doraemon_width/2-100, tipLabel.doraemon_bottom+15,200, 200) items:items];
            chart.descriptionTextColor = [UIColor whiteColor];
            chart.descriptionTextFont = [UIFont systemFontOfSize:6];
            chart.descriptionTextShadowColor = [UIColor clearColor];
            chart.shouldHighlightSectorOnTouch = NO;
            [chart strokeChart];
            
            [self addSubview:chart];
        }

    }
    return self;
}

- (void)getData{
    NSArray *dataArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    NSMutableArray *mineTypeArray = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel* httpModel in dataArray) {
        NSString *mineType = httpModel.mineType;
        if (!mineType || [mineTypeArray containsObject:mineType]) {
            continue;
        }
        [mineTypeArray addObject:mineType];
    }
    
    NSMutableArray *mineTypeDataArray = [NSMutableArray array];
    NSArray *colors = @[PNGreen, PNYellow, PNRed,PNYellow];
    NSInteger colorIndex = 0;
    for (NSString *mineTypeA in mineTypeArray) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setValue:mineTypeA forKey:@"mineType"];
        NSInteger num = 0;
        for (DoraemonNetFlowHttpModel* httpModel in dataArray) {
            NSString *mineTypeB = httpModel.mineType;
            if ([mineTypeA isEqualToString:mineTypeB]) {
                num++;
            }
        }
        [dic setValue:@(num) forKey:@"num"];
        [dic setValue:colors[colorIndex%(colors.count)] forKey:@"color"];
        colorIndex++;
        [mineTypeDataArray addObject:dic];
    }
    
    NSMutableArray *xLabels = [NSMutableArray array];
    NSMutableArray *yValues = [NSMutableArray array];
    NSMutableArray *strokeColors = [NSMutableArray array];
    for (NSDictionary *mineTypeData in mineTypeDataArray) {
        [xLabels addObject:mineTypeData[@"mineType"]];
        [yValues addObject:mineTypeData[@"num"]];
        [strokeColors addObject:mineTypeData[@"color"]];
    }
    
    _xLabels = [NSArray arrayWithArray:xLabels];
    _yValues = [NSArray arrayWithArray:yValues];
    _strokeColors = [NSArray arrayWithArray:strokeColors];
}


@end
