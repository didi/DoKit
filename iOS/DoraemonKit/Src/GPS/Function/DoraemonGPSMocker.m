//
//  DoraemonGPSMocker.m
//  DoraemonKit
//
//  Created by yixiang on 2018/7/4.
//

#import "DoraemonGPSMocker.h"
#import "NSObject+Doraemon.h"
#import "CLLocationManager+Doraemon.h"

@interface DoraemonGPSMocker()<CLLocationManagerDelegate>

@property (nonatomic, strong) NSMapTable *locationMonitor;
@property (nonatomic,strong) CLLocation *oldLocation;
@property (nonatomic, strong) CLLocation *pointLocation;
@property (nonatomic,strong) NSTimer *simTimer;

@end

@implementation DoraemonGPSMocker

+ (void)load {
    [[DoraemonGPSMocker shareInstance] swizzleCLLocationMangagerDelegate];
}

+ (DoraemonGPSMocker *)shareInstance{
    static dispatch_once_t once;
    static DoraemonGPSMocker *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonGPSMocker alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if(self){
        _locationMonitor = [NSMapTable strongToWeakObjectsMapTable];
        _isMocking = NO;
    }
    return self;
}


- (void)addLocationBinder:(id)binder delegate:(id)delegate{
    NSString *binderKey = [NSString stringWithFormat:@"%p_binder",binder];
    NSString *delegateKey = [NSString stringWithFormat:@"%p_delegate",binder];
    [_locationMonitor setObject:binder forKey:binderKey];
    [_locationMonitor setObject:delegate forKey:delegateKey];
}

- (void)swizzleCLLocationMangagerDelegate {
    [[CLLocationManager class] doraemon_swizzleInstanceMethodWithOriginSel:@selector(setDelegate:) swizzledSel:@selector(doraemon_swizzleLocationDelegate:)];
}

-(BOOL)mockPoint:(CLLocation*)location{
    _isMocking = YES;
    self.pointLocation = location;
    if (self.simTimer) {
        [self pointMock];
    } else {
        self.simTimer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(pointMock) userInfo:nil repeats:YES];
        [self.simTimer fire];
    }
    return YES;
}

- (void)pointMock {
    CLLocation *mockLocation = [[CLLocation alloc] initWithCoordinate:CLLocationCoordinate2DMake(self.pointLocation.coordinate.latitude, self.pointLocation.coordinate.longitude) altitude:0 horizontalAccuracy:5 verticalAccuracy:5 timestamp:[NSDate date]];
    [self dispatchLocationsToAll:@[mockLocation]];
}

- (void)dispatchLocationsToAll:(NSArray*)locations{
    for (NSString *key in _locationMonitor.keyEnumerator) {
        if ([key hasSuffix:@"_binder"]) {
            NSString *binderKey = key;
            CLLocationManager *binderManager = [_locationMonitor objectForKey:binderKey];
            [self dispatchLocationUpdate:binderManager locations:locations];
        }
    }
}

- (void)stopMockPoint{
    _isMocking = NO;
    if(self.simTimer){
        [self.simTimer invalidate];
        self.simTimer = nil;
    }
}

//if manager is nil.enum all manager.
-(void)enumDelegate:(CLLocationManager*)manager block:(void (^)(id<CLLocationManagerDelegate> delegate))block{
    NSString *key = [NSString stringWithFormat:@"%p_delegate",manager];
    id<CLLocationManagerDelegate> delegate = [_locationMonitor objectForKey:key];
    if (delegate) {
        block(delegate);
    }
}

#pragma mark - CLLocationManagerDelegate
// 这个过期接口不能删掉，防止应用方实现了这个方法
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-implementations"
-(void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation{
    if (!self.isMocking){
        [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
            if ([delegate respondsToSelector:@selector(locationManager:didUpdateToLocation:fromLocation:)]) {
                #pragma clang diagnostic push
                #pragma clang diagnostic ignored "-Wdeprecated-declarations"
                [delegate locationManager:manager didUpdateToLocation:newLocation fromLocation:oldLocation];
                #pragma clang diagnostic pop
            }
        }];
    }
}
#pragma clang diagnostic pop

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations {
    if (!self.isMocking) {
        [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
            if ([delegate respondsToSelector:@selector(locationManager:didUpdateLocations:)]) {
                [delegate locationManager:manager didUpdateLocations:locations];
            }
        }];
    }
}
    
- (void)locationManager:(CLLocationManager *)manager didUpdateHeading:(CLHeading *)newHeading {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didUpdateHeading:)]) {
            [delegate locationManager:manager didUpdateHeading:newHeading];
        }
    }];
}

