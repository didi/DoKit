//
//  DoraemonFileSyncManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/6/10.
//

#import "DoraemonFileSyncManager.h"
#import <GCDWebServer/GCDWebServerRequest.h>
#import <GCDWebServer/GCDWebServerDataResponse.h>


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
    
    [self addHandlerForMethod:@"GET"
                         path:@"/list"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse * _Nullable(__kindof GCDWebServerRequest * _Nonnull request) {
        GCDWebServerResponse *response = [GCDWebServerDataResponse responseWithJSONObject:@{@"rows" : @"XXXX"}];
        [response setValue:@"*" forAdditionalHeader:@"Access-Control-Allow-Origin"];
        
        return response;
    }];
    
    //__weak typeof(self)weakSelf = self;
    
    [self addHandlerForMethod:@"GET"
                         path:@"/databaseList"
                 requestClass:[GCDWebServerRequest class]
                 processBlock:^GCDWebServerResponse*(GCDWebServerRequest* request) {
        
        return [GCDWebServerDataResponse responseWithJSONObject:@{@"rows" : @"gagaagg"}];
    }];
}

- (void)startServer{
    [self startWithPort:9002 bonjourName:@"Hello DoKit"];
}

@end
