//
//  DoraemonDemoMemoryLeakModel.h
//  DoraemonKitDemo
//
//  Created by didi on 2019/10/6.
//  Copyright © 2019 yixiang. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonDemoMemoryLeakModelBlock)(void);

@interface DoraemonDemoMemoryLeakModel : NSObject

- (void)addBlock:(DoraemonDemoMemoryLeakModelBlock)block;

- (void)install;

@end

NS_ASSUME_NONNULL_END
