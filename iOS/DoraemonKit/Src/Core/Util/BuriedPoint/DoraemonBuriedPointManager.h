//
//  DoraemonBuriedPointManager.h
//  DoraemonKit
//
//  Created by didi on 2020/2/23.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

#define DoKitBP(name) [[DoraemonBuriedPointManager shareManager] addPointName:name];

@interface DoraemonBuriedPointManager : NSObject

+ (DoraemonBuriedPointManager *)shareManager;
- (void)addPointName:(NSString *)name;

@end

NS_ASSUME_NONNULL_END
