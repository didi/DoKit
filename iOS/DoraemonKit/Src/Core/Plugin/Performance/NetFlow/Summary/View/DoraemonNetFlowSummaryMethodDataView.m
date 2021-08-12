//
//  DoraemonNetFlowSummaryMethodDataView.m
//  DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryMethodDataView.h"
#import "UIView+Doraemon.h"
#import "DoraemonNetFlowDataSource.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonBarChart.h"
#import "DoraemonDefine.h"

@interface DoraemonNetFlowSummaryMethodDataView()
@property (nonatomic, strong) NSArray<DoraemonChartDataItem *> *chartItems;

@end

@implementation DoraemonNetFlowSummaryMethodDataView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.cornerRadius = 5.f;

        UILabel *tipLabel = [[UILabel alloc] init];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            self.backgroundColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
                if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                    return [UIColor secondarySystemBackgroundColor];
                } else {
                    return [UIColor whiteColor];
                }
            }];
            
            tipLabel.textColor = [UIColor labelColor];
        } else {
#endif
            self.backgroundColor = [UIColor whiteColor];
            
            tipLabel.textColor = [UIColor blackColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        tipLabel.text = DoraemonLocalizedString(@"HTTP方法");
        tipLabel.font = [UIFont systemFontOfSize:14];
        [tipLabel sizeToFit];
        tipLabel.frame = CGRectMake(10, 10, tipLabel.doraemon_width, tipLabel.doraemon_height);
        [self addSubview:tipLabel];
        
        [self getData];
        
        if (self.chartItems.count > 0) {
            DoraemonBarChart *chart = [[DoraemonBarChart alloc] initWithFrame:CGRectMake(0, tipLabel.doraemon_bottom+10, self.doraemon_width, self.doraemon_height-tipLabel.doraemon_bottom-10)];
            chart.items = _chartItems;
            chart.yAxis.labelCount = 5;
            chart.contentInset = UIEdgeInsetsMake(0, 50, 40, 20);
            NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
            formatter.maximumFractionDigits = 2;
            chart.vauleFormatter = formatter;
            
            [self addSubview:chart];
            [chart display];
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

    NSMutableArray<DoraemonChartDataItem *> *items = [NSMutableArray array];
    for (NSDictionary *methodData in methodDataArray) {
        DoraemonChartDataItem *item = [[DoraemonChartDataItem alloc] initWithValue:[methodData[@"num"] doubleValue] name:methodData[@"method"] color: [UIColor doraemon_randomColor]];
        [items addObject:item];
    }
    
    self.chartItems = [NSArray arrayWithArray:items];;
}


@end