-(BOOL)locationManagerShouldDisplayHeadingCalibration:(CLLocationManager *)manager{
    __block BOOL ret = NO;
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManagerShouldDisplayHeadingCalibration:)]) {
            ret = [delegate locationManagerShouldDisplayHeadingCalibration:manager];
        }
    }];
    
    return ret;
}

- (void)locationManager:(CLLocationManager *)manager
      didDetermineState:(CLRegionState)state forRegion:(CLRegion *)region {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didDetermineState:forRegion:)]) {
            [delegate locationManager:manager didDetermineState:state forRegion:region];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager
        didRangeBeacons:(NSArray<CLBeacon *> *)beacons inRegion:(CLBeaconRegion *)region{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didRangeBeacons:inRegion:)]) {
            [delegate locationManager:manager didRangeBeacons:beacons inRegion:region];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager rangingBeaconsDidFailForRegion:(CLBeaconRegion *)region
              withError:(NSError *)error{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:rangingBeaconsDidFailForRegion:withError:)]) {
            [delegate locationManager:manager rangingBeaconsDidFailForRegion:region withError:error];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager didRangeBeacons:(NSArray<CLBeacon *> *)beacons satisfyingConstraint:(CLBeaconIdentityConstraint *)beaconConstraint API_AVAILABLE(ios(13.0)) {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didRangeBeacons:satisfyingConstraint:)]) {
            [delegate locationManager:manager didRangeBeacons:beacons satisfyingConstraint:beaconConstraint];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager didFailRangingBeaconsForConstraint:(CLBeaconIdentityConstraint *)beaconConstraint error:(NSError *)error API_AVAILABLE(ios(13.0)) {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didFailRangingBeaconsForConstraint:error:)]) {
            [delegate locationManager:manager didFailRangingBeaconsForConstraint:beaconConstraint error:error];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager
         didEnterRegion:(CLRegion *)region{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didEnterRegion:)]) {
            [delegate locationManager:manager didEnterRegion:region];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager
          didExitRegion:(CLRegion *)region{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didExitRegion:)]) {
            [delegate locationManager:manager didExitRegion:region];
        }
    }];
}

-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didFailWithError:)]) {
            [delegate locationManager:manager didFailWithError:error];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager
monitoringDidFailForRegion:(nullable CLRegion *)region
              withError:(NSError *)error{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:monitoringDidFailForRegion:withError:)]) {
            [delegate locationManager:manager monitoringDidFailForRegion:region withError:error];
        }
    }];
}

-(void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didChangeAuthorizationStatus:)]) {
            [delegate locationManager:manager didChangeAuthorizationStatus:status];
        }
    }];
}

-(void)locationManagerDidChangeAuthorization:(CLLocationManager *)manager {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManagerDidChangeAuthorization:)]) {
            [delegate performSelector:@selector(locationManagerDidChangeAuthorization:) withObject:manager];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager didStartMonitoringForRegion:(CLRegion *)region{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didStartMonitoringForRegion:)]) {
            [delegate locationManager:manager didStartMonitoringForRegion:region];
        }
    }];
}

- (void)locationManagerDidPauseLocationUpdates:(CLLocationManager *)manager {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManagerDidPauseLocationUpdates:)]) {
            [delegate locationManagerDidPauseLocationUpdates:manager];
        }
    }];
}

- (void)locationManagerDidResumeLocationUpdates:(CLLocationManager *)manager {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManagerDidResumeLocationUpdates:)]) {
            [delegate locationManagerDidResumeLocationUpdates:manager];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager didFinishDeferredUpdatesWithError:(nullable NSError *)error{
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didFinishDeferredUpdatesWithError:)]) {
            [delegate locationManager:manager didFinishDeferredUpdatesWithError:error];
        }
    }];
}

- (void)locationManager:(CLLocationManager *)manager didVisit:(CLVisit *)visit {
    [self enumDelegate:manager block:^(id<CLLocationManagerDelegate> delegate) {
        if ([delegate respondsToSelector:@selector(locationManager:didVisit:)]) {
            [delegate locationManager:manager didVisit:visit];
        }
    }];
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
-(void)dispatchLocationUpdate:(CLLocationManager *)manager locations:(NSArray*)locations{
    NSString *key = [NSString stringWithFormat:@"%p_delegate",manager];
    id<CLLocationManagerDelegate> delegate = [_locationMonitor objectForKey:key];
    if ([delegate respondsToSelector:@selector(locationManager:didUpdateLocations:)]) {
        [delegate locationManager:manager didUpdateLocations:locations];
    }else if ([delegate respondsToSelector:@selector(locationManager:didUpdateToLocation:fromLocation:)]){
        [delegate locationManager:manager didUpdateToLocation:locations.firstObject fromLocation:self.oldLocation];
        self.oldLocation = locations.firstObject;
    }
}
#pragma clang diagnostic pop
@end

