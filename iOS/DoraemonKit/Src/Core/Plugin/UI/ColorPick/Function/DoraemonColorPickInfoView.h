//
//  DoraemonColorPickInfoView.h
//  DoraemonKit
//
//  Created by wenquan on 2018/12/3.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN
@class DoraemonColorPickInfoView;

@protocol DoraemonColorPickInfoViewDelegate <NSObject>

@optional

- (void)closeBtnClicked:(id)sender onColorPickInfoView:(DoraemonColorPickInfoView *)colorPickInfoView;

@end

@interface DoraemonColorPickInfoView : UIView

@property (nonatomic, weak) id<DoraemonColorPickInfoViewDelegate> delegate;

- (void)setCurrentColor:(NSString *)hexColor;

@end

NS_ASSUME_NONNULL_END
