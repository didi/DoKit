//
//  DoraemonViewAlignManager.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/16.
//

#import <Foundation/Foundation.h>

@interface DoraemonViewAlignManager : NSObject

+ (DoraemonViewAlignManager *)shareInstance;

- (void)show;

- (void)hidden;

@end
