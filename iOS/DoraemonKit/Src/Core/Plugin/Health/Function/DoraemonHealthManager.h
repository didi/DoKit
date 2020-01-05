//
//  DoraemonHealthManager.h
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHealthManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL start;

@property (nonatomic, copy) NSArray *cellTitle;//说明数据

@end

NS_ASSUME_NONNULL_END
