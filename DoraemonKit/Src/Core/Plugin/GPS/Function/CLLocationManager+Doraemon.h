//
//  CLLocationManager+Doraemon.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/7/4.
//

#import <CoreLocation/CoreLocation.h>

@interface CLLocationManager (Doraemon)

- (void)doraemon_swizzleLocationDelegate:(id)delegate;

@end
