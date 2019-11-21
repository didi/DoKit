//
//  DoraemonAllTestStatisticsManager.m
//  AFNetworking
//
//  Created by didi on 2019/9/27.
//

#import "DoraemonAllTestStatisticsManager.h"
#import "DoraemonDefine.h"

@interface DoraemonAllTestStatisticsManager()


@end

@implementation DoraemonAllTestStatisticsManager

+ (DoraemonAllTestStatisticsManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonAllTestStatisticsManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonAllTestStatisticsManager alloc] init];
    });
    return instance;
}

-(NSMutableArray *)getLastResultArray{
    if(!_resultDic)
        return nil;
    
    NSMutableArray *infoArray = [[NSMutableArray alloc] init];
    NSArray *common_data = _resultDic[@"common_data"];
    NSArray *flow_data = _resultDic[@"flow_data"];
    NSArray *commonKeyArray = [[NSArray alloc] initWithObjects:@"memory", @"CPU", @"fps", nil];
    NSArray *flowKeyArray = [[NSArray alloc] initWithObjects:@"upFlow", @"downFlow", nil];

    if(common_data.count<=0 && flow_data.count<=0)
        return nil;
    int index = 0;
    
    while (index < common_data.count) {
        int count = 0;
        NSString *pageName = common_data[index][@"page"];
        NSMutableArray *addArray = [NSMutableArray array];
        
        for (NSString *value in commonKeyArray) {
            NSMutableArray *valueArray = [NSMutableArray array];
            NSMutableDictionary *addDiction = [NSMutableDictionary dictionary];
            for (int i = index; i<common_data.count; i++) {
                NSDictionary *infoItem = common_data[i];
                if([pageName isEqual:infoItem[@"page"]]){
                    count++;
                    if([infoItem[value] floatValue] >= 0)
                        [valueArray addObject:infoItem[value]];
                }else{
                     break;
                }
            }
            addDiction[@"title"] = value;
            if(valueArray.count>0 && [[valueArray valueForKeyPath:@"@max.floatValue"] floatValue] >= 0){
                addDiction[@"max"] = [NSNumber numberWithFloat:round([[valueArray valueForKeyPath:@"@max.floatValue"] floatValue]*10)/10];
                addDiction[@"min"] = [NSNumber numberWithFloat:round([[valueArray valueForKeyPath:@"@min.floatValue"] floatValue]*10)/10];
                addDiction[@"average"] = [NSNumber numberWithFloat:round([[valueArray valueForKeyPath:@"@avg.floatValue"] floatValue]*10)/10];
                [addArray addObject:addDiction];
            }
        }
        count /=commonKeyArray.count;
        long timeMin = [self getTimeStrWithString:common_data[index][@"time"]];
        long timeMax = timeMin + 1;
        if(count>1){
            timeMax = 1 + [self getTimeStrWithString:common_data[index + count -1][@"time"]];
        }
        
        for (NSString *value in flowKeyArray) {
            NSMutableArray *valueArray = [NSMutableArray array];
            NSMutableDictionary *addDiction = [NSMutableDictionary dictionary];
            for (NSDictionary *infoItem in flow_data) {
                if(infoItem[value]){
                    long time = [self getTimeStrWithString:infoItem[@"time"]];
                    if(time > timeMin && time < timeMax){
                        [valueArray addObject:infoItem[value]];
                    }
                }
            }
            addDiction[@"title"] = value;
            if(valueArray.count > 0){
                float max = [[valueArray valueForKeyPath:@"@max.floatValue"] floatValue];
                float min = [[valueArray valueForKeyPath:@"@min.floatValue"] floatValue];
                float average = [[valueArray valueForKeyPath:@"@avg.floatValue"] floatValue];
                addDiction[@"max"] = [NSNumber numberWithFloat:round(max)*10/10];
                addDiction[@"min"] = [NSNumber numberWithFloat:round(min)*10/10];
                addDiction[@"average"] = [NSNumber numberWithFloat:round(average)*10/10];
                if(max > 1000)
                    addDiction[@"max"] = [NSString stringWithFormat:@"%.1fK",max/=1000];
                if(min > 1000)
                    addDiction[@"min"] = [NSString stringWithFormat:@"%.1fK",min/=1000];
                if(average > 1000)
                    addDiction[@"average"] = [NSString stringWithFormat:@"%.1fK",average/=1000];
                if(max > 1000)
                    addDiction[@"max"] = [NSString stringWithFormat:@"%.1fM",max/1000];
                if(min > 1000)
                    addDiction[@"min"] = [NSString stringWithFormat:@"%.1fM",min/1000];
                if(average > 1000)
                    addDiction[@"average"] = [NSString stringWithFormat:@"%.1fM",average/1000];
                [addArray addObject:addDiction];
            }
        }
        index += count;
        [infoArray addObject:[NSMutableDictionary dictionary]];
        infoArray[infoArray.count-1][@"common_data"] = addArray;
        infoArray[infoArray.count-1][@"page"] = [NSString stringWithFormat:@"%@(%d)",pageName,count];
    }
    NSMutableArray *new_info_array = [NSMutableArray array];
    for (NSDictionary *item in infoArray) {
        NSArray *common_data_array = item[@"common_data"];
        if(common_data_array.count>0)
           [new_info_array addObject:item];
    }
    return new_info_array;
}

- (long)getTimeStrWithString:(NSString *)str{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];// 创建一个时间格式化对象
    [dateFormatter setDateFormat:@"YYYY-MM-dd HH:mm:ss:SSS"]; //设定时间的格式
    NSDate *tempDate = [dateFormatter dateFromString:str];//将字符串转换为时间对象
    return (long)[tempDate timeIntervalSince1970];
}

@end
