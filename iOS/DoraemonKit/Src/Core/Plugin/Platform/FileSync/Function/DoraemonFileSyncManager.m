//
//  DoraemonFileSyncManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/6/10.
//

#import "DoraemonFileSyncManager.h"
#import <GCDWebServer/GCDWebServerRequest.h>
#import <GCDWebServer/GCDWebServerDataResponse.h>
#import <GCDWebServer/GCDWebServerMultiPartFormRequest.h>
#import <GCDWebServer/GCDWebServerFileResponse.h>
#import "DoraemonAppInfoUtil.h"

@interface DoraemonFileSyncManager()

@property (nonatomic, strong) NSFileManager *fm;

@end


@implementation DoraemonFileSyncManager

+ (instancetype)sharedInstance{
    static DoraemonFileSyncManager *instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[DoraemonFileSyncManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _start = NO;
        [self setRouter];
        _fm = [NSFileManager defaultManager];
    }
    return self;
}

- (void)setRouter{
    [self addDefaultHandlerForMethod:@"GET"
                        requestClass:[GCDWebServerRequest class]
                        processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        NSString *html = @"<html><body>请访问 <b><a href=\"https://www.dokit.cn\">www.dokit.cn</a></b> 使用该功能</body></html>";
        return [GCDWebServerDataResponse responseWithHTML:html];
    }];
    
    __weak typeof(self) weakSelf = self;
    [self addHandlerForMethod:@"GET"
                         path:@"/getDeviceInfo"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return  [weakSelf getDeviceInfo];
    }];
    
    [self addHandlerForMethod:@"GET"
                         path:@"/getFileList"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf getFileList:request];
    }];
    
    [self addHandlerForMethod:@"POST"
                         path:@"/uploadFile"
                 requestClass:[GCDWebServerMultiPartFormRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf uploadFile:(GCDWebServerMultiPartFormRequest *)request];
    }];
    
    [self addHandlerForMethod:@"GET"
                         path:@"/downloadFile"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf downloadFile:request];
    }];
    
    [self addHandlerForMethod:@"GET"
                         path:@"/createFolder"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf createFolder:request];
    }];
    
    [self addHandlerForMethod:@"GET"
                         path:@"/getFileDetail"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf getFileDetail:request];
    }];
}

- (NSString *)getRelativeFilePath:(NSString *)fullPath{
    NSString *rootPath = NSHomeDirectory();
    return [fullPath stringByReplacingOccurrencesOfString:rootPath withString:@""];
}

- (NSDictionary *)getCode:(NSInteger)code data:(NSDictionary *)data{
    NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
    [info setValue:@(code) forKey:@"code"];
    [info setValue:data forKey:@"data"];
    return info;
}

- (void)startServer{
    [self startWithPort:9002 bonjourName:@"Hello DoKit"];
}


