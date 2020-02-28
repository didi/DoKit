//
//  DoraemonLargeImageDetectionManager.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonCacheManager.h"
#import "DoraemonResponseImageModel.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonUrlUtil.h"

static DoraemonLargeImageDetectionManager *instance = nil;

@interface DoraemonLargeImageDetectionManager() <DoraemonNetworkInterceptorDelegate>
@end

@implementation DoraemonLargeImageDetectionManager {
    dispatch_semaphore_t semaphore;
    BOOL _isDetecting;
}

+ (instancetype)shareInstance {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[DoraemonLargeImageDetectionManager alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _images = [NSMutableArray array];
        semaphore = dispatch_semaphore_create(1);
        _isDetecting = NO;
        _minimumDetectionSize = 500 * 1024;
    }
    return self;
}

- (void)setDetecting:(BOOL)detecting {
    _isDetecting = detecting;
    [self updateInterceptStatus];
}

- (BOOL)detecting {
    return _isDetecting;
}

- (void)updateInterceptStatus {
    if (_isDetecting) {
        [[DoraemonNetworkInterceptor shareInstance] addDelegate: self];
    } else {
        [[DoraemonNetworkInterceptor shareInstance] removeDelegate: self];
    }
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    if (![response.MIMEType hasPrefix:@"image/"]) {
        return;
    }
    if ([DoraemonUrlUtil getResponseLength:(NSHTTPURLResponse *)response data:data] < self.minimumDetectionSize) {
        return;
    }
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    DoraemonResponseImageModel *model = [[DoraemonResponseImageModel alloc] initWithResponse: response data: data];
    [self.images addObject: model];
    dispatch_semaphore_signal(semaphore);
}


- (BOOL)shouldIntercept {
    return _isDetecting;
}
    
@end
