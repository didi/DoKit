//
//  DoraemonPersistenceUtil.m
//  DoraemonKit-DoraemonKit
//
//  Created by ZhangHonglin on 2018/11/7.
//

#import "DoraemonPersistenceUtil.h"


static NSString * const kPersistenceDir         = @"performance";
static NSString * const kPersistenceItemCPU     = @"CPU";
static NSString * const kPersistenceItemFPS     = @"FPS";
static NSString * const kPersistenceItemMemory  = @"Memory";
static NSString * const kPersistenceItemNetFlow = @"NetFlow";

static NSString * const kPersistenceFileType    = @".txt";

@implementation DoraemonPersistenceUtil

+ (void)saveRecord:(DoraemonRecordModel *)obj {
    DoraemonRecordModel *record = obj;
    NSString *directory = [self getDirectoryWithType:record.type];
    
    if (![[NSFileManager defaultManager] fileExistsAtPath:directory]) {
        BOOL success = [[NSFileManager defaultManager] createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:nil];
        if (!success) {
            NSLog(@"create dic fail");
            return;
        }
    }
    
    NSMutableArray *dataArray = [NSMutableArray array];
    for (DoraemonPerformanceInfoModel *item in record.recordsArray) {
        [dataArray addObject:[item dictionary]];
    }
   
    NSString *json = [self dictToJsonStr:dataArray];
    NSString *filePath = [directory stringByAppendingPathComponent:[NSString stringWithFormat:@"%@%@", @(record.startTime).stringValue, kPersistenceFileType]];
    
    NSError *error;
    [json writeToFile:filePath atomically:YES encoding:NSUTF8StringEncoding error:&error];
    if (error) {
        NSLog(@"write data failed, error: %@", error);
    }
}

+ (NSArray *)getLocalRecordsWithType:(DoraemonRecordType)type {
    NSString *directory = [self getDirectoryWithType:type];
    NSArray *fileArray = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:directory error:nil];
    return fileArray;
}

+ (NSArray *)getRecordDetailWithType:(DoraemonRecordType)type fileName:(NSString *)fileName {
    NSString *directory = [self getDirectoryWithType:type];
    fileName = [NSString stringWithFormat:@"%@%@", fileName, kPersistenceFileType];
    NSString *filePath = [directory stringByAppendingPathComponent:fileName];
    
    NSError *error = nil;
    NSString *json = [NSString stringWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:&error];
    if (error) {
        return @[];
    }
    
    error = nil;
    NSData *data = [json dataUsingEncoding:NSUTF8StringEncoding];
    NSArray *itemArray = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
    if (error) {
        return @[];
    }
    
    NSMutableArray *resultArray = [NSMutableArray array];
    for (NSDictionary *item in itemArray) {
        if (item[@"value"]) {
            DoraemonPerformanceInfoModel *model = [DoraemonPerformanceInfoModel new];
            model.value = ((NSNumber *)(item[@"value"])).floatValue;
            model.heightValue = ((NSNumber *)(item[@"heightValue"])).floatValue;
            model.time = ((NSString *)(item[@"time"])).doubleValue;
            [resultArray addObject:model];
        }
    }
    
    return resultArray;
}

+ (NSString *)getDirectoryWithType:(DoraemonRecordType)type {
    NSString *path = nil;
    switch (type) {
        case DoraemonRecordTypeFPS:
            path = kPersistenceItemFPS;
            break;
            
        case DoraemonRecordTypeCPU:
            path = kPersistenceItemCPU;
            break;
            
        case DoraemonRecordTypeMemory:
            path = kPersistenceItemMemory;
            break;
            
        case DoraemonRecordTypeNetFlow:
            path = kPersistenceItemNetFlow;
            break;
            
        default:
            break;
    }
    
    if (path) {
        NSString *cachePath = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).lastObject;
        return [cachePath stringByAppendingPathComponent:path];
    }
    
    return nil;
}

+ (NSString *)dictToJsonStr:(id)obj{
    NSString *jsonString = nil;
    if ([NSJSONSerialization isValidJSONObject:obj])
    {
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:obj options:NSJSONWritingPrettyPrinted error:&error];
        jsonString =[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        if (error) {
            NSLog(@"Error:%@" , error);
        }
    }
    
    return jsonString;
}

@end
