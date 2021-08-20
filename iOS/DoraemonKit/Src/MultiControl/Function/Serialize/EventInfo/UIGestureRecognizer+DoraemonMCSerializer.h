//
//  UIGestureRecognizer+DoraemonMCSerializer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

UIKIT_EXTERN NSString  const * _Nonnull kUIGestureRecognizerDoraemonMCSerializerWrapperKey ;

UIKIT_EXTERN NSString  const * _Nonnull kUIGestureRecognizerDoraemonMCSerializerIndexKey;

@interface UIGestureRecognizer (DoraemonMCSerializer)


/// 当前手势对象的信息转为字典
- (NSDictionary *)do_mc_serialize_dictionary;
/// 将字典中的值同步到当前手势对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary;

/// 继承链上的每个类都将自己需要序列化的参数加入到dict中
- (void)do_mc_serialize_setupDictionary:(NSMutableDictionary *)dictM;

/// 主机上 手势的触摸坐标
@property (nonatomic , assign) CGPoint do_mc_location_at_host;

/// 清除用于一机多控的所有关联属性
- (void)do_mc_clear_all_value_at_host;
@end

NS_ASSUME_NONNULL_END
