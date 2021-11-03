//
//  DoraemonWeexLogDataSource.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DoraemonWeexLogModel.h"

NS_ASSUME_NONNULL_BEGIN
@interface DoraemonWeexLogDataSource : NSObject

+ (nonnull DoraemonWeexLogDataSource *)shareInstance;

@property (nonatomic ,strong) NSMutableArray<DoraemonWeexLogModel *> *logs;

- (void)addLog:(DoraemonWeexLogModel *)model;

@end
NS_ASSUME_NONNULL_END

