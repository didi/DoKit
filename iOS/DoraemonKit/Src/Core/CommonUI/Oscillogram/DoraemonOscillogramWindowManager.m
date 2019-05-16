//
//  DoraemonOscillogramWindowManager.m
//  AFNetworking
//
//  Created by yixiang on 2019/5/16.
//

#import "DoraemonOscillogramWindowManager.h"
#import "DoraemonFPSOscillogramWindow.h"
#import "DoraemonCPUOscillogramWindow.h"
#import "DoraemonMemoryOscillogramWindow.h"
#import "DoraemonNetFlowOscillogramWindow.h"
#import "DoraemonDefine.h"

@interface DoraemonOscillogramWindowManager()

@property (nonatomic, strong) DoraemonFPSOscillogramWindow *fpsWindow;
@property (nonatomic, strong) DoraemonCPUOscillogramWindow *cpuWindow;
@property (nonatomic, strong) DoraemonMemoryOscillogramWindow *memoryWindow;
@property (nonatomic, strong) DoraemonNetFlowOscillogramWindow *netflowWindow;

@end

@implementation DoraemonOscillogramWindowManager

+ (DoraemonOscillogramWindowManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonOscillogramWindowManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonOscillogramWindowManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    if (self = [super init]) {
        _fpsWindow = [DoraemonFPSOscillogramWindow shareInstance];
        _cpuWindow = [DoraemonCPUOscillogramWindow shareInstance];
        _memoryWindow = [DoraemonMemoryOscillogramWindow shareInstance];
        _netflowWindow = [DoraemonNetFlowOscillogramWindow shareInstance];
    }
    return self;
}

- (void)resetLayout{
    CGFloat offsetY = 0;
    CGFloat width = 0;
    CGFloat height = kDoraemonSizeFrom750_Landscape(240);
    if (kInterfaceOrientationPortrait){
        width = DoraemonScreenWidth;
        offsetY = IPHONE_TOPSENSOR_HEIGHT;
    }else{
        width = DoraemonScreenHeight;
    }
    if (!_fpsWindow.hidden) {
        _fpsWindow.frame = CGRectMake(0, offsetY, width, height);
        offsetY += _fpsWindow.doraemon_height+kDoraemonSizeFrom750(4);
    }
    
    if (!_cpuWindow.hidden) {
        _cpuWindow.frame = CGRectMake(0, offsetY, width, height);
        offsetY += _cpuWindow.doraemon_height+kDoraemonSizeFrom750(4);
    }
    
    if (!_memoryWindow.hidden) {
        _memoryWindow.frame = CGRectMake(0, offsetY, width, height);
        offsetY += _memoryWindow.doraemon_height+kDoraemonSizeFrom750(4);
    }
    
    if (!_netflowWindow.hidden) {
        _netflowWindow.frame = CGRectMake(0, offsetY, width, height);
    }
    
}

@end
