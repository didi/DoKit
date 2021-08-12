//
//  DoraemonNetFlowHttpModel.m
//  DoraemonKit
//
//  Created by yixiang on 2018/4/11.
//

#import "DoraemonNetFlowHttpModel.h"
#import "DoraemonNetFlowManager.h"
#import "NSURLRequest+Doraemon.h"
#import "DoraemonUrlUtil.h"

@implementation DoraemonNetFlowHttpModel

+ (void)dealWithResponseData:(NSData *)responseData response:(NSURLResponse*)response request:(NSURLRequest *)request complete:(void (^)(DoraemonNetFlowHttpModel *model))complete {
    DoraemonNetFlowHttpModel *httpModel = [[DoraemonNetFlowHttpModel alloc] init];
    //request
    httpModel.request = request;
    httpModel.requestId = request.requestId;
    httpModel.url = [request.URL absoluteString];
    httpModel.method = request.HTTPMethod;
    //response
    httpModel.mineType = response.MIMEType;
    httpModel.response = response;
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    httpModel.statusCode = [NSString stringWithFormat:@"%d",(int)httpResponse.statusCode];
    httpModel.responseData = responseData;
    httpModel.responseBody = [DoraemonUrlUtil convertJsonFromData:responseData];
    httpModel.totalDuration = [NSString stringWithFormat:@"%fs",[[NSDate date] timeIntervalSince1970] - request.startTime.doubleValue];
    httpModel.downFlow = [NSString stringWithFormat:@"%lli",[DoraemonUrlUtil getResponseLength:(NSHTTPURLResponse *)response data:responseData]];
    [[DoraemonNetFlowManager shareInstance] httpBodyFromRequest:request bodyCallBack:^(NSData *body) {
        httpModel.requestBody = [DoraemonUrlUtil convertJsonFromData:body];
        NSUInteger length = [DoraemonUrlUtil getHeadersLengthWithRequest:request] + [body length];
        httpModel.uploadFlow = [NSString stringWithFormat:@"%zi", length];
        complete(httpModel);
    }];
}

@end
