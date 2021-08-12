//
//  DoraemonWeexLogModel.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <WeexSDK/WeexSDK.h>

NS_ASSUME_NONNULL_BEGIN
@interface DoraemonWeexLogModel : NSObject

@property (nonatomic, copy) NSString *content;
@property (nonatomic, assign) NSTimeInterval timeInterval;
@property (nonatomic, assign) BOOL expand;
@property (nonatomic, assign) WXLogFlag flag;

@end
NS_ASSUME_NONNULL_END

