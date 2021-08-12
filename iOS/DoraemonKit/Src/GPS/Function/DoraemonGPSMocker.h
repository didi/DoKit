//
//  DoraemonGPSMocker.h
//  DoraemonKit
//
//  Created by yixiang on 2018/7/4.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

//参考wander
@interface DoraemonGPSMocker : NSObject

+ (DoraemonGPSMocker *)shareInstance;

- (void)addLocationBinder:(id)binder delegate:(id)delegate;

- (BOOL)mockPoint:(CLLocation*)location;

- (void)stopMockPoint;

@property (nonatomic, assign) BOOL isMocking;

@end
