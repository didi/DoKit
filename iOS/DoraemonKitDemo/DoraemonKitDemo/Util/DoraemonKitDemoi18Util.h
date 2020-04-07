//
//  DoraemonKitDemoi18Util.h
//  DoraemonKitDemo
//
//  Created by didi on 2020/4/4.
//  Copyright © 2020 yixiang. All rights reserved.
//

#import <Foundation/Foundation.h>

#define DoraemonDemoLocalizedString(key)   [DoraemonKitDemoi18Util localizedString:key]

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonKitDemoi18Util : NSObject

+ (NSString *)localizedString:(NSString *)key;

@end

NS_ASSUME_NONNULL_END
