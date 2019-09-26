//
//  DoraemonANRTool.m
//  DoraemonKit
//   
//  Created by DeveloperLY on 2019/9/18.
//  
//

#import "DoraemonANRTool.h"

@implementation DoraemonANRTool

+ (void)saveANRInfo:(NSDictionary *)info {
    if ([info isKindOfClass:[NSDictionary class]] && (info.count > 0)) {
        NSFileManager *manager = [NSFileManager defaultManager];
        NSString *anrDirectory = [self anrDirectory];
        if (anrDirectory && [manager fileExistsAtPath:anrDirectory]) {
            // 获取 ANR 保存的路径
            NSString *anrPath = [anrDirectory stringByAppendingPathComponent:[NSString stringWithFormat:@"ANR %@.plist", info[@"title"]]];
            [info writeToFile:anrPath atomically:YES];
        }
    }
}

+ (NSString *)anrDirectory {
    NSString *cachePath = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    NSString *directory = [cachePath stringByAppendingPathComponent:@"ANR"];
    
    NSFileManager *manager = [NSFileManager defaultManager];
    if (![manager fileExistsAtPath:directory]) {
        [manager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
    return directory;
}

@end
