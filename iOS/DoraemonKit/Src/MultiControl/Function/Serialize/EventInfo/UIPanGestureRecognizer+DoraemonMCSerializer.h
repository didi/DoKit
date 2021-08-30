//
//  UIPanGestureRecognizer+DoraemonMCSerializer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIPanGestureRecognizer (DoraemonMCSerializer)

/// 主机上 平移手势的偏移距离
@property (nonatomic , assign) CGPoint do_mc_translation_at_host;
/// 主机上平移手势的加速度
@property (nonatomic , assign) CGPoint do_mc_vol_at_host;

@end

NS_ASSUME_NONNULL_END
