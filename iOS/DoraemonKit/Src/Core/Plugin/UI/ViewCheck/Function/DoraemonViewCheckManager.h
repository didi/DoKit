//
//  DoraemonViewCheckManager.h
//  DoraemonKit
//
//  Created by yixiang on 2018/3/28.
//

#import <Foundation/Foundation.h>

@interface DoraemonViewCheckManager : NSObject

+ (DoraemonViewCheckManager *)shareInstance;

- (void)show;

- (void)hidden;

@end
