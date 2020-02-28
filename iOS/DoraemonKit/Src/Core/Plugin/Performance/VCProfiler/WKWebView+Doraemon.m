//
//  WKWebView+Doraemon.m
//  AFNetworking
//
//  Created by didi on 2020/2/7.
//

#import "WKWebView+Doraemon.h"
#import <objc/runtime.h>
#import "NSObject+Doraemon.h"
#import "DoraemonHealthManager.h"

@implementation WKWebView (Doraemon)

+ (void)load{
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(loadRequest:) swizzledSel:@selector(doraemon_loadRequest:)];
}

- (void)doraemon_loadRequest:(NSURLRequest *)request{
    [self doraemon_loadRequest:request];
    NSString *urlString = request.URL.absoluteString;
    urlString = [NSString stringWithFormat:@"wkwebview:%@",urlString];
    [[DoraemonHealthManager sharedInstance] openH5Page:urlString];
}

@end
