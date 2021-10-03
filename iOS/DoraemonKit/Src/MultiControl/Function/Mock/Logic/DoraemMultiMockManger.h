//
//  DoraemMultiMockManger.h
//  DoraemonKit
//
//  Created by wzp on 2021/10/1.
//

#import <Foundation/Foundation.h>

@protocol DoraemonMultiNetworkInterceptorDelegate;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemMultiMockManger : NSObject <DoraemonMultiNetworkInterceptorDelegate>

@property (nonatomic, strong) NSArray *excludeArray;
@property (nonatomic, strong) NSString *caseId;
@property (nonatomic, strong) NSString *key;


+ (DoraemMultiMockManger *)sharedInstance;


@end

NS_ASSUME_NONNULL_END
