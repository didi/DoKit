//
//  DoraemonFileSyncManager.h
//  DoraemonKit
//
//  Created by didi on 2020/6/10.
//

#import <Foundation/Foundation.h>
#import <GCDWebServer/GCDWebServer.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonFileSyncManager : GCDWebServer

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL start;//服务时候开启

- (void)startServer;

@end

NS_ASSUME_NONNULL_END
