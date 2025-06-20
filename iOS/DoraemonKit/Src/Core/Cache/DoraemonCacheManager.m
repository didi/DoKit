//
//  DoraemonCacheManager.m
//  DoraemonKit
//
//  Created by yixiang on 2017/12/12.
//

#import "DoraemonCacheManager.h"
#import "DoraemonManager.h"
#import "DoraemonDefine.h"
#import "DoraemonManager.h"

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
static NSString * const kDoraemonLargeImageDetectionKey = @"doraemon_large_image_detection_key";
static NSString * const kDoraemonH5historicalRecord = @"doraemon_historical_record";
static NSString * const kDoraemonJsHistoricalRecord = @"doraemon_js_historical_record";
static NSString * const kDoraemonStartTimeKey = @"doraemon_start_time_key";
static NSString * const kDoraemonStartClassKey = @"doraemon_start_class_key";
static NSString * const kDoraemonANRTrackKey = @"doraemon_anr_track_key";
static NSString * const kDoraemonMemoryLeakKey = @"doraemon_memory_leak_key";
static NSString * const kDoraemonMemoryLeakAlertKey = @"doraemon_memory_leak_alert_key";
static NSString * const kDoraemonAllTestKey = @"doraemon_allTest_window_key";
static NSString * const kDoraemonMockCacheKey = @"doraemon_mock_cache_key";
static NSString * const kDoraemonHealthStartKey = @"doraemon_health_start_key";
#define kDoraemonKitManagerKey [NSString stringWithFormat:@"%@_doraemon_kit_manager_key",DoKitVersion]

@interface DoraemonCacheManager()

@property (nonatomic, strong) NSUserDefaults *defaults;
@property (nonatomic, assign) BOOL memoryLeakOn;
@property (nonatomic, assign) BOOL firstReadMemoryLeakOn;

@end

@implementation DoraemonCacheManager

+ (DoraemonCacheManager *)sharedInstance{
    static dispatch_once_t once;
    static DoraemonCacheManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonCacheManager alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self  = [super init];
    if (self) {
        _defaults = [NSUserDefaults standardUserDefaults];
    }
    return self;
}

- (void)saveLoggerSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonLoggerSwitchKey];
    [_defaults synchronize];
}

- (BOOL)loggerSwitch{
    return [_defaults boolForKey:kDoraemonLoggerSwitchKey];

}

- (void)saveMockGPSSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonMockGPSSwitchKey];
    [_defaults synchronize];
}

- (BOOL)mockGPSSwitch{
    return [_defaults boolForKey:kDoraemonMockGPSSwitchKey];
}

- (void)saveMockCoordinate:(CLLocationCoordinate2D)coordinate{
    NSDictionary *dic = @{
                          @"longitude":@(coordinate.longitude),
                          @"latitude":@(coordinate.latitude)
                          };
    [_defaults setObject:dic forKey:kDoraemonMockCoordinateKey];
    [_defaults synchronize];
}

- (CLLocationCoordinate2D)mockCoordinate{
    NSDictionary *dic = [_defaults valueForKey:kDoraemonMockCoordinateKey];
    CLLocationCoordinate2D coordinate ;
    if (dic[@"longitude"]) {
        coordinate.longitude = [dic[@"longitude"] doubleValue];
    }else{
        coordinate.longitude = 0.;
    }
    if (dic[@"latitude"]) {
        coordinate.latitude = [dic[@"latitude"] doubleValue];
    }else{
        coordinate.latitude = 0.;
    }
    
    return coordinate;
}

- (void)saveFpsSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonFpsKey];
    [_defaults synchronize];
}

- (BOOL)fpsSwitch{
    return [_defaults boolForKey:kDoraemonFpsKey];
}

- (void)saveCpuSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonCpuKey];
    [_defaults synchronize];
}

- (BOOL)cpuSwitch{
    return [_defaults boolForKey:kDoraemonCpuKey];
}

- (void)saveMemorySwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonMemoryKey];
    [_defaults synchronize];
}

- (BOOL)memorySwitch{
    return [_defaults boolForKey:kDoraemonMemoryKey];
}

- (void)saveNetFlowSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonNetFlowKey];
    [_defaults synchronize];
}

- (BOOL)netFlowSwitch{
    return [_defaults boolForKey:kDoraemonNetFlowKey];
}

- (void)saveAllTestSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonAllTestKey];
    [_defaults synchronize];
}

- (BOOL)allTestSwitch{
    return [_defaults boolForKey:kDoraemonAllTestKey];
}

