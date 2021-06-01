//
//  RelativePositionManager.h
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/26.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DoraemonVisualInfoWindow.h"

@interface RelativePositionManager : NSObject

+ (RelativePositionManager *)shareInstance;

- (void)show;

- (void)hidden;

- (void)refresh;

@end

