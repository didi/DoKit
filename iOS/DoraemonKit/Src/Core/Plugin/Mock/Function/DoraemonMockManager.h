//
//  DoraemonMockManager.h
//  AFNetworking
//
//  Created by didi on 2019/10/31.
//

#import <Foundation/Foundation.h>
#import "DoraemonMockAPI.h"

NS_ASSUME_NONNULL_BEGIN
@interface DoraemonMockManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL mock;
@property (nonatomic, strong) NSMutableArray<DoraemonMockAPI *> *dataArray;

- (void)queryMockData;

- (BOOL)needMock:(NSURLRequest *)request;

- (NSString *)getSceneId:(NSURLRequest *)request;

@end
NS_ASSUME_NONNULL_END

