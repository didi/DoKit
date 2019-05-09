//
//  DoraemonRunTraceHelpManager.h
//  DoraemonKit-DoraemonKit
//
//  Created by S S on 2019/5/9.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonRunTraceHelpManager : NSObject

+ (DoraemonRunTraceHelpManager *)shareInstance;

- (void)show;

- (void)hidden;


@end

NS_ASSUME_NONNULL_END
