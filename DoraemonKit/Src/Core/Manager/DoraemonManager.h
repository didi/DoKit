//
//  DoraemonManager.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <Foundation/Foundation.h>

@interface DoraemonManager : NSObject

+ (DoraemonManager *)shareInstance;

- (void)install;

@property (nonatomic,strong) NSMutableArray *dataArray;

- (void)addPluginWithTitle:(NSString *)title icon:(NSString *)iconName desc:(NSString *)desc pluginName:(NSString *)entryName atModule:(NSString *)moduleName;

- (void)addStartPlugin:(NSString *)pluginName;

- (void)addH5DoorBlock:(void(^)(NSString *h5Url))block;

- (void)addANRBlock:(void(^)(NSDictionary *anrDic))block;

- (void)addperformanceBlock:(void(^)(NSDictionary *performanceDic))block;

- (void)hiddenDoraemon;

- (void)hiddenHomeWindow;

@end
