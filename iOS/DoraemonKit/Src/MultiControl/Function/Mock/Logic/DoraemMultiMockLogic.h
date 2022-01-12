//
//  DoraemMultiMockLogic.h
//  DoraemonKit
//
//  Created by wzp on 2021/10/1.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemMultiMockLogic : NSObject

/*
 * 打开网络mock
 */
+ (void)openMultiWorkINterceptor;

/*
 * 关闭网络mock
 */
+ (void)closeMultiWorkINterceptor;


+ (void)multiControlGetConfigRequest;

+ (void)startRecord;

/*
 * 结束录制
 */
+ (void)endRecordWithPersonName:(NSString *)personName
                       caseName:(NSString *)caseName
                            sus:(void(^)(id _Nonnull responseObject))sus
                           fail:(void (^) (NSError *_Nonnull error))fail;


/*
 * 获取测试用例列表
 */
+ (void)getMultiCaseListWithSus:(void(^)(id responseObject))sus
                           fail:(void(^)(NSError *error))fail;


/*
 * 获取用例接口列表
 */
+ (void)getCaseApiListWithCaseId:(NSString *)caseId
                             sus:(void(^)(id responseObject))sus
                            fail:(void(^)(NSError *error))fail;

/*
 * 获取用例接口列表
 */
+ (void)getMultiCaseListWithCaseID:(NSString *)caseID key:(NSString *)key;



@end

NS_ASSUME_NONNULL_END