- (void)saveLargeImageDetectionSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonLargeImageDetectionKey];
    [_defaults synchronize];
}

- (BOOL)largeImageDetectionSwitch{
    return [_defaults boolForKey: kDoraemonLargeImageDetectionKey];
}

- (void)saveSubThreadUICheckSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonSubThreadUICheckKey];
    [_defaults synchronize];
}

- (BOOL)subThreadUICheckSwitch{
    return [_defaults boolForKey:kDoraemonSubThreadUICheckKey];
}

- (void)saveCrashSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonCrashKey];
    [_defaults synchronize];
}

- (BOOL)crashSwitch{
    return [_defaults boolForKey:kDoraemonCrashKey];
}

- (void)saveNSLogSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonNSLogKey];
    [_defaults synchronize];
}

- (BOOL)nsLogSwitch{
    return [_defaults boolForKey:kDoraemonNSLogKey];
}

- (void)saveMethodUseTimeSwitch:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonMethodUseTimeKey];
    [_defaults synchronize];
}

- (BOOL)methodUseTimeSwitch{
    return [_defaults boolForKey:kDoraemonMethodUseTimeKey];
}

- (void)saveStartTimeSwitch:(BOOL)on {
    [_defaults setBool:on forKey:kDoraemonStartTimeKey];
    [_defaults synchronize];
}

- (BOOL)startTimeSwitch{
    return [_defaults boolForKey:kDoraemonStartTimeKey];
}

- (void)saveANRTrackSwitch:(BOOL)on {
    [_defaults setBool:on forKey:kDoraemonANRTrackKey];
    [_defaults synchronize];
}

- (BOOL)anrTrackSwitch {
    return [_defaults boolForKey:kDoraemonANRTrackKey];
}

- (NSArray<NSString *> *)h5historicalRecord {
    return [_defaults objectForKey:kDoraemonH5historicalRecord];
}

- (void)saveH5historicalRecordWithText:(NSString *)text {
    /// 过滤异常数据
    if (!text || text.length <= 0) { return; }
    
    NSArray *records = [self h5historicalRecord];
    
    NSMutableArray *muarr = [NSMutableArray arrayWithArray:records];
    
    /// 去重
    if ([muarr containsObject:text]) {
        if ([muarr.firstObject isEqualToString:text]) {
            return;
        }
        [muarr removeObject:text];
    }
    [muarr insertObject:text atIndex:0];
    
    /// 限制数量
    if (muarr.count > 10) { [muarr removeLastObject]; }
    
    [_defaults setObject:muarr.copy forKey:kDoraemonH5historicalRecord];
    [_defaults synchronize];
}

- (void)clearAllH5historicalRecord {
    [_defaults removeObjectForKey:kDoraemonH5historicalRecord];
    [_defaults synchronize];
}

- (void)clearH5historicalRecordWithText:(NSString *)text {
    /// 过滤异常数据
    if (!text || text.length <= 0) { return; }
    NSArray *records = [self h5historicalRecord];
    /// 不包含
    if (![records containsObject:text]) { return; }
    NSMutableArray *muarr = [NSMutableArray array];
    if (records && records.count > 0) { [muarr addObjectsFromArray:records]; }
    [muarr removeObject:text];
    
    
    if (muarr.count > 0) {
        [_defaults setObject:muarr.copy forKey:kDoraemonH5historicalRecord];
    } else {
        [_defaults removeObjectForKey:kDoraemonH5historicalRecord];
    }
    [_defaults synchronize];
}

- (NSArray<NSDictionary *> *)jsHistoricalRecord {
    return [_defaults arrayForKey:kDoraemonJsHistoricalRecord];
}

- (NSString *)jsHistoricalRecordForKey:(NSString *)key {
    NSArray *history = [self jsHistoricalRecord] ?: @[];
    for (NSDictionary *dict in history) {
        //是否同名配置
        if ([[dict objectForKey:@"key"] isEqualToString:key]) {
            return [dict objectForKey:@"value"];
        }
    }
    return nil;
}

- (void)saveJsHistoricalRecordWithText:(NSString *)text forKey:(NSString *)key {
    NSString *saveKey = [NSString stringWithFormat:@"%.0f", NSDate.date.timeIntervalSince1970];
    if (key.length > 0) {
        saveKey = key;
    }
    NSMutableArray *list = [NSMutableArray array];
    BOOL matched = NO;
    NSArray *history = [self jsHistoricalRecord] ?: @[];
    for (NSDictionary *dict in history) {
        //是否同名配置
        if ([[dict objectForKey:@"key"] isEqualToString:saveKey]) {
            [list addObject:@{
                @"key": saveKey,
                @"value": text
            }];
            matched = YES;
            continue;
        }
        [list addObject:dict];
    }
    if (!matched) {
        [list insertObject:@{
            @"key": saveKey,
            @"value": text
        } atIndex:0];
    }
    [_defaults setObject:list forKey:kDoraemonJsHistoricalRecord];
    [_defaults synchronize];
}

