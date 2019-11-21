//
//  DoraemonUtil.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import "DoraemonUtil.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonUtil

- (instancetype)init{
    self = [super init];
    if (self) {
        _fileSize = 0;
    }
    return self;
}



+ (NSString *)dateFormatTimeInterval:(NSTimeInterval)timeInterval{
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:timeInterval];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *dateString = [formatter stringFromDate: date];
    return dateString;
}

+ (NSString *)dateFormatNSDate:(NSDate *)date{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *dateString = [formatter stringFromDate: date];
    return dateString;
}

+ (NSString *)dateFormatNow{
    NSDate *date = [NSDate date];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSString *dateString = [formatter stringFromDate: date];
    return dateString;
}

// byte格式化为 B KB MB 方便流量查看
+ (NSString *)formatByte:(CGFloat)byte{
    double convertedValue = byte;
    int multiplyFactor = 0;
    NSArray *tokens = [NSArray arrayWithObjects:@"B",@"KB",@"MB",@"GB",@"TB",nil];
    
    while (convertedValue > 1024) {
        convertedValue /= 1024;
        multiplyFactor++;
    }
    return [NSString stringWithFormat:@"%4.2f%@",convertedValue, [tokens objectAtIndex:multiplyFactor]]; ;
}

+ (NSString *)formatTimeIntervalToMS:(NSTimeInterval)timeInterval{
    CGFloat ms = timeInterval * 1000;
    return [NSString stringWithFormat:@"%.0f",ms];
}

+ (void)savePerformanceDataInFile:(NSString *)fileName data:(NSString *)data{
    NSString *cachesDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    NSString *anrDir = [cachesDir stringByAppendingPathComponent:@"DoraemonPerformance"];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDir = NO;
    BOOL existed = [fileManager fileExistsAtPath:anrDir isDirectory:&isDir];
    if(!(isDir && existed)){
        [fileManager createDirectoryAtPath:anrDir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *path = [anrDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.txt",fileName]];
    NSString *text = data;
    BOOL writeSuccess = [text writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:nil];
    if (writeSuccess) {
        NSLog(@"写入成功");
    }
}

+ (NSDictionary *)dictionaryWithJsonString:(NSString *)jsonString {
    if (jsonString == nil) {
        return nil;
    }
    
    NSData *jsonData = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    NSError *err;
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                                                        options:NSJSONReadingMutableContainers
                                                          error:&err];
    if(err) {
        NSLog(@"json解析失败：%@",err);
        return nil;
    }
    return dic;
}

+(NSString *)dictToJsonStr:(NSDictionary *)dict{
    
    NSString *jsonString = nil;
    if ([NSJSONSerialization isValidJSONObject:dict])
    {
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&error];
        jsonString =[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        if (error) {
            NSLog(@"Error:%@" , error);
        }
    }
    return jsonString;
}

//获取某一条文件路径的文件大小
- (void)getFileSizeWithPath:(NSString *)path{
    NSFileManager * fileManger = [NSFileManager defaultManager];
    BOOL isDir = NO;
    BOOL isExist = [fileManger fileExistsAtPath:path isDirectory:&isDir];
    if (isExist){
        if(isDir){
            //文件夹
            NSArray * dirArray = [fileManger contentsOfDirectoryAtPath:path error:nil];
            NSString * subPath = nil;
            for(NSString *str in dirArray) {
                subPath = [path stringByAppendingPathComponent:str];
                [self getFileSizeWithPath:subPath];
            }
        }else{
            //文件
            NSDictionary *dict = [[NSFileManager defaultManager] attributesOfItemAtPath:path error:nil];
            NSInteger size = [dict[@"NSFileSize"] integerValue];
            _fileSize += size;
        }
    }else{
        //不存在该文件path
        //NSLog(@"不存在该文件");
    }
}

//删除某一路径下的所有文件
+ (void)clearFileWithPath:(NSString *)path{
    NSFileManager *fm = [NSFileManager defaultManager];
    NSArray *files = [fm subpathsAtPath:path];
    for (NSString *file in files) {
        NSError *error;
        NSString *filePath = [path stringByAppendingPathComponent:file];
        if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
            [[NSFileManager defaultManager] removeItemAtPath:filePath error:&error];
            if (!error) {
                NSLog(@"remove file: %@", file);
            }
        }
    }
}

+ (void)clearLocalDatas {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *homePath = NSHomeDirectory();
        NSArray *folders = @[@"Documents", @"Library", @"tmp"];
        for (NSString *folder in folders) {
            [DoraemonUtil clearFileWithPath:[homePath stringByAppendingPathComponent:folder]];
        }
    });
}

+ (void)openPlugin:(UIViewController *)vc {
    [DoraemonHomeWindow openPlugin:vc];
}


+ (UIViewController *)rootViewControllerForKeyWindow {
    return [UIViewController rootViewControllerForKeyWindow];
}

+ (UIViewController *)topViewControllerForKeyWindow {
    return [UIViewController topViewControllerForKeyWindow];
}

@end
