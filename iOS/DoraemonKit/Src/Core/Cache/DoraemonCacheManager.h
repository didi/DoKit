//
//  DoraemonCacheManager.h
//  DoraemonKit
//
//  Created by yixiang on 2017/12/12.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

@interface DoraemonCacheManager : NSObject

+ (DoraemonCacheManager *)sharedInstance;

- (void)saveLoggerSwitch:(BOOL)on;

- (BOOL)loggerSwitch;

- (void)saveMockGPSSwitch:(BOOL)on;

- (BOOL)mockGPSSwitch;

- (void)saveMockCoordinate:(CLLocationCoordinate2D)coordinate;

- (CLLocationCoordinate2D)mockCoordinate;

- (void)saveFpsSwitch:(BOOL)on;

- (BOOL)fpsSwitch;

- (void)saveCpuSwitch:(BOOL)on;

- (BOOL)cpuSwitch;

- (void)saveMemorySwitch:(BOOL)on;

- (BOOL)memorySwitch;

- (void)saveNetFlowSwitch:(BOOL)on;

- (BOOL)netFlowSwitch;

- (void)saveAllTestSwitch:(BOOL)on;

- (BOOL)allTestSwitch;

- (void)saveLargeImageDetectionSwitch:(BOOL)on;

- (BOOL)largeImageDetectionSwitch;

- (void)saveSubThreadUICheckSwitch:(BOOL)on;

- (BOOL)subThreadUICheckSwitch;

- (void)saveCrashSwitch:(BOOL)on;

- (BOOL)crashSwitch;

- (void)saveNSLogSwitch:(BOOL)on;

- (BOOL)nsLogSwitch;

- (void)saveMethodUseTimeSwitch:(BOOL)on;

- (BOOL)methodUseTimeSwitch;

- (void)saveStartTimeSwitch:(BOOL)on;

- (BOOL)startTimeSwitch;

- (void)saveANRTrackSwitch:(BOOL)on;

- (BOOL)anrTrackSwitch;

/// 历史记录
- (NSArray<NSString *> *)h5historicalRecord;
- (void)saveH5historicalRecordWithText:(NSString *)text;
- (void)clearAllH5historicalRecord;
- (void)clearH5historicalRecordWithText:(NSString *)text;

/// JS历史脚本
- (NSArray<NSDictionary *> *)jsHistoricalRecord;
- (NSString *)jsHistoricalRecordForKey:(NSString *)key;
- (void)saveJsHistoricalRecordWithText:(NSString *)text forKey:(NSString *)key;
- (void)clearJsHistoricalRecordWithKey:(NSString *)key;

/// 保存启动类
- (void)saveStartClass : (NSString *)startClass;
- (NSString *)startClass;

// 内存泄漏开关
- (void)saveMemoryLeak:(BOOL)on;
- (BOOL)memoryLeak;

// 内存泄漏弹框开关
- (void)saveMemoryLeakAlert:(BOOL)on;
- (BOOL)memoryLeakAlert;

// mockapi本地缓存情况
- (void)saveMockCache:(NSArray *)mocks;
- (NSArray *)mockCahce;

// 健康体检开关
- (void)saveHealthStart:(BOOL)on;
- (BOOL)healthStart;

// Kit Manager数据保存
- (void)saveKitManagerData:(NSMutableArray *)dataArray;
- (NSMutableArray *)kitManagerData;
- (NSMutableArray *)kitShowManagerData;
- (NSMutableArray *)allKitShowManagerData;
@end
