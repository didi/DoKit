//
//  DoraemonVisualInfoWindow.h
//  DoraemonKit
//
//  Created by wenquan on 2018/12/5.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonVisualInfoWindow : UIWindow
/** 显示的文本 */
@property (nonatomic, copy) NSString *infoText;
/** 显示的属性文本 */
@property (nonatomic, copy) NSAttributedString *infoAttributedText;
@end

NS_ASSUME_NONNULL_END
