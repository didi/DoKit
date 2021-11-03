//
//  DoraemonWeexStorageResolver.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStorageResolver.h"

@interface DoraemonWeexStorageResolver()

@property (nonatomic, copy) NSString *weexStorageFile;

@end

@implementation DoraemonWeexStorageResolver

- (instancetype)init{
    self = [super init];
    if (self) {
        NSString *docPath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
        _weexStorageFile = [[docPath stringByAppendingPathComponent:@"wxstorage"] stringByAppendingPathComponent:@"wxstorage.plist"];
    }
    return self;
}

- (NSDictionary *)getWeexStorageInfo{
    if (_weexStorageFile) {
        NSDictionary *dic = [[NSDictionary alloc] initWithContentsOfFile:_weexStorageFile];
        return dic;
    }
    return @{};
}

@end
