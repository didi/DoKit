//
//  DoraemonViewMetricsManager.h
//  DoraemonKit
//
//  Created by xgb on 2018/12/11.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface DoraemonViewMetricsConfig : NSObject

@property (nonatomic, strong) UIColor *borderColor;     //default randomColor
@property (nonatomic, assign) CGFloat borderWidth;      //default 1
@property (nonatomic, assign) BOOL enable;              //default NO

+ (instancetype)defaultConfig;

@end

