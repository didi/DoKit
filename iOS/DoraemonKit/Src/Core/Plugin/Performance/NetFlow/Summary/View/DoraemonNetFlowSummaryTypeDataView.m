//
//  DoraemonNetFlowSummaryTypeDataView.m
//  DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryTypeDataView.h"
#import "UIView+Doraemon.h"
#import "DoraemonPieChart.h"
#import "DoraemonNetFlowDataSource.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonDefine.h"

@interface DoraemonNetFlowSummaryTypeDataView()
@property (nonatomic, strong) NSArray<DoraemonChartDataItem *> *chartItems;

@end

@implementation DoraemonNetFlowSummaryTypeDataView

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
        
        tipLabel.text = DoraemonLocalizedString(@"数据类型");
        tipLabel.font = [UIFont systemFontOfSize:14];
        [tipLabel sizeToFit];
        tipLabel.frame = CGRectMake(10, 10, tipLabel.doraemon_width, tipLabel.doraemon_height);
        [self addSubview:tipLabel];
        
        [self getData];
        
        if (self.chartItems.count > 0) {
            DoraemonPieChart *chart = [[DoraemonPieChart alloc] initWithFrame:CGRectMake(0, tipLabel.doraemon_bottom+10, self.doraemon_width, self.doraemon_height-tipLabel.doraemon_bottom-10)];
            chart.items = self.chartItems;
            [self addSubview:chart];
            [chart display];
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
        [mineTypeDataArray addObject:dic];
    }

    NSMutableArray<DoraemonChartDataItem *> *items = [NSMutableArray array];
    for (NSDictionary *mineTypeData in mineTypeDataArray) {
        DoraemonChartDataItem *item = [[DoraemonChartDataItem alloc] initWithValue:[mineTypeData[@"num"] doubleValue] name:mineTypeData[@"mineType"] color:[UIColor doraemon_randomColor]];
        [items addObject:item];
    }
    self.chartItems = [NSArray arrayWithArray:items];
}


@end