#pragma mark -- 服务具体处理
- (GCDWebServerResponse *)getDeviceInfo{
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setValue:[DoraemonAppInfoUtil iphoneName] forKey:@"deviceName"];
    [dic setValue:[DoraemonAppInfoUtil uuid] forKey:@"deviceId"];
    [dic setValue:[DoraemonAppInfoUtil getIPAddress:YES] forKey:@"deviceIp"];
    
    NSDictionary *info = [self getCode:200 data:dic];
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:info];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)getFileList: (GCDWebServerRequest *)request{
    
    NSDictionary *query = request.query;
    NSString *dirPath = query[@"dirPath"];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@",rootPath,dirPath];
    
    NSMutableArray *files = @[].mutableCopy;
       NSError *error = nil;
    NSArray *paths = [_fm contentsOfDirectoryAtPath:targetPath error:&error];
    for (NSString *path in paths) {
        BOOL isDir = false;
        NSString *fullPath = [targetPath stringByAppendingPathComponent:path];
        [_fm fileExistsAtPath:fullPath isDirectory:&isDir];
        
        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
        dic[@"isRootPath"] = [path isEqualToString:rootPath] ? @YES : @NO;
        dic[@"fileName"] = path;
        dic[@"fileUrl"] = [self getRelativeFilePath:fullPath];
        if (isDir) {
            dic[@"fileType"] = @"folder";
        }else{
            dic[@"fileType"] = [path pathExtension];
        }
        
        NSDictionary *fileAttributes = [_fm attributesOfItemAtPath:fullPath error:nil];
        NSDate *modifyDate = [fileAttributes objectForKey:NSFileModificationDate];
        dic[@"modifyTime"] = @([modifyDate timeIntervalSince1970]*1000);
        [files addObject:dic];
    }
    
    NSMutableDictionary *data = [[NSMutableDictionary alloc] init];
    [data setValue:dirPath forKey:@"dirPath"];
    [data setValue:[DoraemonAppInfoUtil uuid] forKey:@"deviceId"];
    [data setValue:files forKey:@"fileList"];
    
    NSDictionary *res = [self getCode:200 data:data];
    
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse*)uploadFile:(GCDWebServerMultiPartFormRequest*)request{
#warning todo
    GCDWebServerMultiPartFile *file = [request firstFileForControlName:@"file"];
    NSString *dirPath = [[request firstArgumentForControlName:@"dirPath"] string];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@",rootPath,dirPath,file.fileName];
    NSError *error = nil;
    
    NSDictionary *res;
    if (![_fm moveItemAtPath:file.temporaryPath toPath:targetPath error:&error]) {
        NSLog(@"Failed moving uploaded file to \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    }else{
        res = [self getCode:200 data:nil];
    }
    
     
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)downloadFile:(GCDWebServerRequest *)request{
    NSString *rootPath = NSHomeDirectory();
    NSString *dirPath = [[request query] objectForKey:@"dirPath"];
    NSString *fileName = [[request query] objectForKey:@"fileName"];
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@",rootPath,dirPath,fileName];
    
    NSDictionary *res;
    BOOL isDirectory = NO;
    if (![_fm fileExistsAtPath:targetPath isDirectory:&isDirectory]) {
        NSLog(@"\"%@\" does not exist", targetPath);
        res = [self getCode:0 data:nil];
        GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
        [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
        return response;
    } else {
        GCDWebServerFileResponse *response = [GCDWebServerFileResponse responseWithFile:targetPath isAttachment:YES];
        return response;
    }
}

- (GCDWebServerResponse *)createFolder:(GCDWebServerRequest *)request{
    NSString *rootPath = NSHomeDirectory();
    NSString *dirPath = [[request query] objectForKey:@"dirPath"];
    NSString *fileName = [[request query] objectForKey:@"fileName"];
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@",rootPath,dirPath,fileName];
    
    NSDictionary *res;
    if (![[NSFileManager defaultManager] createDirectoryAtPath:targetPath withIntermediateDirectories:NO attributes:nil error:nil]) {
        NSLog(@"Failed creating directory \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    }else{
        res = [self getCode:200 data:nil];
    }
    
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    return response;
}

- (GCDWebServerResponse *)getFileDetail:(GCDWebServerRequest *)request{
    NSString *rootPath = NSHomeDirectory();
    NSString *dirPath = [[request query] objectForKey:@"dirPath"];
    NSString *fileName = [[request query] objectForKey:@"fileName"];
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@",rootPath,dirPath,fileName];
    
    NSDictionary *res;
    if ([_fm fileExistsAtPath:targetPath]) {
        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
        NSString *fileContent = [[NSString alloc] initWithData:[_fm contentsAtPath:targetPath] encoding:NSUTF8StringEncoding];
        [dic setValue:targetPath.pathExtension forKey:@"fileType"];
        [dic setValue:fileContent forKey:@"fileContent"];
        res = [self getCode:200 data:dic];
    } else {
        NSLog(@"File not founded at \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    }

    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    return response;
}

@end
