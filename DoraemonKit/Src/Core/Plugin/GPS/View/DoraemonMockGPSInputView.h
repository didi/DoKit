//
//  DoraemonMockGPSInputView.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/2.
//

#import <UIKit/UIKit.h>

@protocol DoraemonMockGPSInputViewDelegate <NSObject>

- (void)inputViewOkClick:(NSString *)gps;

@end

@interface DoraemonMockGPSInputView : UIView

@property (nonatomic, weak) id<DoraemonMockGPSInputViewDelegate> delegate;

@end

