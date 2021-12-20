//
//  DoraemonNetFlowManager.h
//  DoraemonKit
//
//  Created by yixiang on 2018/4/11.
//

#import <Foundation/Foundation.h>

typedef void(^HttpBodyCallBack)(NSData *body);

@interface DoraemonNetFlowManager : NSObject

+ (DoraemonNetFlowManager *)shareInstance;

@property (nonatomic, strong) NSDate *startInterceptDate;
@property (nonatomic, assign) BOOL canIntercept;

- (void)canInterceptNetFlow:(BOOL)enable;

- (void)httpBodyFromRequest:(NSURLRequest *)request bodyCallBack:(HttpBodyCallBack)complete;

@end
