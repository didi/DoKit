//
//  DoraemonNetFlowSummaryTypeDataView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonNetFlowSummaryTypeDataView.h"
#import "UIView+Doraemon.h"
#import "PieChart.h"
#import "DoraemonNetFlowDataSource.h"
#import "Doraemoni18NUtil.h"

@interface DoraemonNetFlowSummaryTypeDataView()
@property (nonatomic, strong) NSArray *chartItems;

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
        
        if (self.chartItems.count > 0) {
            PieChart *chart = [[PieChart alloc] initWithFrame:CGRectMake(self.doraemon_width/2-100, tipLabel.doraemon_bottom+15,200, 200)];
            chart.items = self.chartItems;
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

    NSMutableArray<ChartDataItem *> *items = [NSMutableArray array];
    for (NSDictionary *mineTypeData in mineTypeDataArray) {
        ChartDataItem *item = [[ChartDataItem alloc] initWithValue:[mineTypeData[@"num"] doubleValue] name:[mineTypeData[@"mineType"] stringValue] color:[UIColor doraemon_randomColor]];
        [items addObject:item];
    }
    self.chartItems = [NSArray arrayWithArray:items];
}


@end
