//
//  DoraemonCacheManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/12.
//

#import "DoraemonCacheManager.h"

static NSString * const kDoraemonLoggerSwitchKey = @"doraemon_env_key";
static NSString * const kDoraemonMockGPSSwitchKey = @"doraemon_mock_gps_key";
static NSString * const kDoraemonMockCoordinateKey = @"doraemon_mock_coordinate_key";
static NSString * const kDoraemonFpsKey = @"doraemon_fps_key";
static NSString * const kDoraemonCpuKey = @"doraemon_cpu_key";
static NSString * const kDoraemonMemoryKey = @"doraemon_memory_key";
static NSString * const kDoraemonNetFlowKey = @"doraemon_netflow_key";
static NSString * const kDoraemonSubThreadUICheckKey = @"doraemon_sub_thread_ui_check_key";
static NSString * const kDoraemonCrashKey = @"doraemon_crash_key";
static NSString * const kDoraemonNSLogKey = @"doraemon_nslog_key";
static NSString * const kDoraemonMethodUseTimeKey = @"doraemon_method_use_time_key";

@implementation DoraemonCacheManager

+ (DoraemonCacheManager *)sharedInstance{
    static dispatch_once_t once;
    static DoraemonCacheManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonCacheManager alloc] init];
    });
    return instance;
}

- (void)saveLoggerSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonLoggerSwitchKey];
    [defaults synchronize];
}

- (BOOL)loggerSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonLoggerSwitchKey];

}

- (void)saveMockGPSSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonMockGPSSwitchKey];
    [defaults synchronize];
}

- (BOOL)mockGPSSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonMockGPSSwitchKey];
}

- (void)saveMockCoordinate:(CLLocationCoordinate2D)coordinate{
    NSDictionary *dic = @{
                          @"longitude":@(coordinate.longitude),
                          @"latitude":@(coordinate.latitude)
                          };
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:dic forKey:kDoraemonMockCoordinateKey];
    [defaults synchronize];
}

- (CLLocationCoordinate2D)mockCoordinate{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSDictionary *dic = [defaults valueForKey:kDoraemonMockCoordinateKey];
    CLLocationCoordinate2D coordinate ;
    if (dic[@"longitude"]) {
        coordinate.longitude = [dic[@"longitude"] doubleValue];
    }else{
        coordinate.longitude = -1.;
    }
    if (dic[@"latitude"]) {
        coordinate.latitude = [dic[@"latitude"] doubleValue];
    }else{
        coordinate.latitude = -1.;
    }
    
    return coordinate;
}

- (void)saveFpsSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonFpsKey];
    [defaults synchronize];
}

- (BOOL)fpsSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonFpsKey];
}

- (void)saveCpuSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonCpuKey];
    [defaults synchronize];
}

- (BOOL)cpuSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonCpuKey];
}

- (void)saveMemorySwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonMemoryKey];
    [defaults synchronize];
}

- (BOOL)memorySwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonMemoryKey];
}

- (void)saveNetFlowSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonNetFlowKey];
    [defaults synchronize];
}

- (BOOL)netFlowSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonNetFlowKey];
}

- (void)saveSubThreadUICheckSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonSubThreadUICheckKey];
    [defaults synchronize];
}

- (BOOL)subThreadUICheckSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonSubThreadUICheckKey];
}

- (void)saveCrashSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonCrashKey];
    [defaults synchronize];
}

- (BOOL)crashSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonCrashKey];
}

- (void)saveNSLogSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonNSLogKey];
    [defaults synchronize];
}

- (BOOL)nsLogSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonNSLogKey];
}

- (void)saveMethodUseTimeSwitch:(BOOL)on{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setBool:on forKey:kDoraemonMethodUseTimeKey];
    [defaults synchronize];
}

- (BOOL)methodUseTimeSwitch{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    return [defaults boolForKey:kDoraemonMethodUseTimeKey];
}

@end
