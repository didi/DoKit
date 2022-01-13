//
//  DoraemMultiMockManger.h
//  DoraemonKit
//
//  Created by wzp on 2021/10/1.
//

#import <Foundation/Foundation.h>
@class DoraemMultiCaseListModel;

@protocol DoraemonMultiNetworkInterceptorDelegate;


@interface DoraemMultiItem : NSObject

@property (nonatomic, strong)NSURLResponse *requese;
@property (nonatomic, strong)NSURLRequest  *request;
@property (nonatomic, strong)NSString  *requestBody;
@property (nonatomic, strong)NSString  *responseBody;
@property (nonatomic, strong)NSString  *key;
@property (nonatomic, strong)NSString  *pId;
@property (nonatomic, strong)NSString  *caseId;
@property (nonatomic, strong)NSString  *contentType;
@property (nonatomic, strong)NSString  *method;
@property (nonatomic, strong)NSString  *path;
@property (nonatomic, strong)NSDictionary  *query;

@end

NS_ASSUME_NONNULL_BEGIN

@interface DoraemMultiMockManger : NSObject <DoraemonMultiNetworkInterceptorDelegate>

@property (nonatomic, strong) NSArray *excludeArray;
@property (nonatomic, strong) NSString *caseId;
@property (nonatomic, strong) NSString *key;
@property (nonatomic, strong) DoraemMultiCaseListModel  *selectCaseModel;


+ (DoraemMultiMockManger *)sharedInstance;


@end

NS_ASSUME_NONNULL_END
