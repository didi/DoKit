//
//  DoraemonMockManager.h
//  AFNetworking
//
//  Created by didi on 2019/10/31.
//

#import <Foundation/Foundation.h>
#import "DoraemonMockAPIModel.h"
#import "DoraemonMockUpLoadModel.h"

NS_ASSUME_NONNULL_BEGIN
@interface DoraemonMockManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, assign) BOOL mock;
@property (nonatomic, strong) NSMutableArray<DoraemonMockAPIModel *> *mockArray;
@property (nonatomic, strong) NSMutableArray<DoraemonMockUpLoadModel *> *upLoadArray;

@property (nonatomic, copy) NSArray *groups;//分组数据
@property (nonatomic, copy) NSArray *states;//状态数据
@property (nonatomic, copy) NSString *mockGroup;//mock数据选中的分组
@property (nonatomic, copy) NSString *mockState;//mock数据选中的状态
@property (nonatomic, copy) NSString *uploadGroup;//上传信息选中的分组
@property (nonatomic, copy) NSString *uploadState;//上传信息选中的状态


- (void)queryMockData;

- (BOOL)needMock:(NSURLRequest *)request;

- (NSString *)getSceneId:(NSURLRequest *)request;

- (BOOL)needSave:(NSURLRequest *)request;

- (NSMutableArray<DoraemonMockAPIModel *> *)filterMockArray;

- (NSMutableArray<DoraemonMockUpLoadModel *> *)filterUpLoadArray;


@end
NS_ASSUME_NONNULL_END

