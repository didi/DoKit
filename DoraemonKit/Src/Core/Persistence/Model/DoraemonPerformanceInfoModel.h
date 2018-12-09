//
//  DoraemonPerformanceInfoModel.h
//  DoraemonKit-DoraemonKit
//
//  Created by ZhangHonglin on 2018/11/7.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonPerformanceInfoModel : NSObject

@property (nonatomic, assign) NSTimeInterval time;

@property (nonatomic, assign) CGFloat value;

@property (nonatomic, assign) CGFloat heightValue;

- (NSDictionary *)dictionary;

@end

NS_ASSUME_NONNULL_END