- (void)clearJsHistoricalRecordWithKey:(NSString *)key {
    if (!key) {
        return;
    }
    NSMutableArray *list = [NSMutableArray array];
    NSArray *history = [self jsHistoricalRecord] ?: @[];
    for (NSDictionary *dict in history) {
        //是否同名配置
        if ([[dict objectForKey:@"key"] isEqualToString:key]) {
            continue;
        }
        [list addObject:dict];
    }
    [_defaults setObject:list forKey:kDoraemonJsHistoricalRecord];
    [_defaults synchronize];
}

- (void)saveStartClass : (NSString *)startClass {
    [_defaults setObject:startClass forKey:kDoraemonStartClassKey];
    [_defaults synchronize];
}

- (NSString *)startClass {
    NSString *startClass = [_defaults objectForKey:kDoraemonStartClassKey];
    return startClass;
}

// 内存泄漏开关
- (void)saveMemoryLeak:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonMemoryLeakKey];
    [_defaults synchronize];
}
- (BOOL)memoryLeak{
    if (_firstReadMemoryLeakOn) {
        return _memoryLeakOn;
    }
    _firstReadMemoryLeakOn = YES;
    _memoryLeakOn = [_defaults boolForKey:kDoraemonMemoryLeakKey];
     
    return _memoryLeakOn;
}

// 内存泄漏弹框开关
- (void)saveMemoryLeakAlert:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonMemoryLeakAlertKey];
    [_defaults synchronize];
}
- (BOOL)memoryLeakAlert{
    return [_defaults boolForKey:kDoraemonMemoryLeakAlertKey];
}

// mockapi本地缓存情况
- (void)saveMockCache:(NSArray *)mocks{
    [_defaults setObject:mocks forKey:kDoraemonMockCacheKey];
    [_defaults synchronize];
}
- (NSArray *)mockCahce{
    return [_defaults objectForKey:kDoraemonMockCacheKey];
}

// 健康体检开关
- (void)saveHealthStart:(BOOL)on{
    [_defaults setBool:on forKey:kDoraemonHealthStartKey];
    [_defaults synchronize];
}
- (BOOL)healthStart{
    return [_defaults boolForKey:kDoraemonHealthStartKey];
}

// Kit Manager数据保存 只保存内部数据
- (void)saveKitManagerData:(NSArray *)dataArray{
    NSMutableArray *mutableDataArray = [[NSMutableArray alloc] init];
    for (NSDictionary *dic in dataArray) {
        NSString *moduleName = dic[@"moduleName"];
        if (moduleName && ([moduleName isEqualToString:DoraemonLocalizedString(@"常用工具")] ||
                           [moduleName isEqualToString:DoraemonLocalizedString(@"性能检测")] ||
                           [moduleName isEqualToString:DoraemonLocalizedString(@"视觉工具")] ||
                           [moduleName isEqualToString:DoraemonLocalizedString(@"平台工具")] ||
                           [moduleName isEqualToString:@"Weex"])) {
            NSArray *pluginArray = dic[@"pluginArray"];
            NSMutableArray *mutablepluginArray = [[NSMutableArray alloc] init];
            for (NSDictionary *subDic in pluginArray){
                [mutablepluginArray addObject:subDic.mutableCopy];
            }
            NSMutableDictionary *mutableDic = [[NSMutableDictionary alloc] init];
            [mutableDic setValue:dic[@"moduleName"] forKey:@"moduleName"];
            [mutableDic setValue:mutablepluginArray forKey:@"pluginArray"];
            
            [mutableDataArray addObject:mutableDic];
        }

    }
    [_defaults setObject:mutableDataArray forKey:kDoraemonKitManagerKey];
    [_defaults synchronize];
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonKitManagerUpdateNotification object:nil userInfo:nil];
}

