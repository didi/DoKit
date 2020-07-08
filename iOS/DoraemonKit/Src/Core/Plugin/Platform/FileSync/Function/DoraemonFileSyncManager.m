//
//  DoraemonFileSyncManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/6/10.
//

#import "DoraemonFileSyncManager.h"
#import <GCDWebServer/GCDWebServerRequest.h>
#import <GCDWebServer/GCDWebServerDataRequest.h>
#import <GCDWebServer/GCDWebServerDataResponse.h>
#import <GCDWebServer/GCDWebServerMultiPartFormRequest.h>
#import <GCDWebServer/GCDWebServerFileResponse.h>
#import "DoraemonAppInfoUtil.h"

#define DK_SERVER_PORT 9002

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
#pragma mark - file
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
    
    [self addHandlerForMethod:@"POST"
                         path:@"/deleteFile"
                 requestClass:[GCDWebServerMultiPartFormRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf deleteFile:(GCDWebServerMultiPartFormRequest *)request];
    }];
    
    [self addHandlerForMethod:@"POST"
                         path:@"/rename"
                 requestClass:[GCDWebServerMultiPartFormRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf rename:(GCDWebServerMultiPartFormRequest *)request];
    }];
    
    [self addHandlerForMethod:@"POST"
                         path:@"/saveFile"
                 requestClass:[GCDWebServerMultiPartFormRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf saveFile:(GCDWebServerMultiPartFormRequest *)request];
    }];
    
#pragma mark - database
    [self addHandlerForMethod:@"GET"
                         path:@"/getAllTable"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf getAllTable:request];
    }];
    
    [self addHandlerForMethod:@"GET"
                         path:@"/getTableData"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf getTableData:request];
    }];
    
    [self addHandlerForMethod:@"POST"
                         path:@"/insertRow"
                 requestClass:[GCDWebServerDataRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf insertRow:(GCDWebServerDataRequest *)request];
    }];
    
    [self addHandlerForMethod:@"POST"
                         path:@"/updateRow"
                 requestClass:[GCDWebServerDataRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf updateRow:(GCDWebServerDataRequest *)request];
    }];
    
    [self addHandlerForMethod:@"POST"
                         path:@"/deleteRow"
                 requestClass:[GCDWebServerDataRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        return [weakSelf deleteRow:(GCDWebServerDataRequest *)request];
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
    [self startWithPort:DK_SERVER_PORT bonjourName:@"Hello DoKit"];
}


#pragma mark -- 服务具体处理

- (GCDWebServerResponse *)responseWhenFailed {
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:[self getCode:0 data:nil]];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    return response;
}

- (GCDWebServerResponse *)deleteRow:(GCDWebServerDataRequest *)request {
    NSDictionary *data = [NSJSONSerialization JSONObjectWithData:request.data options:0 error:nil];
    NSString *dirPath = data[@"dirPath"];
    NSString *fileName = data[@"fileName"];
    NSString *tableName = data[@"tableName"];
    NSArray *rowDatas = data[@"rowDatas"];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];
    
    if (![_fm fileExistsAtPath:targetPath]) {
        return [self responseWhenFailed];
    }
    
    FMDatabase *db = [FMDatabase databaseWithPath:targetPath];
    if (![db open]) {
        return [self responseWhenFailed];
    }
    
    /**
     构造sql
     DELETE FROM tableName
     WHERE pk=pkValue;
     */
    NSMutableString *sql = [NSString stringWithFormat:@"DELETE FROM %@ WHERE ", tableName].mutableCopy;
    __block NSString *pk = nil;
    __block id pkValue = nil;
    
    [rowDatas enumerateObjectsUsingBlock:^(NSDictionary *  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj[@"isPrimary"] boolValue]) {
            pk = obj[@"title"];
            pkValue = obj[@"value"];
            *stop = YES;
        }
    }];
    
    [sql appendString:[NSString stringWithFormat:@"%@=%@;", pk, pkValue]];

    BOOL sus = [db executeUpdate:sql];;
    
    [db close];
    
    if (sus) {
        GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:[self getCode:200 data:nil]];
        [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
        return response;
    } else {
        return [self responseWhenFailed];
    }
}

