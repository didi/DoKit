//
//  DoraemonMessagePackager.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/// 事件消息类型
typedef NS_ENUM(NSInteger , DoraemonMCMessageType) {
    /// 基于手势识别器的
    DoraemonMCMessageTypeGuesture,
    /// 基于UIControl的
    DoraemonMCMessageTypeControl,
    /// 基于复用列表项被点击的
    DoraemonMCMessageTypeDidSelectCell,
    DoraemonMCMessageTypeDidScrollToCell,
    /// 基于文本输入
    DoraemonMCMessageTypeTextInput,
    /// 底部tabbar选中
    DoraemonMCMessageTypeTarbarSelected
};

@interface DoraemonMCMessage : NSObject

// 消息类型
@property (nonatomic , assign) DoraemonMCMessageType type;
// 自定义消息类型
@property (nonatomic , assign) NSString * customType;
// 控件的xPath
@property (nonatomic , copy  ) NSString *xPath;

// 当前消息对应的事件信息
@property (nonatomic , copy  ) NSDictionary *eventInfo;

// 当前所在的页面类名
@property (nonatomic , copy  ) NSString *currentVCClassName;

@property (nonatomic , assign) BOOL isFirstResponder;
- (NSString *)toMessageString;

@end

@interface DoraemonMCMessagePackager : NSObject

/**
 根据类型,xPath和事件信息组装消息字符串,用于发送到网络
 */
+ (DoraemonMCMessage *)packageMessageWithView:(UIView *)view
                             gusture:(UIGestureRecognizer *)gusture
                              action:(SEL)action
                           indexPath:(NSIndexPath *)indexPath
                        messageType:(DoraemonMCMessageType)type;

/**
 自定义事件
 */
+ (DoraemonMCMessage *)packageCustomMessageWithView:(UIView *)view
                                          eventInfo:(NSDictionary *)eventInfo
                                        messageType:(NSString *)type;

/***
 根据从网络上获取的消息字符串, 解析出消息对象
 */
+ (DoraemonMCMessage *)parseMessageString:(NSString *)messageString;

@end

NS_ASSUME_NONNULL_END
