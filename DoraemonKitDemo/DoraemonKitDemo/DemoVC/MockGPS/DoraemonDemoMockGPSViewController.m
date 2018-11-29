//
//  DoraemonDemoMockGPSViewController.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/7/4.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "DoraemonDemoMockGPSViewController.h"
#import <MapKit/MapKit.h>
#import "UIView+DoraemonPositioning.h"
#import "DoraemonDemoMockGPSAnnotation.h"

@interface DoraemonDemoMockGPSViewController ()<MKMapViewDelegate,CLLocationManagerDelegate>

@property (nonatomic, strong) MKMapView *mapView;
@property (nonatomic, strong) DoraemonDemoMockGPSAnnotation *annotation;

@property (nonatomic, strong) CLLocationManager *lcManager;

@end

@implementation DoraemonDemoMockGPSViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"模拟位置";
    
    //初始化地图
    MKMapView *mapView = [[MKMapView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height)];
    mapView.mapType = MKMapTypeStandard;
    mapView.delegate = self;
    mapView.showsUserLocation = YES;
    [self.view addSubview:mapView];
    self.mapView = mapView;
    
    // 2.判断是否打开了位置服务
    if([CLLocationManager locationServicesEnabled]) {
        // 创建位置管理者对象
        self.lcManager = [[CLLocationManager alloc] init];
        self.lcManager.delegate = self; // 设置代理
        // 设置定位距离过滤参数 (当本次定位和上次定位之间的距离大于或等于这个值时，调用代理方法)
        self.lcManager.distanceFilter = 100;
        self.lcManager.desiredAccuracy = kCLLocationAccuracyBest; // 设置定位精度(精度越高越耗电)
        [self.lcManager startUpdatingLocation]; // 开始更新位置
    }
}

/** 获取到新的位置信息时调用*/
-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations{
    NSLog(@"定位到了");
}
/** 不能获取位置信息时调用*/
-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSLog(@"获取定位失败");
}
///* 更新用户位置会调用 */
//- (void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation{
//    CLLocation *location = userLocation.location;
//    CLLocationCoordinate2D coordinate = location.coordinate;
//    NSLog(@"更新用户位置会调用 经度：%f，纬度：%f",coordinate.latitude,coordinate.longitude);
//}
//
//
///* 每当大头针显示在可视界面上时，就会调用该方法，用户位置的蓝色点也是个大头针，也会调用 */
//- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation
//{
//    if ([annotation isKindOfClass:[DoraemonDemoMockGPSAnnotation class]]) {
//        DoraemonDemoMockGPSAnnotation *annotationLT = (DoraemonDemoMockGPSAnnotation *)annotation;
//        //类似于UITableViewCell的重用机制，大头针视图也有重用机制
//        static NSString *key = @"AnnotationIdentifier";
//        MKAnnotationView *view = [self.mapView dequeueReusableAnnotationViewWithIdentifier:key];
//        if (!view) {
//            view = [[MKAnnotationView alloc] initWithAnnotation:annotation
//                                                reuseIdentifier:key];
//        }
//        //设置大头针数据
//        view.annotation = annotation;
//        //自定义大头针默认是NO，表示不能弹出视图，这里让大头针可以点击弹出视图
//        view.canShowCallout = NO;
//        //设置大头针图标
//        view.image = annotationLT.icon;
//        return view;
//    }
//    //返回nil，表示显示默认样式
//    return nil;
//}



@end
