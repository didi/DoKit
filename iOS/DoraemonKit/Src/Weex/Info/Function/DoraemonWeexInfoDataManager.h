//
//  DoraemonWeexInfoDataManager.h
//  DoraemonKit
//
//  Created by yixiang on 2019/6/4.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@interface DoraemonWeexInfoDataManager : NSObject

+ (nonnull DoraemonWeexInfoDataManager *)shareInstance;

- (void)formatInfo:(NSDictionary *)value;

@property (nonatomic, copy) NSString *instanceId;
@property (nonatomic, copy) NSString *wxBundleUrl;
@property (nonatomic, copy) NSString *wxBundleSize;
@property (nonatomic, copy) NSString *wxSDKVersion;
@property (nonatomic, copy) NSString *wxJSLibVersion;
@property (nonatomic, copy) NSString *wxBundleType;
@property (nonatomic, copy) NSString *wxStartDownLoadBundle;
@property (nonatomic, copy) NSString *wxEndDownLoadBundle;
@property (nonatomic, copy) NSString *wxRenderTimeOrigin;
@property (nonatomic, copy) NSString *wxEndLoadBundle;
@property (nonatomic, copy) NSString *wxFirstInteractionView;
@property (nonatomic, copy) NSString *wxInteraction;

@end
NS_ASSUME_NONNULL_END

