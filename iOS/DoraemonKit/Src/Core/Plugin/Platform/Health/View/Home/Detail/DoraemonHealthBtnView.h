//
//  DoraemonHealthBtnView.h
//  DoraemonKit
//
//  Created by didi on 2019/12/30.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonHealthButtonDelegate<NSObject>

- (void)healthBtnClick:(id)sender;

@end

@interface DoraemonHealthBtnView : UIView

@property (nonatomic, weak) id<DoraemonHealthButtonDelegate> delegate;

- (void)statusForBtn:(BOOL)start;

@end

NS_ASSUME_NONNULL_END