- (GCDWebServerResponse *)updateRow:(GCDWebServerDataRequest *)request {
    NSDictionary *data = [NSJSONSerialization JSONObjectWithData:request.data options:0 error:nil];
    NSString *dirPath = data[@"dirPath"];
    NSString *fileName = data[@"fileName"];
    NSString *tableName = data[@"tableName"];
    NSArray *rowDatas = data[@"rowDatas"];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];
    
    if (![_fm fileExistsAtPath:targetPath]) {
        return [self responseWhenFailed];
    }
    
    FMDatabase *db = [FMDatabase databaseWithPath:targetPath];
    if (![db open]) {
        return [self responseWhenFailed];
    }
    
    /**
     构造sql
     UPDATE tableName
     SET title=value, title_2=value_2
     WHERE pk=pkValue;
     */
    NSMutableString *sql = [NSString stringWithFormat:@"UPDATE %@ SET ", tableName].mutableCopy;
    NSMutableArray *newValues = @[].mutableCopy;
    __block NSString *pk = nil;
    __block id pkValue = nil;
    
    @autoreleasepool {
        [rowDatas enumerateObjectsUsingBlock:^(NSDictionary *  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([obj[@"isPrimary"] boolValue]) {
                pk = obj[@"title"];
                pkValue = obj[@"value"];
            } else {
                NSString *title = obj[@"title"];
                id value = obj[@"value"] != nil ? obj[@"value"] : @"NULL";
                if ([value isKindOfClass:[NSString class]] && ![value isEqualToString:@"NULL"]) {
                    value = [NSString stringWithFormat:@"'%@'", value];
                }
                NSString *newValue = [NSString stringWithFormat:@"%@=%@", title, value];
                [newValues addObject:newValue];
            }
        }];
    }
    
    NSString *newValuesStr = [newValues componentsJoinedByString:@","];
    [sql appendString:newValuesStr];
    NSString *condition = [NSString stringWithFormat:@" WHERE %@=%@;", pk, pkValue];
    [sql appendString:condition];

    BOOL sus = [db executeUpdate:sql];;
    
    [db close];
    
    if (sus) {
        GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:[self getCode:200 data:nil]];
        [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
        return response;
    } else {
        return [self responseWhenFailed];
    }
}

- (GCDWebServerResponse *)getTableData:(GCDWebServerRequest *)request {
    NSDictionary *query = request.query;
    NSString *dirPath = query[@"dirPath"];
    NSString *fileName = query[@"fileName"];
    NSString *tableName = query[@"tableName"];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];

    if (![_fm fileExistsAtPath:targetPath]) {
        return [self responseWhenFailed];
    }
    
    FMDatabase *db = [FMDatabase databaseWithPath:targetPath];
    if (![db open]) {
        return [self responseWhenFailed];
    }
    
    NSMutableArray *fieldInfo = @[].mutableCopy;
    NSMutableArray *rows = @[].mutableCopy;
    FMResultSet *tableInfo = [db executeQuery:[NSString stringWithFormat:@"PRAGMA table_info(%@)", tableName]];
    while ([tableInfo next]) {
        NSString *title = [tableInfo stringForColumn:@"name"];
        BOOL isPrimary = [tableInfo boolForColumn:@"pk"];
        if (title) {
            [fieldInfo addObject:@{@"title" : title,
                                   @"isPrimary" : @(isPrimary)}];
        }
    }
    
    FMResultSet *set = [db executeQuery:[NSString stringWithFormat:@"SELECT * FROM %@;", tableName]];
    while ([set next]) {
        NSDictionary *row = [NSDictionary dictionaryWithDictionary:set.resultDictionary];
        [rows addObject:row];
    }
    
    [db close];
    
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:@{@"fieldInfo" : fieldInfo,
                                                                                        @"rows" : rows}];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)insertRow:(GCDWebServerDataRequest *)request {
    NSDictionary *data = [NSJSONSerialization JSONObjectWithData:request.data options:0 error:nil];
    NSString *dirPath = data[@"dirPath"];
    NSString *fileName = data[@"fileName"];
    NSString *tableName = data[@"tableName"];
    NSArray *rowDatas = data[@"rowDatas"];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];
    
    if (![_fm fileExistsAtPath:targetPath]) {
        return [self responseWhenFailed];
    }
    
    FMDatabase *db = [FMDatabase databaseWithPath:targetPath];
    if (![db open]) {
        return [self responseWhenFailed];
    }
    
    //获取列名
    NSMutableArray *colArr = @[].mutableCopy;
    FMResultSet *tableInfo = [db executeQuery:[NSString stringWithFormat:@"PRAGMA table_info(%@)", tableName]];
    while ([tableInfo next]) {
        NSString *colName = [tableInfo stringForColumn:@"name"];
        if (colName) {
            [colArr addObject:colName];
        }
    }
    
    NSString *columns = [colArr componentsJoinedByString:@","];
    
    BOOL sus = NO;
    
    if (rowDatas.count) {
        NSMutableString *sql = [NSString stringWithFormat:@"INSERT INTO %@(%@) VALUES ", tableName, columns].mutableCopy;
        NSMutableArray *allValues = @[].mutableCopy;
        @autoreleasepool {
            /**
             构造sql
             INSERT INTO
             tableName(key_1,key_2,key_3)
             VALUES
             (value_1,value_2,value_3),
             (value_4,value_5,value_6);
             */
            [rowDatas enumerateObjectsUsingBlock:^(NSDictionary *  _Nonnull data, NSUInteger idx, BOOL * _Nonnull stop) {
                NSMutableArray *arr = @[].mutableCopy;
                [colArr enumerateObjectsUsingBlock:^(NSString *  _Nonnull colName, NSUInteger idx, BOOL * _Nonnull stop) {
                    id value = data[colName] != nil ? data[colName] : @"NULL";
                    if ([value isKindOfClass:[NSString class]] && ![value isEqualToString:@"NULL"]) {
                        value = [NSString stringWithFormat:@"'%@'", value];
                    }
                    [arr addObject:value];
                }];
                NSString *values = [arr componentsJoinedByString:@","];
                [allValues addObject:[NSString stringWithFormat:@"(%@)", values]];
            }];
        }
        
        NSString *allValuesStr = [allValues componentsJoinedByString:@","];
        [sql appendString:allValuesStr];
        [sql appendString:@";"];
        
        sus = [db executeUpdate:sql];
    }
    
    [db close];
    
    if (sus) {
        GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:[self getCode:200 data:nil]];
        [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
        return response;
    } else {
        return [self responseWhenFailed];
    }
}

