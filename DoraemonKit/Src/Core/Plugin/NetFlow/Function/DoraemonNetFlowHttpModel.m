//
//  DoraemonNetFlowHttpModel.m
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import "DoraemonNetFlowHttpModel.h"
#import "NSURLRequest+Doraemon.h"
#import "DoraemonUrlUtil.h"

@implementation DoraemonNetFlowHttpModel

+ (DoraemonNetFlowHttpModel *)dealWithResponseData:(NSData *)responseData response:(NSURLResponse*)response request:(NSURLRequest *)request{
    DoraemonNetFlowHttpModel *httpModel = [[DoraemonNetFlowHttpModel alloc] init];
    
    //request
    httpModel.request = request;
    httpModel.requestId = request.requestId;
    httpModel.url = [request.URL absoluteString];
    httpModel.method = request.HTTPMethod;
    NSData *httpBody = [DoraemonUrlUtil getHttpBodyFromRequest:request];
    httpModel.requestBody = [DoraemonUrlUtil convertJsonFromData:httpBody];
    
    httpModel.uploadFlow = [NSString stringWithFormat:@"%zi",[DoraemonUrlUtil getRequestLength:request]];
    
    //response
    httpModel.mineType = response.MIMEType;
    httpModel.response = response;
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    httpModel.statusCode = [NSString stringWithFormat:@"%d",(int)httpResponse.statusCode];
    httpModel.responseData = responseData;
    httpModel.responseBody = [DoraemonUrlUtil convertJsonFromData:responseData];
    httpModel.totalDuration = [NSString stringWithFormat:@"%fs",[[NSDate date] timeIntervalSince1970] - request.startTime.doubleValue];
    httpModel.downFlow = [NSString stringWithFormat:@"%zi",[DoraemonUrlUtil getResponseLength:response data:responseData]];
    
    return httpModel;
    
}




@end
