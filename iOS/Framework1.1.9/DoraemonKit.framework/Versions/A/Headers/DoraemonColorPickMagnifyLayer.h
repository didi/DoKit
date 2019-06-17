//
//  DoraemonColorPickMagnifyLayer.h
//  DoraemonKit
//
//  Created by wenquan on 2019/1/31.
//

#import <QuartzCore/QuartzCore.h>

typedef NSString* (^DoraemonColorPickMagnifyLayerPointColorBlock) (CGPoint);

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonColorPickMagnifyLayer : CALayer

/**
 获取指定点的颜色值
 */
@property (nonatomic, copy) DoraemonColorPickMagnifyLayerPointColorBlock pointColorBlock;

/**
 目标视图展示位置
 */
@property (nonatomic, assign) CGPoint targetPoint;

@end

NS_ASSUME_NONNULL_END
