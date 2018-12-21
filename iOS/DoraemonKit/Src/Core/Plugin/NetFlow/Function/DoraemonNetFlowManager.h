//
//  DoraemonNetFlowManager.h
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import <Foundation/Foundation.h>

@interface DoraemonNetFlowManager : NSObject

+ (DoraemonNetFlowManager *)shareInstance;

@property (nonatomic, strong) NSDate *startInterceptDate;
@property (nonatomic, assign) BOOL canIntercept;

- (void)canInterceptNetFlow:(BOOL)enable;

+ (void)setEnabled:(BOOL)enabled forSessionConfiguration:(NSURLSessionConfiguration *)sessionConfig;

@end