- (GCDWebServerResponse *)getAllTable:(GCDWebServerRequest *)request {
    NSDictionary *query = request.query;
    NSString *dirPath = query[@"dirPath"];
    NSString *fileName = query[@"fileName"];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];

    if (![_fm fileExistsAtPath:targetPath]) {
        return [self responseWhenFailed];
    }
    
    FMDatabase *db = [FMDatabase databaseWithPath:targetPath];
    if (![db open]) {
        return [self responseWhenFailed];
    }
    
    NSString *sql = @"SELECT * FROM sqlite_master WHERE type='table' ORDER BY name;";
    FMResultSet *set = [db executeQuery:sql];
    NSMutableArray *data = @[].mutableCopy;
    while ([set next]) {
        NSString *name = [set stringForColumnIndex:1];
        [data addObject:name];
    }
    
    [db close];
    
    NSMutableDictionary *info = [[NSMutableDictionary alloc] init];
    [info setValue:@(200) forKey:@"code"];
    [info setValue:data forKey:@"data"];

    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:info];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)saveFile:(GCDWebServerMultiPartFormRequest *)request {
    NSString *dirPath = [[request firstArgumentForControlName:@"dirPath"] string];
    NSString *fileName = [[request firstArgumentForControlName:@"fileName"] string];
    NSString *content = [[request firstArgumentForControlName:@"content"] string];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];

    NSDictionary *res;
    if (![_fm createFileAtPath:targetPath contents:[content dataUsingEncoding:NSUTF8StringEncoding] attributes:nil]) {
        NSLog(@"Failed save file at \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    }else{
        res = [self getCode:200 data:nil];
    }
    
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)deleteFile:(GCDWebServerMultiPartFormRequest *)request {
    NSString *dirPath = [[request firstArgumentForControlName:@"dirPath"] string];
    NSString *fileName = [[request firstArgumentForControlName:@"fileName"] string];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, fileName];
    NSError *error = nil;

    NSDictionary *res;
    if (![_fm removeItemAtPath:targetPath error:&error]) {
        NSLog(@"Failed deleting file at \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    }else{
        res = [self getCode:200 data:nil];
    }
    
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)rename:(GCDWebServerMultiPartFormRequest *)request {
    NSString *dirPath = [[request firstArgumentForControlName:@"dirPath"] string];
    NSString *oldName = [[request firstArgumentForControlName:@"oldName"] string];
    NSString *newName = [[request firstArgumentForControlName:@"newName"] string];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, oldName];
    NSString *destinationPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, newName];
    NSError *error = nil;

    NSDictionary *res;
    if (![_fm moveItemAtPath:targetPath toPath:destinationPath error:&error]) {
        NSLog(@"Failed rename file at \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    }else{
        res = [self getCode:200 data:nil];
    }
    
    GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:res];
    [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
    
    return response;
}

- (GCDWebServerResponse *)getDeviceInfo{
    NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
    [dic setValue:[DoraemonAppInfoUtil iphoneName] forKey:@"deviceName"];
    [dic setValue:[DoraemonAppInfoUtil uuid] forKey:@"deviceId"];
    [dic setValue:[NSString stringWithFormat:@"%@:%@", [DoraemonAppInfoUtil getIPAddress:YES], @(DK_SERVER_PORT)] forKey:@"deviceIp"];
    
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
        dic[@"dirPath"] = dirPath;
        dic[@"isRootPath"] = [path isEqualToString:rootPath] ? @YES : @NO;
        dic[@"fileName"] = path;
//        dic[@"fileUrl"] = [self getRelativeFilePath:fullPath];
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
    GCDWebServerMultiPartFile *file = [request firstFileForControlName:@"file"];
    NSString *dirPath = [[request firstArgumentForControlName:@"dirPath"] string];
    NSString *rootPath = NSHomeDirectory();
    NSString *targetPath = [NSString stringWithFormat:@"%@/%@/%@", rootPath, dirPath, file.fileName];
    NSError *error = nil;
    
    if (![_fm fileExistsAtPath:[NSString stringWithFormat:@"%@/%@", rootPath, dirPath]]) {
        [_fm createDirectoryAtPath:[NSString stringWithFormat:@"%@/%@", rootPath, dirPath] withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
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
    if (![[NSFileManager defaultManager] createDirectoryAtPath:targetPath withIntermediateDirectories:YES attributes:nil error:nil]) {
        NSLog(@"Failed creating directory \"%@\"", targetPath);
        res = [self getCode:0 data:nil];
    } else {
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
