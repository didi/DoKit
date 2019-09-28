//
//  DoraemonNetFlowSummaryMethodDataView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryMethodDataView.h"
#import "UIView+Doraemon.h"
#import "DoraemonNetFlowDataSource.h"
#import "Doraemoni18NUtil.h"
#import "BarChart.h"

@interface DoraemonNetFlowSummaryMethodDataView()
@property (nonatomic, strong) NSArray *chartItems;

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
        
        if (self.chartItems.count > 0) {
            BarChart *chart = [[BarChart alloc] initWithFrame:CGRectMake(0, tipLabel.doraemon_bottom+10, self.doraemon_width, self.doraemon_height-tipLabel.doraemon_bottom-10)];
            chart.items = _chartItems;
            chart.yAxis.labelCount = 3;
            chart.contentInset = UIEdgeInsetsMake(30, 50, 40, 20);
            chart.vauleFormatter = [[NSNumberFormatter alloc] init];
            
            [self addSubview:chart];
        }

    }
    return self;
}

- (void)getData{
    NSArray *dataArray = [DoraemonNetFlowDataSource shareInstance].httpModelArray;
    NSMutableArray<NSString *> *methodArray = [NSMutableArray array];
    for (DoraemonNetFlowHttpModel* httpModel in dataArray) {
        NSString *method = httpModel.method;
        if (!method || [methodArray containsObject:method]) {
            continue;
        }
        [methodArray addObject:method];
    }
    
    NSMutableArray *methodDataArray = [NSMutableArray array];
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
        [methodDataArray addObject:dic];
    }

    NSMutableArray<ChartDataItem *> *items = [NSMutableArray array];
    for (NSDictionary *methodData in methodDataArray) {
        ChartDataItem *item = [[ChartDataItem alloc] initWithValue:[methodData[@"num"] doubleValue] name:methodData[@"method"] color: [UIColor doraemon_randomColor]];
        [items addObject:item];
    }
    
    self.chartItems = [NSArray arrayWithArray:items];;
}


@end
