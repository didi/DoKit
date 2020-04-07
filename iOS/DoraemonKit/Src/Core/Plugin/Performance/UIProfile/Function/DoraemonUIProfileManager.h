//
//  DoraemonUIProfileManager.h
//  DoraemonKit
//
//  Created by xgb on 2019/8/1.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonUIProfileManager : NSObject

@property (nonatomic, assign) BOOL enable;              //default NO

+ (instancetype)sharedInstance;

@end

NS_ASSUME_NONNULL_END
