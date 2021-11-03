//
//  DoraemonOscillogramWindowManager.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN


@interface DoraemonOscillogramWindowManager : NSObject

+ (DoraemonOscillogramWindowManager *)shareInstance;

- (void)resetLayout;

@end

NS_ASSUME_NONNULL_END
