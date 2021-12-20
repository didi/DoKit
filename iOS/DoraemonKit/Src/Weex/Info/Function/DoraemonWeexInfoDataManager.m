//
//  DoraemonWeexInfoDataManager.m
//  DoraemonKit
//
//  Created by yixiang on 2019/6/4.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexInfoDataManager.h"
#import "DoraemonWeexInfoAnalyzer.h"

@implementation DoraemonWeexInfoDataManager

+ (nonnull DoraemonWeexInfoDataManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonWeexInfoDataManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonWeexInfoDataManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    if (self = [super init]) {
        [WXAnalyzerCenter addWxAnalyzer:[DoraemonWeexInfoAnalyzer new]];
        [WXAnalyzerCenter setOpen:YES];
    }
    return self;
}

- (void)formatInfo:(NSDictionary *)value{
    if (![value isKindOfClass:NSDictionary.class]) {
        return;
    }
    
    NSString *instanceId = value[@"module"];
    if (![instanceId isKindOfClass:NSString.class]) {
        return;
    }
    if (instanceId) {
        if(!_instanceId){
            _instanceId = instanceId;
            NSLog(@"doraemon instanceId = %@",_instanceId);
        }
        if (![_instanceId isEqualToString:instanceId]) {
            _instanceId = instanceId;
            NSLog(@"doraemon instanceId = %@",_instanceId);
        }
    }
    
    NSDictionary *data = value[@"data"];
    if (![data isKindOfClass:NSDictionary.class]) {
        return;
    }
    NSString *wxBundleUrl = [data objectForKey:@"wxBundleUrl"];
    if (wxBundleUrl) {
        _wxBundleUrl = wxBundleUrl;
        NSLog(@"doraemon wxBundleUrl = %@",_wxBundleUrl);
    }
    
    NSNumber *wxBundleSize = [data objectForKey:@"wxBundleSize"];
    if (wxBundleSize != nil) {
        _wxBundleSize = [NSString stringWithFormat:@"%@",wxBundleSize];
        NSLog(@"doraemon wxBundleSize = %@",_wxBundleSize);
    }
    
    NSString *wxSDKVersion = [data objectForKey:@"wxSDKVersion"];
    if (wxSDKVersion) {
        _wxSDKVersion = wxSDKVersion;
        NSLog(@"doraemon wxSDKVersion = %@",_wxSDKVersion);
    }
    
    NSString *wxJSLibVersion = [data objectForKey:@"wxJSLibVersion"];
    if (wxJSLibVersion) {
        _wxJSLibVersion = wxJSLibVersion;
        NSLog(@"doraemon wxJSLibVersion = %@",_wxJSLibVersion);
    }
    
    NSString *wxBundleType = [data objectForKey:@"wxBundleType"];
    if (wxBundleType) {
        _wxBundleType = wxBundleType;
        NSLog(@"doraemon wxBundleType = %@",_wxBundleType);
    }

    NSString *wxStartDownLoadBundle = [data objectForKey:@"wxStartDownLoadBundle"];
    if (wxStartDownLoadBundle) {
        _wxStartDownLoadBundle = wxStartDownLoadBundle;
        NSLog(@"doraemon wxStartDownLoadBundle = %@",_wxStartDownLoadBundle);
    }
    NSString *wxEndDownLoadBundle = [data objectForKey:@"wxEndDownLoadBundle"];
    if (wxEndDownLoadBundle) {
        _wxEndDownLoadBundle = wxEndDownLoadBundle;
        NSLog(@"doraemon wxEndDownLoadBundle = %@",_wxEndDownLoadBundle);
    }
    NSString *wxRenderTimeOrigin = [data objectForKey:@"wxRenderTimeOrigin"];
    if (wxRenderTimeOrigin) {
        _wxRenderTimeOrigin = wxRenderTimeOrigin;
        NSLog(@"doraemon wxRenderTimeOrigin = %@",_wxRenderTimeOrigin);
    }
    NSString *wxEndLoadBundle = [data objectForKey:@"wxEndLoadBundle"];
    if (wxEndLoadBundle) {
        _wxEndLoadBundle = wxEndLoadBundle;
        NSLog(@"doraemon wxEndLoadBundle = %@",_wxEndLoadBundle);
    }
    NSString *wxFirstInteractionView = [data objectForKey:@"wxFirstInteractionView"];
    if (wxFirstInteractionView) {
        _wxFirstInteractionView = wxFirstInteractionView;
        NSLog(@"doraemon wxFirstInteractionView = %@",_wxFirstInteractionView);
    }
    NSString *wxInteraction = [data objectForKey:@"wxInteraction"];
    if (wxInteraction) {
        _wxInteraction = wxInteraction;
        NSLog(@"doraemon wxInteraction = %@",_wxInteraction);
    }
}

@end
