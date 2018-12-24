//
//  DoraemonNetFlowHttpModel.h
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import <Foundation/Foundation.h>

@interface DoraemonNetFlowHttpModel : NSObject

@property (nonatomic, copy) NSString *requestId;
@property (nonatomic, copy) NSString *url;
@property (nonatomic, copy) NSString *method;
@property (nonatomic, copy) NSString *requestBody;
@property (nonatomic, copy) NSString *statusCode;
@property (nonatomic, copy) NSData *responseData;
@property (nonatomic, copy) NSString *responseBody;
@property (nonatomic, copy) NSString *mineType;
@property (nonatomic, assign) NSTimeInterval startTime;
@property (nonatomic, assign) NSTimeInterval endTime;
@property (nonatomic, copy) NSString *totalDuration;
@property (nonatomic, copy) NSString *uploadFlow;//上行流量
@property (nonatomic, copy) NSString *downFlow;//下行流量

@property (nonatomic, strong) NSURLRequest *request;
@property (nonatomic, strong) NSURLResponse *response;

+ (DoraemonNetFlowHttpModel *)dealWithResponseData:(NSData *)responseData response:(NSURLResponse*)response request:(NSURLRequest *)request;

@end
