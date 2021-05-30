//
//  DoraemonViewAPluginManager.h
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/2.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DoraemonViewAPluginManager : NSObject

+ (DoraemonViewAPluginManager *)shareInstance;

- (void)showA;

- (void)showB;

- (void)hiddenA;

- (void)hiddenB;

@end
