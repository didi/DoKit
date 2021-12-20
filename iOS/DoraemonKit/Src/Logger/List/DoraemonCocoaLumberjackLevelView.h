//
//  DoraemonCocoaLumberjackLevelView.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/6.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonCocoaLumberjackLevelViewDelegate<NSObject>

- (void)segmentSelected:(NSInteger)index;

@end

@interface DoraemonCocoaLumberjackLevelView : UIView

@property (nonatomic, weak) id<DoraemonCocoaLumberjackLevelViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
