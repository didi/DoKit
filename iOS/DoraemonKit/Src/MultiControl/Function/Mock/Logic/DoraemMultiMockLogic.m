//
//  DoraemMultiMockLogic.m
//  DoraemonKit
//
//  Created by wzp on 2021/10/1.
//

#import "DoraemMultiMockLogic.h"
#import "DoraemonMultiNetWorkSerivce.h"
#import "DoraemMultiMockManger.h"
#import "DoraemonMultiNetworkInterceptor.h"
#import<CommonCrypto/CommonDigest.h>
/*
 *  一机多控逻辑层 单例
 */

 @interface DoraemMultiMockLogic()

@end

@implementation DoraemMultiMockLogic

/*
 * 打开网络mock
 */
+ (void)openMultiWorkINterceptor {
    [DoraemonMultiNetworkInterceptor shareInstance].shouldIntercept = YES;
    [[DoraemonMultiNetworkInterceptor shareInstance] addDelegate:[DoraemMultiMockManger sharedInstance]];
}

/*
 * 关闭网络mock
 */
+ (void)closeMultiWorkINterceptor {
    
    [[DoraemonMultiNetworkInterceptor shareInstance] removeDelegate:[DoraemMultiMockManger sharedInstance]];
}

/*
 * 获取全局配置
 */
+ (void)multiControlGetConfigRequest {

    [DoraemonMultiNetWorkSerivce multiControlGetConfigWithSus:^(id  _Nonnull responseObject) {
        if (![responseObject isKindOfClass:[NSDictionary class]]) {
            return;
        }
        NSDictionary *dict = (NSDictionary *)responseObject[@"data"];
        NSDictionary * multiControl = [dict objectForKey:@"multiControl"];
        [DoraemMultiMockManger sharedInstance].excludeArray = [multiControl objectForKey:@"exclude"];
    } fail:^(NSError * _Nonnull error) {

    }];
}

/*
 * 开始录制
 */
+ (void)startRecord {
    [DoraemonMultiNetWorkSerivce startRecordRequestWithSus:^(id  _Nonnull responseObject) {
        if (![responseObject isKindOfClass:[NSDictionary class]]) {
            return;
        }
        NSDictionary *dict = (NSDictionary *)responseObject[@"data"];
        [DoraemMultiMockManger sharedInstance].caseId = [dict objectForKey:@"caseId"];
        NSLog(@"[DoraemMultiMockManger sharedInstance].caseId = %@",[DoraemMultiMockManger sharedInstance].caseId);
        
        //开始录制 更新界面的提示的情况
        // add windows 上面的
        
    } fail:^(NSError * _Nonnull error) {
        
    }];

}

/*
 * 结束录制
 */
+ (void)endRecordWithPersonName:(NSString *)personName
                       caseName:(NSString *)caseName
                            sus:(void(^)(id _Nonnull responseObject))sus
                           fail:(void (^) (NSError *_Nonnull error))fail {
    
    [DoraemonMultiNetWorkSerivce endRecordWithCaseID:[DoraemMultiMockManger sharedInstance].caseId  personName:personName caseName:caseName sus:^(id  _Nonnull responseObject) {
        
        if (![responseObject isKindOfClass:[NSDictionary class]]) {
            return;
        }
        NSDictionary *dict = (NSDictionary *)responseObject[@"data"];
        
    } fail:^(NSError * _Nonnull error) {
        
    }];
}


/*
 * 接口信息上传
 */
+ (void)uploadApiInfoWithPath:(NSString *)path{
    
    
    [DoraemonMultiNetWorkSerivce uploadApiInfoWithCaseID:[DoraemMultiMockManger sharedInstance].caseId key:[DoraemMultiMockManger sharedInstance].key path:path sus:^(id  _Nonnull responseObject) {
        
        
    } fail:^(NSError * _Nonnull error) {
        
    }];
}

/*
 * 获取测试用例列表
 */
+ (void)getMultiCaseListWithSus:(void(^)(id responseObject))sus
                           fail:(void(^)(NSError *error))fail  {
    
    [DoraemonMultiNetWorkSerivce getCaseListWithSus:^(id  _Nonnull responseObject) {
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError * _Nonnull error) {
        if(fail){
            fail(error);
        }
    }];

}

/*
 * 获取用例接口列表
 */
+ (void)getMultiCaseListWithCaseID:(NSString *)caseID key:(NSString *)key sus:(void(^)(id responseObject))sus fail:(void(^)(NSError *error))fail {
    
    [DoraemonMultiNetWorkSerivce getCaseApiInfoWithCaseID:caseID key:key sus:^(id  _Nonnull responseObject) {
        
        // 成功处理
        NSLog(@"responseObject === %@",responseObject);
        
        
        
    } fail:^(NSError * _Nonnull error) {
        
        // 失败处理
    }];
    
}




/*
 * 获取用例接口列表
 */
+ (void)getCaseApiListWithCaseId:(NSString *)caseId
                             sus:(void(^)(id responseObject))sus
                            fail:(void(^)(NSError *error))fail {
    
    
    [DoraemonMultiNetWorkSerivce getCaseApiListWithCaseId:caseId sus:^(id  _Nonnull responseObject) {
        // 成功处理
        NSLog(@"responseObject === %@",responseObject);
        
        
    } fail:^(NSError * _Nonnull error) {
        
    }];

}



/*
 * 获取用例接口列表
 */
+ (void)getMultiCaseListWithCaseID:(NSString *)caseID key:(NSString *)key {
    
    [DoraemonMultiNetWorkSerivce getCaseApiInfoWithCaseID:caseID key:key sus:^(id  _Nonnull responseObject) {
        
        // 成功处理
        NSLog(@"responseObject === %@",responseObject);
        
        
        
    } fail:^(NSError * _Nonnull error) {
        
        // 失败处理
    }];
    
}


/*
 *  Md5 计算
 */
+ (NSString *)encodMd5:(NSString *)input {
    
    const char *cStr = [input UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cStr, (CC_LONG)strlen(cStr), digest);
    NSMutableString *output =  [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH *2];
    for (int i = 0; i <CC_MD5_DIGEST_LENGTH; i++) {
        [output appendFormat:@"%02x",digest[i]];
    }

    return output;
}




@end