- (NSMutableArray *)kitManagerData{
    //NSUserDefaults返回的对象都是不可变的,第一步要不他们都要变成可变的
    NSArray *dataArray = [_defaults objectForKey:kDoraemonKitManagerKey];
    NSMutableArray *mutableDataArray = [[NSMutableArray alloc] init];
    for (NSDictionary *dic in dataArray) {
        NSArray *pluginArray = dic[@"pluginArray"];
        NSMutableArray *mutablepluginArray = [[NSMutableArray alloc] init];
        for (NSDictionary *subDic in pluginArray){
            [mutablepluginArray addObject:subDic.mutableCopy];
        }
        NSMutableDictionary *mutableDic = [[NSMutableDictionary alloc] init];
        [mutableDic setValue:dic[@"moduleName"] forKey:@"moduleName"];
        [mutableDic setValue:mutablepluginArray forKey:@"pluginArray"];
        
        [mutableDataArray addObject:mutableDic];
    }
    return mutableDataArray;
}

- (NSMutableArray *)kitShowManagerData{
    //NSUserDefaults返回的对象都是不可变的,第一步要不他们都要变成可变的
    NSArray *dataArray = [_defaults objectForKey:kDoraemonKitManagerKey];
    NSMutableArray *mutableDataArray = [[NSMutableArray alloc] init];
    for (NSDictionary *dic in dataArray) {
        NSArray *pluginArray = dic[@"pluginArray"];
        NSMutableArray *mutablepluginArray = [[NSMutableArray alloc] init];
        for (NSDictionary *subDic in pluginArray){
            BOOL show = [subDic[@"show"] boolValue];
            if (show) {
                [mutablepluginArray addObject:subDic.mutableCopy];
            }
        }
        NSMutableDictionary *mutableDic = [[NSMutableDictionary alloc] init];
        [mutableDic setValue:dic[@"moduleName"] forKey:@"moduleName"];
        [mutableDic setValue:mutablepluginArray forKey:@"pluginArray"];
        
        [mutableDataArray addObject:mutableDic];
    }
    return mutableDataArray;
}

//外部数据+保存数据
- (NSMutableArray *)allKitShowManagerData{
     NSMutableArray *dataArray = [DoraemonManager shareInstance].dataArray;
    NSMutableArray *mutableDataArray = [[NSMutableArray alloc] init];
    if ([self kitShowManagerData].count>0) {
        for (NSDictionary *dic in dataArray) {
            NSString *moduleName = dic[@"moduleName"];
            if (moduleName && ([moduleName isEqualToString:DoraemonLocalizedString(@"常用工具")] ||
                               [moduleName isEqualToString:DoraemonLocalizedString(@"性能检测")] ||
                               [moduleName isEqualToString:DoraemonLocalizedString(@"视觉工具")] ||
                               [moduleName isEqualToString:DoraemonLocalizedString(@"平台工具")] ||
                               [moduleName isEqualToString:@"Weex"])) {
                continue;
            }
            
            NSArray *pluginArray = dic[@"pluginArray"];
            NSMutableArray *mutablepluginArray = [[NSMutableArray alloc] init];
            for (NSDictionary *subDic in pluginArray){
                [mutablepluginArray addObject:subDic.mutableCopy];
            }
            NSMutableDictionary *mutableDic = [[NSMutableDictionary alloc] init];
            [mutableDic setValue:dic[@"moduleName"] forKey:@"moduleName"];
            [mutableDic setValue:mutablepluginArray forKey:@"pluginArray"];
            
            [mutableDataArray addObject:mutableDic];

        }
        [mutableDataArray addObjectsFromArray:[self kitShowManagerData]];
    }else{
        for (NSDictionary *dic in dataArray) {
            NSString *moduleName = dic[@"moduleName"];
            if (moduleName && ([moduleName isEqualToString:DoraemonLocalizedString(@"常用工具")] ||
                               [moduleName isEqualToString:DoraemonLocalizedString(@"性能检测")] ||
                               [moduleName isEqualToString:DoraemonLocalizedString(@"视觉工具")] ||
                               [moduleName isEqualToString:DoraemonLocalizedString(@"平台工具")] ||
                               [moduleName isEqualToString:@"Weex"])) {
                [mutableDataArray addObject:dic];
                continue;
            }
            
            NSArray *pluginArray = dic[@"pluginArray"];
            NSMutableArray *mutablepluginArray = [[NSMutableArray alloc] init];
            for (NSDictionary *subDic in pluginArray){
                [mutablepluginArray addObject:subDic.mutableCopy];
            }
            NSMutableDictionary *mutableDic = [[NSMutableDictionary alloc] init];
            [mutableDic setValue:dic[@"moduleName"] forKey:@"moduleName"];
            [mutableDic setValue:mutablepluginArray forKey:@"pluginArray"];
            if (mutableDic.allKeys.count) {
                [mutableDataArray insertObject:mutableDic atIndex:0];
            }
        }
    }
    
    return mutableDataArray;
}

@end
