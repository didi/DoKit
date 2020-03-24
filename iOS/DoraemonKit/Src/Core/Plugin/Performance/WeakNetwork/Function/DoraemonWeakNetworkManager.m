//
//  DoraemonWeakNetworkManager.m
//  AFNetworking
//
//  Created by didi on 2019/11/21.
//

#import "DoraemonWeakNetworkManager.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonWeakNetworkHandle.h"
#import "DoraemonDefine.h"
#import "DoraemonWeakNetworkWindow.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonUrlUtil.h"

@interface DoraemonWeakNetworkManager()<DoraemonNetworkInterceptorDelegate,DoraemonNetworkWeakDelegate>

@property (nonatomic, assign) CGFloat sleepTime;
@property (nonatomic, strong) DoraemonWeakNetworkHandle *weakHandle;
@property (nonatomic, strong) NSDate *startTime;
@property (nonatomic, strong) NSTimer *secondTimer;


@end

@implementation DoraemonWeakNetworkManager

+ (DoraemonWeakNetworkManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonWeakNetworkManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonWeakNetworkManager alloc] init];
        instance.shouldWeak = NO;
        instance.sleepTime = 500000;
    });
    return instance;
}

- (void)startRecord{
    [DoraemonWeakNetworkManager shareInstance].startTime = [NSDate date];
    if(!_secondTimer){
        _secondTimer = [NSTimer timerWithTimeInterval:1.0f target:self selector:@selector(doSecondFunction) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:_secondTimer forMode:NSRunLoopCommonModes];
    }
}

- (void)doSecondFunction{
    NSString *str = nil;
    if(![DoraemonWeakNetworkWindow shareInstance].upFlowChanged){
        [[DoraemonWeakNetworkWindow shareInstance] updateFlowValue:@"0" downFlow:str fromWeak:YES];
    }
    if(![DoraemonWeakNetworkWindow shareInstance].downFlowChanged){
        [[DoraemonWeakNetworkWindow shareInstance] updateFlowValue:str downFlow:@"0" fromWeak:YES];
    }
}

- (void)endRecord{
    if(_secondTimer){
        [_secondTimer invalidate];
        _secondTimer = nil;
    }
    [self canInterceptNetFlow:NO];
}

- (void)canInterceptNetFlow:(BOOL)enable{
    _shouldWeak = enable;
    if (enable) {
        [[DoraemonNetworkInterceptor shareInstance] addDelegate:self];
        [DoraemonNetworkInterceptor shareInstance].weakDelegate = self;
        _weakHandle = [[DoraemonWeakNetworkHandle alloc] init];
    }else{
        [DoraemonNetworkInterceptor.shareInstance removeDelegate:self];
        [DoraemonNetworkInterceptor shareInstance].weakDelegate = nil;
    }
}

- (BOOL)shouldWeak{
    return _shouldWeak;
}

- (BOOL)limitSpeed:(NSData *)data isDown:(BOOL)is{
    BOOL result = NO;
    CGFloat speed = is ? _downFlowSpeed : _upFlowSpeed ;
    if(0 == data.length || data.length < (kbChange(speed) ? : kbChange(2000))){
        [self showWeakNetworkWindow:is speed:speed];
        result = YES;
    }
    else{
        [self showWeakNetworkWindow:is speed:speed];
        usleep(_sleepTime);
        [self showWeakNetworkWindow:is speed:speed];
        usleep(_sleepTime);
    }
    [self flowChange:is change:NO];
    return result;
}

- (void)flowChange:(BOOL)isDownFlow change:(BOOL)change{
    if(isDownFlow){
        [DoraemonWeakNetworkWindow shareInstance].downFlowChanged = change;
    }else{
        [DoraemonWeakNetworkWindow shareInstance].upFlowChanged = change;
    }
}

- (void)showWeakNetworkWindow:(BOOL) is speed:(CGFloat)speed{
    NSString *str = nil;
    [self flowChange:is change:YES];
    if(is){
        [[DoraemonWeakNetworkWindow shareInstance] updateFlowValue:str downFlow:[NSString stringWithFormat: @"%f", (kbChange(speed) ? : kbChange(2000))] fromWeak:YES];
    }else{
        [[DoraemonWeakNetworkWindow shareInstance] updateFlowValue:[NSString stringWithFormat: @"%f", (kbChange(speed) ? : kbChange(2000))] downFlow:str fromWeak:YES];
    }
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    [[DoraemonWeakNetworkWindow shareInstance] updateFlowValue:[NSString stringWithFormat:@"%zi",[DoraemonUrlUtil getRequestLength:request]] downFlow:[NSString stringWithFormat:@"%lli",[DoraemonUrlUtil getResponseLength:(NSHTTPURLResponse *)response data:data]] fromWeak:NO];
}

- (BOOL)shouldIntercept {
    return _shouldWeak;
}


#pragma mark - doraemonNSURLProtocolWeakNetDelegate
- (void)handleWeak:(NSData *)data isDown:(BOOL)is{
    NSUInteger count = 0;
    NSData *limitedData = nil;
    NSInteger speed = 0;
    speed = is ? _downFlowSpeed : _upFlowSpeed;
    while (true) {
        limitedData = [_weakHandle weakFlow:data count:count size:kbChange(speed) ? : kbChange(2000)];
        if([self limitSpeed:limitedData isDown:is]){
            return ;
        }
        DoKitLog(@"count == %ld",count);
        [self flowChange:is change:YES];
        count++;
    }
}

- (NSUInteger)delayTime{
    return _delayTime;
}

- (NSInteger)weakNetSelecte{
    return _selecte;
}

@end

