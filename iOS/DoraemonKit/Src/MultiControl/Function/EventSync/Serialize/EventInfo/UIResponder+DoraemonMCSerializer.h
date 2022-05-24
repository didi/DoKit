//
//  UIResponder+DoraemonMCSerializer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

UIKIT_EXTERN NSString const *kUIResponderDoraemonMCSerializerWrapperKey;

@interface UIResponder (DoraemonMCSerializer)

/// 当前对象的信息转为字典
- (NSDictionary *)do_mc_serialize_dictionary;

/// 将字典中的值同步到当前对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary;

@end

NS_ASSUME_NONNULL_END
