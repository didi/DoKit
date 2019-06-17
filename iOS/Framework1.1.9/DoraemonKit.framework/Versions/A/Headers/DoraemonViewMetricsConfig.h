//
//  DoraemonViewMetricsManager.h
//  DoraemonKit
//
//  Created by xgb on 2018/12/11.
//

#import <Foundation/Foundation.h>

@interface DoraemonViewMetricsConfig : NSObject

@property (nonatomic, strong) UIColor *borderColor;     //default red
@property (nonatomic, assign) CGFloat borderWidth;      //default 1
@property (nonatomic, assign) BOOL enable;              //default NO
@property (nonatomic, assign) BOOL ignoreSystemView;    //default YES
@property (nonatomic, strong) NSArray <NSString *> *blackList;

+ (instancetype)defaultConfig;

@end

