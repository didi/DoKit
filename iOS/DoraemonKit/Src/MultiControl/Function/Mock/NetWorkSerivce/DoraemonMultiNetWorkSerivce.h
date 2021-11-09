//
//  DoraemonMultiNetWorkSerivce.h
//  DoraemonKit
//
//  Created by wzp on 2021/9/29.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMultiNetWorkSerivce : NSObject

/*
 * 获取全局配置
 * /app/multiControl/getConfig
 */
+ (void)multiControlGetConfigWithSus:(void(^)(id responseObject))sus
                                fail:(void(^)(NSError *error))fail ;


/*
 * 开始录制
 * /app/multiControl/startRecord
 */
+ (void)startRecordRequestWithSus:(void(^)(id responseObject))sus
                             fail:(void(^)(NSError *error))fail;


/*
 * 结束录制
 * /app/multiControl/endRecord
 */
+ (void)endRecordWithCaseID:(NSString *)caseID
                 personName:(NSString *)personName
                   caseName:(NSString *)caseName
                        sus:(void(^)(id responseObject))su
                       fail:(void(^)(NSError *error))fail;

/*
 * 接口信息上传
 * /app/multiControl/uploadApiInfo
 */
+ (void)uploadApiInfoWithCaseID:(NSString *)caseID
                            key:(NSString *)key
                           path:(NSString *)path
                            sus:(void(^)(id responseObject))sus
                           fail:(void(^)(NSError *error))fail;

/*
 * 获取测试用例列表
 * /app/multiControl/caseList
 */
+ (void)getCaseListWithSus:(void(^)(id responseObject))sus
                      fail:(void(^)(NSError *error))fail ;
@end

NS_ASSUME_NONNULL_END
