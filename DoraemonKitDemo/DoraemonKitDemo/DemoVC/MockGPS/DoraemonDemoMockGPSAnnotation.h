//
//  DoraemonDemoMockGPSAnnotation.h
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/7/4.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

@interface DoraemonDemoMockGPSAnnotation : NSObject<MKAnnotation>

/* 必须创建的属性 */
@property (nonatomic) CLLocationCoordinate2D coordinate;
/* 可选的属性 */
@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *subtitle;
/* 自定义的属性 */
@property (nonatomic, strong) UIImage *icon;


@end
