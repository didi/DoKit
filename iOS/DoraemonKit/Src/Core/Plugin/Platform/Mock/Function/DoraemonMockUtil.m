//
//  DoraemonMockUtil.m
//  AFNetworking
//
//  Created by didi on 2019/11/18.
//

#import "DoraemonMockUtil.h"
#import "DoraemonUtil.h"
#import "DoraemonMockManager.h"
#import "DoraemonDefine.h"

#define DoraemonMockFileName @"mock"
#define DoraemonUploadFileName @"upload"

@interface DoraemonMockUtil()

@end

@implementation DoraemonMockUtil

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
    }
    return self;
}

- (void)saveMockArrayCache{
    NSMutableArray<DoraemonMockAPIModel *> *mockArray = [DoraemonMockManager sharedInstance].mockArray;
    NSMutableArray *dataArray = [[NSMutableArray alloc] init];
    for (DoraemonMockAPIModel *api in mockArray) {
        if (api.selected) {
            NSMutableDictionary *apiDic = [[NSMutableDictionary alloc] init];
            [apiDic setValue:api.apiId forKey:@"apiId"];
            NSArray<DoraemonMockScene *> *sceneList = api.sceneList;
            for (DoraemonMockScene *scene in sceneList) {
                if (scene.selected) {
                    [apiDic setValue:scene.sceneId forKey:@"sceneId"];
                    break;
                }
            }
            [dataArray addObject:apiDic];
        }
    }
    
    [self saveArray:dataArray toFile:DoraemonMockFileName];
}

- (void)saveUploadArrayCache{
    NSMutableArray<DoraemonMockUpLoadModel *> *upLoadArray = [DoraemonMockManager sharedInstance].upLoadArray;
    NSMutableArray *dataArray = [[NSMutableArray alloc] init];
    for (DoraemonMockUpLoadModel *upload in upLoadArray) {
        if (upload.selected) {
            NSMutableDictionary *uploadDic = [[NSMutableDictionary alloc] init];
            [uploadDic setValue:upload.apiId forKey:@"apiId"];
            if(upload.result.length>0){
                [uploadDic setValue:upload.result forKey:@"result"];
            }
            [dataArray addObject:uploadDic];
        }
    }
    
    [self saveArray:dataArray toFile:DoraemonUploadFileName];
}

- (void)readMockArrayCache{
    NSMutableArray<DoraemonMockAPIModel *> *mockArray = [DoraemonMockManager sharedInstance].mockArray;
    for (DoraemonMockAPIModel *api in mockArray) {
        NSString *apiId = api.apiId;
        NSArray *dataArray = [self getArrayFromFile:DoraemonMockFileName];
        for (NSDictionary *apiDic in dataArray) {
            if([apiId isEqualToString:apiDic[@"apiId"]]){
                api.selected = YES;
                for (DoraemonMockScene *scene in api.sceneList) {
                    if ([scene.sceneId isEqualToString: apiDic[@"sceneId"]]) {
                        scene.selected = YES;
                        break;
                    }
                }
                break;
            }
        }
    }
}

- (void)readUploadArrayCache{
    NSMutableArray<DoraemonMockUpLoadModel *> *upLoadArray = [DoraemonMockManager sharedInstance].upLoadArray;
    for (DoraemonMockUpLoadModel *upload in upLoadArray) {
        NSArray *dataArray = [self getArrayFromFile:DoraemonUploadFileName];
        for (NSDictionary *uploadDic in dataArray) {
            if ([upload.apiId isEqualToString:uploadDic[@"apiId"]]) {
                upload.selected = YES;
                if (uploadDic[@"result"]){
                    upload.result = uploadDic[@"result"];
                }
                break;
            }
        }
    }
}

- (void)saveArray:(NSArray *)dataArray toFile:(NSString *)fileName{
    NSString *cachesDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    NSString *anrDir = [cachesDir stringByAppendingPathComponent:@"DoraemonMockCache"];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDir = NO;
    BOOL existed = [fileManager fileExistsAtPath:anrDir isDirectory:&isDir];
    if(!(isDir && existed)){
        [fileManager createDirectoryAtPath:anrDir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *path = [anrDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.json",fileName]];
    NSString *text = [DoraemonUtil arrayToJsonStr:dataArray];
    BOOL writeSuccess = [text writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:nil];
    if (writeSuccess) {
        DoKitLog(@"写入成功");
    }
}

- (NSArray *)getArrayFromFile:(NSString *)fileName{
    NSArray *dataArray;
    NSString *cachesDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    NSString *anrDir = [cachesDir stringByAppendingPathComponent:@"DoraemonMockCache"];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDir = NO;
    BOOL existed = [fileManager fileExistsAtPath:anrDir isDirectory:&isDir];
    if (existed){
        NSString *path = [anrDir stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.json",fileName]];
        NSError *error = nil;
        NSString *json = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:&error];
        dataArray = [DoraemonUtil arrayWithJsonString:json];
    }
    return dataArray;
}

@end
