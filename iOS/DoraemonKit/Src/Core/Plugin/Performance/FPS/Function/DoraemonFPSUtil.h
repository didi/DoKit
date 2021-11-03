//
//  DoraemonFPSUtil.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/9.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonFPSBlock)(NSInteger fps);

@interface DoraemonFPSUtil : NSObject

- (void)start;
- (void)end;
- (void)addFPSBlock:(DoraemonFPSBlock)block;

@end

NS_ASSUME_NONNULL_END
